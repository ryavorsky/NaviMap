package com.navimap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.gms.maps.model.LatLng;
import com.navimap.adapter.AddressAdapter;
import com.navimap.db.NaviMapDatabaseHelper;
import com.navimap.db.dao.NaviMapPointDao;
import com.navimap.db.model.NaviMapPoint;
import com.navimap.utils.LogUtils;
import com.navimap.utils.MapUtils;
import com.navimap.utils.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PickupActivity extends Activity implements AddressAdapter.ViewHolder.IMyViewHolderClicks {
    public static final String EXTRA_CITY_CODE = "extra_city_code";
    private String cityCode = null;
    private static final int SPEECH_REQUEST_CODE = 0;
    private EditText inputEditText;
    private ImageView voiceInputButton;
    private RecyclerView addressList;
    private AddressAdapter addressAdapter;
    private RecyclerView.LayoutManager addressLayoutManager;
    private ProgressBar progressBar;
    private Geocoder geocoder;
    private SearchTask searchTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras!=null)
            cityCode = extras.getString(EXTRA_CITY_CODE);
        initView();
        initListeners();
        geocoder = new Geocoder(this);
        if (searchTask != null)
            searchTask.cancel(true);
        searchTask = new SearchTask();
        searchTask.execute("");
    }

    private void initView() {
        setContentView(R.layout.pickup_menu);
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        voiceInputButton = (ImageView) findViewById(R.id.voiceInputButton);
        voiceInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        addressList = (RecyclerView) findViewById(R.id.addressList);
        addressList.setHasFixedSize(true);
        addressLayoutManager = new LinearLayoutManager(this);
        addressList.setLayoutManager(addressLayoutManager);
    }

    private void initListeners() {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchTask != null)
                    searchTask.cancel(true);
                searchTask = new SearchTask();
                searchTask.execute(s.toString());
            }
        });
    }

    private void searchLocation() {
        InputMethodManager imm = (InputMethodManager) PickupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PickupActivity.this.getCurrentFocus().getWindowToken(), 0);
        String query = inputEditText.getText().toString();

        new AsyncTask() {


            @Override
            protected Object doInBackground(Object[] params) {
                String query = (String) params[0];
                LatLng latLng = null;
                if (query.matches("^[0-9 ]+")) {
                    query = query.replace(" ", "");
                    query = query.substring(0,4)+" "+query.substring(4,query.length());
                    if (query.length() == 8) {
                        latLng = MapUtils.GetLatLngNavi8("(" + cityCode + ") " + query);
                    } else if (query.length() == 6) {
                        latLng = MapUtils.GetLatLngNavi6(cityCode, Integer.parseInt(query));
                    }
                } else {
                    try {
                        Geocoder geocoder = new Geocoder(PickupActivity.this);
                        List<Address> addresses = geocoder.getFromLocationName(query, 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        }
                    } catch (IOException e) {
                        //TODO: log error
                    }
                }
                return latLng;
            }

            @Override
            protected void onPostExecute(Object result) {
                Intent resultIntent = new Intent();
                if (result instanceof LatLng) {
                    LatLng latLng = (LatLng) result;
                    resultIntent.putExtra("lat", latLng.latitude);
                    resultIntent.putExtra("lng", latLng.longitude);
                    setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
                } else {
                    setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
                }
                finish();
            }
        }.execute(query);

    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            inputEditText.setText(spokenText);
            inputEditText.setSelection(spokenText.length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AddressDTO addressDTO) {
        Intent resultIntent = new Intent();
        if (addressDTO != null) {
            LatLng latLng = addressDTO.getLatLng();

            resultIntent.putExtra("lat", latLng.latitude);
            resultIntent.putExtra("lng", latLng.longitude);
            setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
            try {
                NaviMapPointDao naviMapPointDao = NaviMapDatabaseHelper.getInstance(this).getNaviMapPointDao();
                NaviMapPoint naviMapPoint = naviMapPointDao.getNaviMapPointByAddressName(addressDTO.addressName);
                if (naviMapPoint == null) {
                    naviMapPoint = new NaviMapPoint(latLng.latitude, latLng.longitude, addressDTO.getNaviAddress(), addressDTO.addressName, false);
                    NaviMapDatabaseHelper.getInstance(this).getNaviMapPointDao().createOrUpdate(naviMapPoint);
                }
            } catch (SQLException e) {
                LogUtils.e(e);
            }
        } else {
            setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
        }
        finish();
    }

    @Override
    public void onFavoriteClick(AddressDTO addressDTO) {
        if (addressDTO != null) {
            try {
                NaviMapPointDao naviMapPointDao = NaviMapDatabaseHelper.getInstance(this).getNaviMapPointDao();
                NaviMapPoint naviMapPoint = naviMapPointDao.getNaviMapPointByAddressName(addressDTO.addressName);
                if (naviMapPoint != null) {
                    naviMapPoint.setFavorite(!addressDTO.isFavorite());
                    naviMapPointDao.update(naviMapPoint);
                } else {
                    naviMapPoint = new NaviMapPoint(addressDTO.latLng.latitude, addressDTO.latLng.longitude, addressDTO.getNaviAddress(), addressDTO.addressName, !addressDTO.isFavorite());
                    naviMapPointDao.create(naviMapPoint);
                }
                addressDTO.setIsSaved(true);
                addressDTO.setIsFavorite(!addressDTO.isFavorite());
                addressAdapter.updateItem(addressDTO);
            } catch (SQLException e) {
                LogUtils.e(e);
            }
        }
    }

    private class SearchTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String query = (String) params[0];
            AddressDTO naviMapAddress=null;
            List<Address> addresses = new ArrayList<Address>();
            List<AddressDTO> savedAddresses = new ArrayList<AddressDTO>();

            if (!StringUtils.isNullOrEmpty(query)) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }
                try {
                    if (query.matches("^[0-9 ]+")) {
                        query = query.replace(" ", "");
                        LatLng latLng = null;
                        if (query.length() == 8) {
                            String naviCode ="("+cityCode.replaceFirst("0", "+")+") "+query.substring(0,4)+" "+query.substring(4,query.length());
                            latLng = MapUtils.GetLatLngNavi8(naviCode);
                            if (latLng != null) {
                                List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                naviMapAddress = new AddressDTO();
                                naviMapAddress.setNaviAddress(naviCode);
                                naviMapAddress.setLatLng(latLng);
                                if (list.size()>0)
                                    naviMapAddress.setAddressName(StringUtils.addressToString(list.get(0)));
                            }
                        }

                    }
                    addresses = geocoder.getFromLocationName(query, 20);
                } catch (IOException e) {
                    LogUtils.e(e);
                    return e;
                }
            }
            try {
                List<NaviMapPoint> points = NaviMapDatabaseHelper.getInstance(PickupActivity.this).getNaviMapPointDao().getNaviMapPoints("%" + query + "%");
                for (NaviMapPoint point : points) {
                    savedAddresses.add(new AddressDTO(point));
                }
            } catch (SQLException e) {
                LogUtils.e(e);
            }


            return new Object[]{savedAddresses, naviMapAddress, addresses};

        }

        @Override
        protected void onPostExecute(Object result) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            if (result instanceof Exception) {
                new AlertDialog.Builder(PickupActivity.this).setMessage(R.string.error_receiving_addresses).show();
            } else if (result instanceof Object[]) {
                Object[] data = (Object[]) result;
                List<AddressDTO> addresses = (List<AddressDTO>) data[0];
                AddressDTO naviAddress = (AddressDTO) data[1];
                List<Address> googleList = (List<Address>) data[2];
                if (naviAddress!=null)
                    addresses.add(0, naviAddress);
                for (Address address : googleList) {
                    addresses.add(new AddressDTO(address));
                }
                LatLng myLatLng= null;
                for (MapUtils.City city : MapUtils.City.values())
                    if (city.getNaviCode().equals(cityCode)) {
                        myLatLng = city.getLatLng();
                    }
                addressAdapter = new AddressAdapter(addresses, PickupActivity.this, myLatLng);
                addressList.setAdapter(addressAdapter);
            }
        }

    }

    public class AddressDTO {
        private LatLng latLng;
        private String naviAddress;
        private String addressName;
        private boolean isSaved = false;
        private boolean isFavorite = false;

        public AddressDTO() {
        }

        public AddressDTO(Address address) {
            this.latLng = new LatLng(address.getLatitude(), address.getLongitude());
            this.addressName = StringUtils.addressToString(address);
            this.naviAddress = MapUtils.getNavi8(new LatLng(address.getLatitude(), address.getLongitude()));
        }

        public AddressDTO(NaviMapPoint naviMapPoint) {
            this.latLng = new LatLng(naviMapPoint.getLatitude(), naviMapPoint.getLongitude());
            this.addressName = naviMapPoint.getAddress();
            this.naviAddress = naviMapPoint.getNaviAddress();
            this.isSaved = true;
            this.isFavorite = naviMapPoint.isFavorite();
        }

        public AddressDTO(LatLng latLng, String naviAddress, String addressName) {
            this.latLng = latLng;
            this.naviAddress = naviAddress;
            this.addressName = addressName;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        public String getNaviAddress() {
            return naviAddress;
        }

        public void setNaviAddress(String naviAddress) {
            this.naviAddress = naviAddress;
        }

        public String getAddressName() {
            return addressName;
        }

        public void setAddressName(String addressName) {
            this.addressName = addressName;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(boolean isFavorite) {
            this.isFavorite = isFavorite;
        }

        public boolean isSaved() {
            return isSaved;
        }

        public void setIsSaved(boolean isSaved) {
            this.isSaved = isSaved;
        }
    }
}
