package com.example.blackmail;

/*
 * 
 * Code for checking pending goals against the current location of the user.
 * Updates the database accordingly. 
 * 
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.blackmail.DBObjects.GPS_Location;
import com.example.blackmail.DBObjects.Motivation;
import com.example.blackmail.DBObjects.Motivation_Schedule;
import com.example.blackmail.DBObjects.Notification_LocationCheck;
import com.google.android.gms.location.LocationClient;

//If this code does not work (it should), we can use Geofences, but that would increase the database complexity considerably
public class BlackmailCheck extends IntentService implements
		LocationListener {

	private LocationClient lc;
	private LocationManager lm;
	private DBWrapper dbw;
	private int LOCATION_THRESHOLD = 200; // how close, in meters, you have to
											// be to the goal location
	private boolean doingDemo = true;
	private Context con;
	private Location loc;

	// maintain a single instance of the list so that it can be refreshed.
	public static ArrayList<Notification_LocationCheck> checkThese = new ArrayList<Notification_LocationCheck>();

	public BlackmailCheck(){
		super("BlackmailCheck");
	}
	public void RefreshList() {
		checkThese.clear();
		getActiveList(checkThese);
	}
	
	protected void onHandleIntent(Intent intent) {
		
		// Send Notification
		con = getApplicationContext();
		
		dbw = new DBWrapper(con);
		Motivation_Schedule motivationForNotification = new DBObjects().new Motivation_Schedule();
		motivationForNotification = dbw.Get_Next_Motivation_Pending();

		Motivation motivationDetails = new DBObjects().new Motivation();
		motivationDetails = dbw
				.Get_Motivation_Data(motivationForNotification.Motivation_Id);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				con)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle("Blackmail")
				.setContentText(
						motivationDetails.MotivationText + " due shortly.");

		NotificationManager mManager = (NotificationManager) con
				.getApplicationContext()
				.getSystemService(
						con.getApplicationContext().NOTIFICATION_SERVICE);

		mManager.cancelAll();
		mManager.notify(motivationForNotification.Motivation_Id,
				mBuilder.build());

		// Send notification here?
		
		setupLC(con);

		// ArrayList<Notification_LocationCheck> checkThese = new
		// ArrayList<Notification_LocationCheck>();

		// getActiveList(checkThese); // initially populate list
		RefreshList();

		while (checkThese.size() > 0) {
			if (lc == null) // Reconstruct the location client if we have to
				setupLC(con);
			Log.d("blackmail", "before last location");
			String locationProvider = LocationManager.GPS_PROVIDER;
			Location loc = lm.getLastKnownLocation(locationProvider);
			//Location loc = lc.getLastLocation(); // can return null if location
			Log.d("blackmail", "after last location");
			if (loc != null) {
				testCurrentLocation(checkThese, loc); // Events where user is
														// found at location
			}
			checkExpiration(checkThese); // Failure here if you wanted to show
											// up, success if you didn't want
											// to.
			Log.d("blackmail", "After check expiration");
			if(!doingDemo)
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // wait 30 seconds
			getActiveList(checkThese); // populate checkList with new additions
			if(doingDemo) break;

		}
		Log.d("blackmail", "out of loop");
		lm.removeUpdates(this);
		//lc.disconnect(); // We are done, stop polling GPS
	}

	public void success(int motivationID) {
		Log.d("blackmail", "one success");
		dbw.Update_Motivation_NoOfOccurrences(motivationID);
	}

	public void failure(int motivationID) {
		Log.d("blackmail", "one failure");
		dbw.Update_Blackmail_PendingBackOuts(motivationID);
	}

	public void getActiveList(ArrayList<Notification_LocationCheck> checkThese) {
		Long current = new Date().getTime();

		List<Notification_LocationCheck> all = dbw.Get_Pending_Goals_To_Check(); // get
																					// full
																					// list
																					// of
																					// pending
																					// goals

		// Go through each pending goal, see if we should start polling for its
		// GPS
		for (int i = 0; i < all.size(); i++) {
			Notification_LocationCheck m = all.get(i);
			// Are we within the fifteen minute window, and are we not already
			// checking it?
			if ((doingDemo || withinFifteen(current, m.MotivationSchedule.ForTime.getTime()))
					&& notAlreadyChecking(checkThese,
							m.MotivationData.Motivation_Id)) {
				checkThese.add(m); // Add it so it starts being checked
			}
		}
	}

	public void testCurrentLocation(
			ArrayList<Notification_LocationCheck> checkThese, Location currLoc) {
		float[] results = new float[1];
		for (int i = 0; i < checkThese.size(); i++) {
			GPS_Location locData = checkThese.get(i).GPSLocationData;
			int id = checkThese.get(i).MotivationData.Motivation_Id;
			/*for(int x=0; x<30; x++){
				//Log.d("blackmail", ""+currLoc.getAccuracy());
				currLoc = loc;
			}*/
			Location.distanceBetween(currLoc.getLatitude(),
					currLoc.getLongitude(), locData.Latitude,
					locData.Longitude, results);
			// If the distance between the two points is less than
			// LOCATION_THRESHOLD meters
			boolean isThere = results[0] < LOCATION_THRESHOLD;
			Log.d("blackmail", ""+results[0]);
			
			if (isThere) {
				checkThese.remove(i);
				i--;

				if (locData.Check)
					success(id); // Awesome! You said you'd be there and you
									// are!
				else
					failure(id); // You fool! You said you wouldn't be there and
									// you are!

			}
		}
	}

	public void checkExpiration(ArrayList<Notification_LocationCheck> checkThese) {
		Long current = new Date().getTime();
		for (int i = 0; i < checkThese.size(); i++) {
			int id = checkThese.get(i).MotivationData.Motivation_Id;
			long goalTime = checkThese.get(i).MotivationSchedule.ForTime
					.getTime();
			if (!withinFifteen(current, goalTime)) {
				GPS_Location locData = checkThese.remove(i).GPSLocationData;
				i--;

				if (locData.Check)
					failure(id); // You failed to show up on time
				else
					success(id); // You didn't show up! Awesome!

			}
		}
	}

	// -15/+15 minutes
	public boolean withinFifteen(Long current, Long thisTime) {
		return Math.abs(current - thisTime) <= 900000;
	}

	public boolean notAlreadyChecking(
			ArrayList<Notification_LocationCheck> checkThese, int motivationID) {
		for (int i = 0; i < checkThese.size(); i++) {
			if (checkThese.get(i).MotivationData.Motivation_Id == motivationID)
				return false;
		}
		return true;
	}
/*
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("blackmail", "Location connection failed.");

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("blackmail", "Location connection succeeded.");

	}

	@Override
	public void onDisconnected() {
		Log.d("blackmail", "disconnected from location services.");
		// TODO Auto-generated method stub

	}
	*/

	public void setupLC(Context c) {
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		/*lc = new LocationClient(this, this, this);
		lc.connect();
		while (!lc.isConnected()) {
			try {
				int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
		        // If Google Play services is available
		        if (ConnectionResult.SUCCESS == resultCode) {
		        	Log.d("Blackmail", "Google play services is available.");
		        }
				Log.d("blackmail", "Location setup loop.");
				Thread.sleep(1000); // To avoid consuming too much CPU
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}



	@Override
	public void onLocationChanged(Location location) {
		loc = location;
		Log.d("blackmail", "Got location! It is " + location.getLatitude() + " " + location.getLongitude());
		
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
