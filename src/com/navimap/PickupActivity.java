package com.navimap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.navimap.testfairy.R;

import java.io.IOException;
import java.util.List;

public class PickupActivity extends Activity {

    private static final String city_code = "7495";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_menu);

        findViewById(R.id.SearchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
            }
        });
        findViewById(R.id.voiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });
        ((EditText) findViewById(R.id.editText1)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocation();
                    return true;
                }
                return false;
            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //getActionBar().hide();
    }

    private void searchLocation() {
        InputMethodManager imm = (InputMethodManager) PickupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PickupActivity.this.getCurrentFocus().getWindowToken(), 0);
        String query = ((EditText) findViewById(R.id.editText1)).getText().toString();

        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String query = (String) params[0];
                LatLng latLng = null;
                if (query.matches("^[0-9 ]+")) {
                    query = query.replace(" ", "");
                    if (query.length() == 8) {
                        latLng = NaviSupport.GetLatLngNavi8(Integer.parseInt(query));
                    } else if (query.length() == 6) {
                        latLng = NaviSupport.GetLatLngNavi6(city_code, Integer.parseInt(query));
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

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
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
            // Do something with spokenText
            EditText editText = ((EditText) findViewById(R.id.editText1));
            editText.setText(spokenText);
            editText.setSelection(spokenText.length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
