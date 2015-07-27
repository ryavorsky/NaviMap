package com.navimap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.navimap.utils.MapUtils;
import com.navimap.utils.NaviMapUtils;
import com.navimap.utils.NaviSupport;
import com.navimap.utils.StringUtils;

public class MainMenu extends ActionBarActivity implements OnMapReadyCallback {
    public static final String EXTRA_URL = "extra_url";

    public static final int start_zoom = 17;
    public static final int PICKUP_ACTIVITY_OK = 2001;
    public static final int PICKUP_ACTIVITY_ERROR = 2002;

    private static final int PICKUP_ACTIVITY_REQUEST_CODE = 1001;
    private static volatile int num_request = 0;
    private GoogleMap map;
    private LatLng LastLatLng;
    private boolean location_found = false;
    private LinearLayout ll_pickup;
    private TextView tv_addrPost;
    private TextView tv_addrNavi;
    private String url = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            url = extras.getString(EXTRA_URL);

        setContentView(R.layout.main_menu);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ll_pickup = (LinearLayout) findViewById(R.id.enterLocation);
        tv_addrPost = (TextView) findViewById(R.id.tv_post);
        tv_addrNavi = (TextView) findViewById(R.id.tv_navi);

        //���������� ��������� ������ ����� ��������
        ((RelativeLayout) findViewById(R.id.layout_map)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.layout_start)).setVisibility(View.VISIBLE);
        getSupportActionBar().hide();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            //���������� ����������� ������ My Location �� ���� ����� ����-�����
            View btnMyLocation = ((View) mapFragment.getView().findViewById(1)
                    .getParent()).findViewById(2);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80); // size of button in dp
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(20, 0, 0, 20);
            btnMyLocation.setLayoutParams(params);
        } catch (Exception e) {
        }


        ll_pickup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainMenu.this, PickupActivity.class);
                Location location = map.getMyLocation();
                if (location != null) {
                    MapUtils.City city = MapUtils.getNearestCity(new LatLng(location.getLatitude(), location.getLongitude()));
                    intent.putExtra(PickupActivity.EXTRA_CITY_CODE, city.getNaviCode());
                }
                startActivityForResult(intent, PICKUP_ACTIVITY_REQUEST_CODE);
            }
        });

        new NoLocationFoundTask().execute();

    }

    ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, NaviSupport.getLink(tv_addrNavi.getText().toString()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap _map) {

        map = _map;
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);


        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                if (LastLatLng == null) {
                    //������ ��� ����� ���� ��������������
                    location_found = true;

                    if (!StringUtils.isNullOrEmpty(url)) {
                        String[] data = url.split("\\.");
                        String query = "(" + data[0] + ") " + data[1].substring(0, 4) + " " + data[1].substring(4, 8);
                        LastLatLng = NaviSupport.GetLatLngNavi8(query);
                    } else {
                        LastLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    }
                    CameraUpdate start_pos = CameraUpdateFactory.newLatLngZoom(LastLatLng, start_zoom);
                    map.animateCamera(start_pos);
                    tv_addrNavi.setText(NaviSupport.getNavi8Code(LastLatLng));

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //���������� �������� ���� ������ ����������
                    ((RelativeLayout) findViewById(R.id.layout_map)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.layout_start)).setVisibility(View.GONE);
                    getSupportActionBar().show();

                } else {
                    LastLatLng = null;
                    LastLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                }
            }
        });

        map.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                tv_addrNavi.setText(NaviSupport.getNavi8Code(cameraPosition.target));

                //����������� �������� ����� � �����
                new GetPostAddr().execute(new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude));
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICKUP_ACTIVITY_REQUEST_CODE): {
                if (resultCode == PICKUP_ACTIVITY_OK) {
                    try {
                        CameraUpdate start_pos = CameraUpdateFactory.newLatLngZoom(new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lng", 0)), start_zoom);
                        map.animateCamera(start_pos);
                    } catch (Exception e) {
                        //���� �� �������, ���� ����� �� ����������������
                        Toast.makeText(this, data.getDoubleExtra("lat", 0) + ", " + data.getDoubleExtra("lng", 0), Toast.LENGTH_LONG).show();
                    }
                } else if (resultCode == PICKUP_ACTIVITY_ERROR) {
                    Toast.makeText(this, "Incorrect address", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private class NoLocationFoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (!location_found) {
                ((TextView) findViewById(R.id.NoLocationFound)).setVisibility(View.VISIBLE);
            }
        }
    }

    private class GetPostAddr extends AsyncTask<LatLng, Void, String> {
        private int k;

        @Override
        protected String doInBackground(LatLng... params) {
            k = (++num_request);
            return NaviMapUtils.getAddrByLatLng(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //���������, ��� ���� ������ ��� ���������. ����� �� ����� ��������� �����
            if (k == num_request) {
                if (result != null) tv_addrPost.setText(result);
                else tv_addrPost.setText(" ");
            }
        }
    }

}
