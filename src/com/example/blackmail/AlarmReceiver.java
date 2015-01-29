package com.example.blackmail;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.blackmail.DBObjects.Motivation;
import com.example.blackmail.DBObjects.Motivation_Schedule;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String ACTION = "com.example.blackmail.ALARM_TRIGGER_CHECK";

	public static boolean LocationCheckInProgress = false;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION)) {

			// Send Notification

			DBWrapper dbw = new DBWrapper(context);
			Motivation_Schedule motivationForNotification = new DBObjects().new Motivation_Schedule();
			motivationForNotification = dbw.Get_Next_Motivation_Pending();

			Motivation motivationDetails = new DBObjects().new Motivation();
			motivationDetails = dbw
					.Get_Motivation_Data(motivationForNotification.Motivation_Id);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.notification_icon)
					.setContentTitle("Blackmail")
					.setContentText(
							motivationDetails.MotivationText + " due shortly.");

			NotificationManager mManager = (NotificationManager) context
					.getApplicationContext()
					.getSystemService(
							context.getApplicationContext().NOTIFICATION_SERVICE);

			mManager.cancelAll();
			mManager.notify(motivationForNotification.Motivation_Id,
					mBuilder.build());

			// Start location check
			BlackmailCheck check = new BlackmailCheck();
			if (LocationCheckInProgress == false) {
					LocationCheckInProgress = true;
					//check.onRecieve(context, intent);
					check.startService(new Intent(context.getApplicationContext(), BlackmailCheck.class));
					LocationCheckInProgress = false;
			} else {
				check.RefreshList();
			}
		}
	}
}
