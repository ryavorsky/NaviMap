package com.example.navimap;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.navimap.utils.NaviMapUtils;
import com.navimap.utils.NaviSupport;

public class PickupActivity extends Activity {
	
	private static final String city_code = "7495";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.pickup_menu);
	    
	    ((ImageView)findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener() {
			
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
	        	LatLng res = NaviMapUtils.getLatLng(request, "locality:������");
        			
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
	    
	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	    
	    //getActionBar().hide();
	}

}
