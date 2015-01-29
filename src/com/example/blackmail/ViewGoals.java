package com.example.blackmail;

import java.util.ArrayList;
import java.util.List;

import com.example.blackmail.DBObjects.Blackmail;
import com.example.blackmail.DBObjects.GPS_Location;
import com.example.blackmail.DBObjects.Motivation;
import com.example.blackmail.DBObjects.Motivation_Schedule;
import com.example.blackmail.DBObjects.Notification_LocationCheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class ViewGoals extends Activity  implements OnClickListener {
	
	private List<Notification_LocationCheck> list;
	private DBWrapper dbw;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_goals);
		//Will initialize the database if it doesnt exist.
		dbw = new DBWrapper(this);
		ArrayList<Button> buttons = new ArrayList<Button>();
		LinearLayout lyt = (LinearLayout) findViewById(R.id.buttonScroll);
		
		list = dbw.Get_Pending_Goals_To_Check();
		for(int i=0; i<list.size(); i++){
			Motivation m = list.get(i).MotivationData;
			Button b = new Button(this);
			b.setOnClickListener(this);
			b.setText("#" + (i+1)+ ": " + m.MotivationText);
			lyt.addView(b);
		}
		
		if(list.size() == 0){
			TextView tv = new TextView(this);
			tv.setText("No goals to show.");
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(tvlp);
			lyt.addView(tv);
		}
		
		
	}

	@Override
	public void onClick(View v) {
		Button but = (Button) v;
		String[] tok = but.getText().toString().split("[#:]");
		int loc = Integer.parseInt(tok[1]);
		
		Notification_LocationCheck thisInfo = list.get(loc-1);
		Intent in = new Intent(this, GoalInfo.class);
		//Start unwrapping
		Motivation m = thisInfo.MotivationData;
		GPS_Location g = thisInfo.GPSLocationData;
		Motivation_Schedule ms = thisInfo.MotivationSchedule;
		//Get blackmail
		Blackmail bm = dbw.Get_Blackmail_Data(m.Motivation_Id);
		
		Bundle b = new Bundle();
		b.putInt("getMapDataHere", loc);
		b.putString("createdDate", m.CreatedDate.toString());
		b.putString("startDate", m.MotivationStartDate.toString());
		b.putString("endDate", m.MotivationEndDate.toString());
		b.putString("timeToCheck", m.MotivationTime.toString());
		b.putString("beHere", g.Check?"Yes":"No");
		b.putDouble("lat", g.Latitude);
		b.putDouble("long", g.Longitude);
		b.putString("goal", ""+m.NoOfOccurrencesPending);
		b.putString("achieved", ""+(m.NoOfOccurrencesTotal - m.NoOfOccurrencesPending));
		b.putString("remaining", ""+m.NoOfOccurrencesPending);
		b.putString("backouts", ""+bm.NoOfBackoutsPending);
		b.putString("platform", dbw.GetAPITypeID(DBWrapper.APIName_Facebook)==1?"Facebook":"Twitter");
		in.putExtra("b", b);
		startActivity(in);
	}
}
