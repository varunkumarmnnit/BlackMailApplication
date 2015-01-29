package com.example.blackmail;

import java.sql.Date;
import java.sql.Time;

//Encapsulation for all DB data
public class DBObjects {

	// All user / application related data
	public class User_App_Data {
		String Username;
		String Firstname;
		String LastName;
		int ContactNo;
		int AppVersion;
		int DBVersion;

		Date CreatedDate;
		boolean IsActive;

		public User_App_Data() {
		}
	}

	// Master table to map APIs to tables
	public class API_Master {
		int APITypeId;
		String APIName;
		String APITableName;
		String AccessToken;
		String AccessTokenSecret;

		Date CreatedDate;
		boolean IsActive;

		public API_Master() {
		}
	}

	// Facebook API
	public class API_Info_Facebook {
		int Motivation_Id;
		String AccessToken;
		String Status;
		String ImagePath;

		Date CreatedDate;
		boolean IsActive;

		public API_Info_Facebook() {
		}
	}

	// Twitter API
	public class API_Info_Twitter {
		int Motivation_Id;
		String AccessToken;
		String AccessTokenSecret;
		String Status;
		String ImagePath;

		Date CreatedDate;
		boolean IsActive;

		public API_Info_Twitter() {
		}
	}

	// Motivation Data
	public class Motivation {
		int Motivation_Id;
		String MotivationText;
		Date MotivationStartDate;
		Date MotivationEndDate;
		// int Duration;
		int NoOfOccurrencesTotal;
		int NoOfOccurrencesPending;
		Time MotivationTime;

		boolean IsLocked; // Locked once setup is completed by user.
		boolean IsCompleted; // Sets after specified motivation time passes.

		Date CreatedDate;
		boolean IsActive;

		public Motivation() {
		}
		public Motivation(Date StartDate, Date EndDate, int noOfOccurances, Time MotivationTime, String MotivationText){
			this.MotivationStartDate = StartDate;
			this.MotivationEndDate = EndDate;
			this.NoOfOccurrencesTotal = noOfOccurances;
			this.MotivationTime = MotivationTime;
			this.MotivationText = MotivationText;
		}
	}

	// Blackmail Data
	public class Blackmail {
		int Motivation_Id;
		int APITypeId;
		String BlackmailText;
		String BlackmailImagePath;
		int NoOfBackoutsTotal;
		int NoOfBackoutsPending;

		boolean IsValid; //
		String Status; // Status of the overall Blackmail i.e.
						// Success/Failure/Pending

		Date CreatedDate;
		boolean IsActive;

		public Blackmail(int numBackouts, String blackMsg, String blackImgPath) {
			NoOfBackoutsTotal = numBackouts;
			BlackmailText = blackMsg;
			BlackmailImagePath = blackImgPath;
			//Note: API type is called afterwards in the UI thread.
		}
		public Blackmail(){	
		}
	}

	// GPS Location for Motivation
	public class GPS_Location {
		int Motivation_Id;
		double Latitude;
		double Longitude;
		boolean Check; // True - check at location, False - check not at
						// location

		Date CreatedDate;
		boolean IsActive;

		public GPS_Location() {
		}
		public GPS_Location(double Latitude, double Longitude, boolean check){
			this.Latitude = Latitude;
			this.Longitude = Longitude;
			this.Check = check;
		}
	}

	// Alarm / Notification schedules for Motivations
	public class Motivation_Schedule {
		int Motivation_Id;
		Date ForDate;
		Time ForTime;

		String Status; // Individual alarm status i.e.
						// Unset,Pending,Executed,Backout.

		Date CreatedDate;
		boolean IsActive;

		public Motivation_Schedule() {
		}
	}

	// Super object for Goal Setup
	public class GoalSetupData {
		Motivation MotivationData;
		Blackmail BlackmailData;
		GPS_Location GPSLocationData;
		
		public GoalSetupData()
		{
			MotivationData = new Motivation();
			BlackmailData = new Blackmail();
			GPSLocationData = new GPS_Location();
		}
		
		public GoalSetupData(Motivation m, Blackmail b, GPS_Location gl){
			MotivationData = m;
			BlackmailData = b;
			GPSLocationData = gl;
		}
	}

	// Super object for Notification and Location Check
	public class Notification_LocationCheck {
		Motivation MotivationData;
		GPS_Location GPSLocationData;
		Motivation_Schedule MotivationSchedule;
		
		public Notification_LocationCheck(){
			MotivationData = new Motivation();
			GPSLocationData = new GPS_Location();
			MotivationSchedule = new Motivation_Schedule();
		}
	}
}