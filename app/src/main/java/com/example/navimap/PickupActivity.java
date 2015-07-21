package com.example.navimap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.navimap.utils.LogUtils;

public class PickupActivity extends Activity {
    private static final String city_code = "7495";

    private EditText inputEditText;
    private ImageView voiceInputButton;
    private RecyclerView addressList;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListeners();


    }

    private void initView() {
        setContentView(R.layout.pickup_menu);
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        voiceInputButton = (ImageView) findViewById(R.id.voiceInputButton);
        addressList = (RecyclerView) findViewById(R.id.addressList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initListeners() {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new SearchTask().execute(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                LogUtils.e(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
        }

    }


   /* ((ImageView) findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            InputMethodManager imm = (InputMethodManager) PickupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(PickupActivity.this.getCurrentFocus().getWindowToken(), 0);

            String query = ((EditText) findViewById(R.id.editText1)).getText().toString();
            if (query.length() == 8) {
                try {
                    LatLng d = NaviSupport.GetLatLngNavi8(Integer.parseInt(query));

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("lat", d.latitude);
                    resultIntent.putExtra("lng", d.longitude);
                    setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);

                    finish();
                    return;
                } catch (Exception e) {
                }
            } else if (query.length() == 6) {
                try {
                    LatLng d = NaviSupport.GetLatLngNavi6(city_code, Integer.parseInt(query));

                    Intent resultIntent = new Intent();
                    if (d != null) {
                        resultIntent.putExtra("lat", d.latitude);
                        resultIntent.putExtra("lng", d.longitude);
                        setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
                    } else {
                        setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
                    }

                    finish();
                    return;
                } catch (Exception e) {
                }
            }

            String request = query;
            LatLng res = NaviMapUtils.getLatLng(request, "locality:������");

            Intent resultIntent = new Intent();
            if (res != null) {
                resultIntent.putExtra("lat", res.latitude);
                resultIntent.putExtra("lng", res.longitude);
                setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
            } else {
                setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
            }
            finish();
        }
    });

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/
}
