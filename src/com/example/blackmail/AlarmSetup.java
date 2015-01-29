package com.example.blackmail;

import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.sql.Time;

import com.example.blackmail.DBObjects.Motivation_Schedule;

public class AlarmSetup {

	public static final String ACTION = "com.example.blackmail.ALARM_TRIGGER_CHECK";
	
	public void Set_Alarm_For_Schedule(Context context, Motivation_Schedule data)
	{
		long scheduleTimeInMillisEST = data.ForTime.getTime(); // EST milliseconds
		Time t = new Time(scheduleTimeInMillisEST); // EST to GMT time
		long scheduleTimeInMillis = t.getTime(); // GMT milliseconds
		
		Intent i = new Intent(ACTION);
		AlarmManager am = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		
		//RTC_WAKEUP requires GMT milliseconds
		am.set(AlarmManager.RTC_WAKEUP,scheduleTimeInMillis,pi);
	}
}
