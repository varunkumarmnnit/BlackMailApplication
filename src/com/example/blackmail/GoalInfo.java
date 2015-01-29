package com.example.blackmail;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoalInfo extends FragmentActivity {
		
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.goal_info);
			
			//contains all of the info we need to display
			Bundle extras = getIntent().getExtras().getBundle("b");
		
			String[] t = new String[10];
			String ab;
			t[0] = "Created Date: " + extras.getString("createdDate");
			t[1] = "Start Date: " + extras.getString("startDate");
			t[2] = "End Date: " + extras.getString("endDate");
			t[3] = "Time to check: " + extras.getString("timeToCheck");
			t[4] = "Be present: " + extras.getString("beHere");
			
			ab = extras.getString("beHere").equals("No")?"Absences":"Attendances";
			
			t[5] = ab + " desired: " + extras.getString("goal");
			t[6] = ab + " achieved: " + extras.getString("achieved");
			t[7] = ab + " remaining: " + extras.getString("remaining");
			t[8] = "Backouts left: " + extras.getString("backouts");
			t[9] = "Platform: " + extras.getString("platform");
			
			LinearLayout lyt = (LinearLayout) findViewById(R.id.goalInfo);
			for(int i=0; i<10; i++){
				TextView tv = new TextView(this);
				tv.setText(t[i]);
				LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(tvlp);
				lyt.addView(tv);
			}
			
			//Get map from support fragment
			SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.goalInfomap);
			GoogleMap map = mapFrag.getMap();
			if(map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID); //Changes how map looks
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(extras.getDouble("lat"), extras.getDouble("long")), 16f));
				map.addMarker(new MarkerOptions().position(new LatLng(extras.getDouble("lat"), extras.getDouble("long"))).title("Goal Location")
    					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}
		}
}
