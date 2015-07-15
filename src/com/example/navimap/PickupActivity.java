package com.example.navimap;

import android.speech.RecognizerIntent;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

public class PickupActivity extends Activity {
	
	private static final String city_code = "7495";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.pickup_menu);
	    
	    /*((ImageView)findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) PickupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(PickupActivity.this.getCurrentFocus().getWindowToken(), 0);
				
				// TODO Auto-generated method stub
				String query = ((EditText)findViewById(R.id.editText1)).getText().toString();
				if(query.length()==8){
	        		try{
	        			LatLng d = NaviSupport.GetLatLngNavi8(Integer.parseInt(query));
	        			
	        			Intent resultIntent = new Intent();
	        			resultIntent.putExtra("lat", d.latitude);
	        			resultIntent.putExtra("lng", d.longitude);
	        			setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
	        			
	        			finish();
	        			return;
	        		} catch(Exception e){}
	        	} else if(query.length()==6){
	        		try{
	        			LatLng d = NaviSupport.GetLatLngNavi6(city_code, Integer.parseInt(query));
	        			
	        			Intent resultIntent = new Intent();
	        			if(d!=null){
	        				resultIntent.putExtra("lat", d.latitude);
		        			resultIntent.putExtra("lng", d.longitude);
		        			setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
	            		} else {
	            			setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
	            		}
	        			
	        			finish();
	        			return;
	        		} catch(Exception e){}
	        	}
				
	        	String request = query;
	        	LatLng res = NaviMapServices.getLatLng(request, "locality:������");
        			
        		Intent resultIntent = new Intent();
        		if(res!=null){
        			resultIntent.putExtra("lat", res.latitude);
            		resultIntent.putExtra("lng", res.longitude);
            		setResult(MainMenu.PICKUP_ACTIVITY_OK, resultIntent);
        		} else {
        			setResult(MainMenu.PICKUP_ACTIVITY_ERROR, resultIntent);
        		}
        		finish();
			}
		});
	    */
        ((ImageView)findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });
	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	    
	    //getActionBar().hide();
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
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            ((EditText)findViewById(R.id.editText1)).setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
