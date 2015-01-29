package com.example.blackmail;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.blackmail.DBObjects.API_Info_Facebook;
import com.example.blackmail.DBObjects.API_Info_Twitter;
import com.example.blackmail.DBObjects.API_Master;
import com.example.blackmail.DBObjects.Blackmail;
import com.example.blackmail.DBObjects.GPS_Location;
import com.example.blackmail.DBObjects.GoalSetupData;
import com.example.blackmail.DBObjects.Motivation;
import com.example.blackmail.DBObjects.Motivation_Schedule;
import com.example.blackmail.DBObjects.Notification_LocationCheck;
import com.example.blackmail.DBObjects.User_App_Data;
import com.example.blackmail.Posting.TwitterPost;

public class DBWrapper extends BlackmailDBHandler {
	public static String APIName_Facebook = "Facebook API";
	public static String APIName_Twitter = "Twitter API";
	public static String API_Token_Default = "DEFAULTACCESSTOKEN";
	public static String API_TokenSecret_Default = "DEFAULTACCESSTOKENSECRET";
	public static String Status_Pending = "Pending";
	public static String Status_Success = "Success";
	public static String Status_Failure = "Failure";
	public static String Status_Executed = "Executed";
	public static String Status_Backout = "Backout";

	public static boolean isInserted;

	public DBWrapper(Context context) {
		super(context);
		if (!isInserted) {
			INSERT_API_MASTER_INITIAL();
			isInserted = true;
		}
	}

	/*
	 * MISCELLANEOUS
	 */
	private void INSERT_API_MASTER_INITIAL() {
		DBObjects db = new DBObjects();
		API_Master apitwitter = db.new API_Master();
		API_Master apifacebook = db.new API_Master();

		apitwitter.APITypeId = 1;
		apitwitter.APIName = APIName_Twitter;
		apitwitter.APITableName = TABLE_API_INFO_TWITTER;
		apitwitter.AccessToken = API_Token_Default;
		apitwitter.AccessTokenSecret = API_TokenSecret_Default;
		apitwitter.CreatedDate = new java.sql.Date(
				new java.util.Date().getTime());
		apitwitter.IsActive = true;

		apifacebook.APITypeId = 2;
		apifacebook.APIName = APIName_Facebook;
		apifacebook.APITableName = TABLE_API_INFO_FACEBOOK;
		apifacebook.AccessToken = API_Token_Default;
		apifacebook.AccessTokenSecret = API_TokenSecret_Default;
		apifacebook.CreatedDate = new java.sql.Date(
				new java.util.Date().getTime());
		apifacebook.IsActive = true;

		Insert_API_Master_Data(apitwitter);
		Insert_API_Master_Data(apifacebook);
	}

	public String ConvertDateToString(Date date) {
		return date.toString();
	}

	public Date ConvertDateStringToDate(String datestring) {
		return Date.valueOf(datestring);
	}

	public String ConvertTimeToString(Time time) {
		return time.toString();
	}

	public Time ConvertTimeStringToTime(String timestring) {
		return Time.valueOf(timestring);
	}

	public int ConvertBooleanToInt(boolean isactive) {
		if (isactive == true)
			return 1;
		else
			return 0;
	}

	public boolean ConvertIntToBoolean(int isactive) {
		if (isactive == 1)
			return true;
		else
			return false;
	}

	public int GetNextMotivationId() {
		String countQuery = "SELECT  * FROM " + TABLE_MOTIVATION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int records = cursor.getCount();
		cursor.close();
		db.close();

		if (records == 0)
			return 1;
		else
			return (records + 1);
	}

	/*
	 * USER_APP_DATA_TABLE
	 */

	public void Insert_User_App_Data(User_App_Data data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		data.AppVersion = 1;
		data.DBVersion = BlackmailDBHandler.DATABASE_VERSION;
		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(USER_APP_DATA_KEY_USERNAME, data.Username);
		values.put(USER_APP_DATA_KEY_FIRSTNAME, data.Firstname);
		values.put(USER_APP_DATA_KEY_LASTNAME, data.LastName);
		values.put(USER_APP_DATA_KEY_CONTACTNO, data.ContactNo);
		values.put(USER_APP_DATA_KEY_APPVERSION, data.AppVersion);
		values.put(USER_APP_DATA_KEY_DBVERSION, data.DBVersion);
		values.put(USER_APP_DATA_KEY_CREATEDDATE, createddate);
		values.put(USER_APP_DATA_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_USER_APP_DATA, null, values);
		db.close();
	}

	/*
	 * API_MASTER_TABLE
	 */

	public void Insert_API_Master_Data(API_Master data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(API_MASTER_KEY_APITYPEID, data.APITypeId);
		values.put(API_MASTER_KEY_APINAME, data.APIName);
		values.put(API_MASTER_KEY_APITABLENAME, data.APITableName);
		values.put(API_MASTER_KEY_ACCESSTOKEN, data.AccessToken);
		values.put(API_MASTER_KEY_ACCESSTOKENSECRET, data.AccessTokenSecret);
		values.put(API_MASTER_KEY_CREATEDDATE, createddate);
		values.put(API_MASTER_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_API_MASTER, null, values);
		db.close();
	}

	public void Update_API_Master_Data_Tokens(API_Master data) {
		// Read old data depending on APITypeID
		// Update isactive = 0 for old data
		// Insert new row with updated data and Isactive = 1
		DBObjects dbO = new DBObjects();
		API_Master newdata = dbO.new API_Master();

		newdata = Read_API_Master_Data(data.APITypeId);

		/*
		 * String query = "UPDATE " + TABLE_API_MASTER + " SET " +
		 * API_MASTER_KEY_ISACTIVE + "= 0 WHERE " + API_MASTER_KEY_APITYPEID +
		 * " = " + data.APITypeId; // + " AND isActive = 1";
		 * 
		 * SQLiteDatabase db = this.getWritableDatabase();
		 * 
		 * Cursor cursor = db.rawQuery(query, null); cursor.close();
		 */

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(API_MASTER_KEY_ISACTIVE, 0);

		// updating row
		db.update(TABLE_API_MASTER, values, API_MASTER_KEY_APITYPEID + " = ?",
				new String[] { String.valueOf(data.APITypeId) });

		newdata.AccessToken = data.AccessToken;
		newdata.AccessTokenSecret = data.AccessTokenSecret;
		newdata.CreatedDate = data.CreatedDate;
		newdata.IsActive = data.IsActive;

		Insert_API_Master_Data(newdata);
		db.close();
	}

	public API_Master Read_API_Master_Data(int APITypeID) {
		DBObjects dbO = new DBObjects();
		API_Master apimasterdata = dbO.new API_Master();

		// Get data for most recent i.e. isactive = 1 entry for given APITypeID.
		String query = "SELECT * FROM " + TABLE_API_MASTER + " WHERE "
				+ API_MASTER_KEY_APITYPEID + "=" + APITypeID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			apimasterdata.APITypeId = cursor.getInt(0);
			apimasterdata.APIName = cursor.getString(1);
			apimasterdata.APITableName = cursor.getString(2);
			apimasterdata.AccessToken = cursor.getString(3);
			apimasterdata.AccessTokenSecret = cursor.getString(4);
			apimasterdata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(5));
			apimasterdata.IsActive = ConvertIntToBoolean(cursor.getInt(6));
		}
		cursor.close();
		db.close();
		return apimasterdata;
	}

	public boolean CheckAccountSetup(String APIName) {
		/*
		 * String checksetup = "SELECT " + API_MASTER_KEY_ACCESSTOKEN + " FROM "
		 * + TABLE_API_MASTER + " WHERE " + API_MASTER_KEY_APINAME + "='" +
		 * APIName + "' and IsActive = 1";
		 */
		String checksetup = "SELECT " + API_MASTER_KEY_ACCESSTOKEN + ","
				+ API_MASTER_KEY_ISACTIVE + " FROM " + TABLE_API_MASTER
				+ " WHERE " + API_MASTER_KEY_APINAME + "='" + APIName
				+ "' and IsActive = 1";
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor setupcheckcursor = db.rawQuery(checksetup, null);

		String CurrentToken = "";
		int isactive = 3;

		if (setupcheckcursor != null) {
			setupcheckcursor.moveToFirst();
			do {
				CurrentToken = setupcheckcursor.getString(0);
				isactive = setupcheckcursor.getInt(1);
				if (!CurrentToken.equalsIgnoreCase(API_Token_Default))
					return true;
			} while (setupcheckcursor.moveToNext());
			setupcheckcursor.close();
			db.close();
		}
		// if ((CurrentToken.compareTo("") == 0)
		// || (CurrentToken.compareTo(API_Token_Default)) == 0)
		// if (CurrentToken.toString() == API_Token_Default.toString())
		if (CurrentToken.equalsIgnoreCase(API_Token_Default))
			return false;
		else
			return true;
	}

	public int GetAPITypeID(String APIName) {
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + API_MASTER_KEY_APITYPEID + " FROM "
				+ TABLE_API_MASTER + " WHERE " + API_MASTER_KEY_APINAME + "='"
				+ APIName + "' and IsActive = 1";
		int APITypeID = 0;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			APITypeID = cursor.getInt(0);

		} /*
		 * else { cursor.close(); db.close(); return 0; }
		 */
		cursor.close();
		db.close();

		return APITypeID;
	}

	/*
	 * API_INFO_FACEBOOK
	 */

	public void Insert_API_Info_Facebook(API_Info_Facebook data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(API_INFO_FACEBOOK_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(API_INFO_FACEBOOK_KEY_ACCESSTOKEN, data.AccessToken);
		values.put(API_INFO_FACEBOOK_KEY_STATUS, data.Status);
		values.put(API_INFO_FACEBOOK_KEY_IMAGEPATH, data.ImagePath);
		values.put(API_INFO_FACEBOOK_KEY_CREATEDDATE, createddate);
		values.put(API_INFO_FACEBOOK_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_API_INFO_FACEBOOK, null, values);
		db.close();
	}

	public API_Info_Facebook Get_Data_To_Post_Facebook(int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		API_Info_Facebook facebookdata = dbO.new API_Info_Facebook();

		// Get data for given motivation ID.
		String query = "SELECT * FROM " + TABLE_API_INFO_FACEBOOK + " WHERE "
				+ API_INFO_FACEBOOK_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			facebookdata.Motivation_Id = cursor.getInt(0);
			facebookdata.AccessToken = cursor.getString(1);
			facebookdata.Status = cursor.getString(2);
			facebookdata.ImagePath = cursor.getString(3);
			facebookdata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(4));
			facebookdata.IsActive = ConvertIntToBoolean(cursor.getInt(5));
		}

		cursor.close();
		db.close();
		return facebookdata;
	}

	/*
	 * API_INFO_TWITTER
	 */

	public void Insert_API_Info_Twitter(API_Info_Twitter data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(API_INFO_TWITTER_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(API_INFO_TWITTER_KEY_ACCESSTOKEN, data.AccessToken);
		values.put(API_INFO_TWITTER_KEY_ACCESSTOKENSECRET,
				data.AccessTokenSecret);
		values.put(API_INFO_TWITTER_KEY_STATUS, data.Status);
		values.put(API_INFO_TWITTER_KEY_IMAGEPATH, data.ImagePath);
		values.put(API_INFO_TWITTER_KEY_CREATEDDATE, createddate);
		values.put(API_INFO_TWITTER_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_API_INFO_TWITTER, null, values);
		db.close();
	}

	public API_Info_Twitter Get_Data_To_Post_Twitter(int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		API_Info_Twitter twitterdata = dbO.new API_Info_Twitter();

		// Get data for given motivation ID.
		String query = "SELECT * FROM " + TABLE_API_INFO_TWITTER + " WHERE "
				+ API_INFO_TWITTER_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			twitterdata.Motivation_Id = cursor.getInt(0);
			twitterdata.AccessToken = cursor.getString(1);
			twitterdata.AccessTokenSecret = cursor.getString(2);
			twitterdata.Status = cursor.getString(3);
			twitterdata.ImagePath = cursor.getString(4);
			twitterdata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(5));
			twitterdata.IsActive = ConvertIntToBoolean(cursor.getInt(6));
		}

		cursor.close();
		db.close();
		return twitterdata;
	}

	/*
	 * MOTIVATION
	 */

	public void Insert_Motivation(Motivation data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		String motivationstartdate = ConvertDateToString(data.MotivationStartDate);
		String motivationenddate = ConvertDateToString(data.MotivationEndDate);
		String motivationtime = ConvertTimeToString(data.MotivationTime);

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(MOTIVATION_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(MOTIVATION_KEY_MOTIVATIONTEXT, data.MotivationText);
		values.put(MOTIVATION_KEY_MOTIVATIONSTARTDATE, motivationstartdate);
		values.put(MOTIVATION_KEY_MOTIVATIONENDDATE, motivationenddate);
		values.put(MOTIVATION_KEY_MOTIVATIONTIME, motivationtime);
		// values.put(MOTIVATION_KEY_DURATION, data.Duration);
		values.put(MOTIVATION_KEY_NOOFOCCURRENCESTOTAL,
				data.NoOfOccurrencesTotal);
		values.put(MOTIVATION_KEY_NOOFOCCURRENCESPENDING,
				data.NoOfOccurrencesPending);
		values.put(MOTIVATION_KEY_ISLOCKED, 1);
		values.put(MOTIVATION_KEY_ISCOMPLETED, 0);
		values.put(MOTIVATION_KEY_CREATEDDATE, createddate);
		values.put(MOTIVATION_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_MOTIVATION, null, values);
		db.close();
	}

	public Motivation Get_Motivation_Data(int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		Motivation motivationdata = dbO.new Motivation();

		String query = "SELECT * FROM " + TABLE_MOTIVATION + " WHERE "
				+ MOTIVATION_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			motivationdata.Motivation_Id = cursor.getInt(0);
			motivationdata.MotivationText = cursor.getString(1);
			motivationdata.MotivationStartDate = ConvertDateStringToDate(cursor
					.getString(2));
			motivationdata.MotivationEndDate = ConvertDateStringToDate(cursor
					.getString(3));
			motivationdata.NoOfOccurrencesTotal = cursor.getInt(4);
			motivationdata.NoOfOccurrencesPending = cursor.getInt(5);
			motivationdata.MotivationTime = ConvertTimeStringToTime(cursor
					.getString(6));
			motivationdata.IsLocked = ConvertIntToBoolean(cursor.getInt(7));
			motivationdata.IsCompleted = ConvertIntToBoolean(cursor.getInt(8));
			motivationdata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(9));
			motivationdata.IsActive = ConvertIntToBoolean(cursor.getInt(10));
		}

		cursor.close();
		db.close();
		return motivationdata;
	}

	public void Update_Motivation_NoOfOccurrences(int Motivation_ID) {
		int pendingoccurrences = 0;

		String getquery = "SELECT " + MOTIVATION_KEY_NOOFOCCURRENCESPENDING
				+ " FROM " + TABLE_MOTIVATION + " WHERE "
				+ MOTIVATION_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(getquery, null);

		if (cursor != null) {
			cursor.moveToFirst();
			pendingoccurrences = cursor.getInt(0);

			if (pendingoccurrences > 0) {
				if (pendingoccurrences == 1) {
					/*
					 * String query = "UPDATE " + TABLE_MOTIVATION + " SET " +
					 * MOTIVATION_KEY_NOOFOCCURRENCESPENDING + "=0 WHERE " +
					 * MOTIVATION_KEY_MOTIVATIONID + "=" + Motivation_ID +
					 * "  and IsActive = 1"; Cursor updatecursor =
					 * db.rawQuery(query, null); updatecursor.close();
					 */

					ContentValues values = new ContentValues();
					values.put(MOTIVATION_KEY_NOOFOCCURRENCESPENDING, 0);

					// updating row
					db.update(TABLE_MOTIVATION, values,
							MOTIVATION_KEY_MOTIVATIONID + " = ?",
							new String[] { String.valueOf(Motivation_ID) });

					Update_Motivation_SetCompleted(Motivation_ID);
					Update_Blackmail_Status(Motivation_ID, Status_Failure);
				} else {
					/*
					 * String query = "UPDATE " + TABLE_MOTIVATION + " SET " +
					 * MOTIVATION_KEY_NOOFOCCURRENCESPENDING + "=" +
					 * (pendingoccurrences - 1) + " WHERE " +
					 * MOTIVATION_KEY_MOTIVATIONID + "=" + Motivation_ID +
					 * " and IsActive = 1"; Cursor updatecursor =
					 * db.rawQuery(query, null); updatecursor.close();
					 */

					ContentValues values = new ContentValues();
					values.put(MOTIVATION_KEY_NOOFOCCURRENCESPENDING,
							(pendingoccurrences - 1));

					// updating row
					db.update(TABLE_MOTIVATION, values,
							MOTIVATION_KEY_MOTIVATIONID + " = ?",
							new String[] { String.valueOf(Motivation_ID) });
				}
			}
		}

		cursor.close();
		db.close();
	}

	public void Update_Motivation_SetCompleted(int Motivation_ID) {
		/*
		 * String query = "UPDATE " + TABLE_MOTIVATION + " SET " +
		 * MOTIVATION_KEY_ISCOMPLETED + "= 1 WHERE" +
		 * MOTIVATION_KEY_MOTIVATIONID + "=" + Motivation_ID +
		 * " and IsActive = 1";
		 * 
		 * SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor =
		 * db.rawQuery(query, null); cursor.close(); db.close();
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MOTIVATION_KEY_ISCOMPLETED, 1);

		// updating row
		db.update(TABLE_MOTIVATION, values, MOTIVATION_KEY_MOTIVATIONID
				+ " = ?", new String[] { String.valueOf(Motivation_ID) });

		db.close();
	}

	/*
	 * BLACKMAIL
	 */

	public void Insert_Blackmail(Blackmail data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(BLACKMAIL_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(BLACKMAIL_KEY_APITYPEID, data.APITypeId);
		values.put(BLACKMAIL_KEY_BLACKMAILTEXT, data.BlackmailText);
		values.put(BLACKMAIL_KEY_BLACKMAILIMAGEPATH, data.BlackmailImagePath);
		values.put(BLACKMAIL_KEY_NOOFBACKOUTSTOTAL, data.NoOfBackoutsTotal);
		values.put(BLACKMAIL_KEY_NOOFBACKOUTSPENDING, data.NoOfBackoutsPending);
		values.put(BLACKMAIL_KEY_ISVALID, 1);
		values.put(BLACKMAIL_KEY_STATUS, Status_Pending);
		values.put(BLACKMAIL_KEY_CREATEDDATE, createddate);
		values.put(BLACKMAIL_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_BLACKMAIL, null, values);
		db.close();

		DBObjects dbO = new DBObjects();
		if (data.APITypeId == GetAPITypeID(APIName_Facebook)) {
			API_Info_Facebook apiinfofacebookdata = dbO.new API_Info_Facebook();

			apiinfofacebookdata.Motivation_Id = data.Motivation_Id;
			apiinfofacebookdata.Status = data.Status;
			apiinfofacebookdata.ImagePath = data.BlackmailImagePath;
			apiinfofacebookdata.AccessToken = Read_API_Master_Data(data.APITypeId).AccessToken;
			apiinfofacebookdata.CreatedDate = data.CreatedDate;
			apiinfofacebookdata.IsActive = data.IsActive;

			Insert_API_Info_Facebook(apiinfofacebookdata);
		} else {
			API_Info_Twitter apiinfotwitterdata = dbO.new API_Info_Twitter();

			apiinfotwitterdata.Motivation_Id = data.Motivation_Id;
			apiinfotwitterdata.Status = data.Status;
			apiinfotwitterdata.ImagePath = data.BlackmailImagePath;
			apiinfotwitterdata.AccessToken = Read_API_Master_Data(data.APITypeId).AccessToken;
			apiinfotwitterdata.AccessTokenSecret = Read_API_Master_Data(data.APITypeId).AccessTokenSecret;
			apiinfotwitterdata.CreatedDate = data.CreatedDate;
			apiinfotwitterdata.IsActive = data.IsActive;

			Insert_API_Info_Twitter(apiinfotwitterdata);
		}
	}

	public Blackmail Get_Blackmail_Data(int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		Blackmail blackmaildata = dbO.new Blackmail();

		String query = "SELECT * FROM " + TABLE_BLACKMAIL + " WHERE "
				+ BLACKMAIL_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			blackmaildata.Motivation_Id = cursor.getInt(0);
			blackmaildata.APITypeId = cursor.getInt(1);
			blackmaildata.BlackmailText = cursor.getString(2);
			blackmaildata.BlackmailImagePath = cursor.getString(3);
			blackmaildata.NoOfBackoutsTotal = cursor.getInt(4);
			blackmaildata.NoOfBackoutsPending = cursor.getInt(5);
			blackmaildata.IsValid = ConvertIntToBoolean(cursor.getInt(6));
			blackmaildata.Status = cursor.getString(7);
			blackmaildata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(8));
			blackmaildata.IsActive = ConvertIntToBoolean(cursor.getInt(9));
		}

		cursor.close();
		db.close();
		return blackmaildata;
	}

	public void Update_Blackmail_PendingBackOuts(int Motivation_ID) {
		int pendingbackouts = 0;

		String getquery = "SELECT " + BLACKMAIL_KEY_NOOFBACKOUTSPENDING
				+ " FROM " + TABLE_BLACKMAIL + " WHERE "
				+ BLACKMAIL_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(getquery, null);

		if (cursor != null) {
			cursor.moveToFirst();
			pendingbackouts = cursor.getInt(0);

			if (pendingbackouts == 0) {
				// Execute blackmail
				Blackmail b = Get_Blackmail_Data(Motivation_ID);
				API_Master apim = Read_API_Master_Data(b.APITypeId);
				Posting p = new Posting();
				// Facebook
				if (apim.APIName.compareTo(DBWrapper.APIName_Facebook) == 0) {
					// FacebookPost fp = p.new FacebookPost();
					if (b.BlackmailText.compareTo("") == 0) {
						AccountSetupActivity account = new AccountSetupActivity();
						account.onClickPostPhoto(b.BlackmailImagePath);
					} else {
						AccountSetupActivity account = new AccountSetupActivity();
						account.onClickPostStatusUpdate(b.BlackmailText);
					}
				}
				// Twitter
				else {
					TwitterPost tp = p.new TwitterPost();
					if (b.BlackmailText.compareTo("") == 0) {
						tp.twitterPhoto(apim.AccessToken,
								apim.AccessTokenSecret, b.BlackmailImagePath);
					} else {
						tp.twitterText(apim.AccessToken,
								apim.AccessTokenSecret, b.BlackmailText);				}
				}
				Update_Blackmail_Status(Motivation_ID, Status_Success);
				Update_Motivation_SetCompleted(Motivation_ID);
			} else {
				/*
				 * String query = "UPDATE " + TABLE_BLACKMAIL + " SET " +
				 * BLACKMAIL_KEY_NOOFBACKOUTSPENDING + "=" + (pendingbackouts -
				 * 1) + " WHERE " + BLACKMAIL_KEY_MOTIVATIONID + "=" +
				 * Motivation_ID + " and IsActive = 1";
				 * 
				 * Cursor updatecursor = db.rawQuery(query, null);
				 * updatecursor.close();
				 */

				ContentValues values = new ContentValues();
				values.put(BLACKMAIL_KEY_NOOFBACKOUTSPENDING,
						(pendingbackouts - 1));

				// updating row
				db.update(TABLE_BLACKMAIL, values, BLACKMAIL_KEY_MOTIVATIONID
						+ " = ?",
						new String[] { String.valueOf(Motivation_ID) });

			}
		}

		cursor.close();
		db.close();
	}

	public void Update_Blackmail_Status(int Motivation_ID, String Status) {
		/*
		 * String query = "UPDATE" + TABLE_BLACKMAIL + " SET " +
		 * BLACKMAIL_KEY_STATUS + "='" + Status + "' WHERE " +
		 * BLACKMAIL_KEY_MOTIVATIONID + "=" + Motivation_ID +
		 * " and IsActive = 1";
		 * 
		 * SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor =
		 * db.rawQuery(query, null);
		 * 
		 * cursor.close();
		 */

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BLACKMAIL_KEY_STATUS, Status);

		// updating row
		db.update(TABLE_BLACKMAIL, values, BLACKMAIL_KEY_MOTIVATIONID + " = ?",
				new String[] { String.valueOf(Motivation_ID) });

		db.close();
	}

	/*
	 * GPS_LOCATION
	 */

	public void Insert_GPS_Location(GPS_Location data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);

		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		int check = ConvertBooleanToInt(data.Check);

		values.put(GPS_LOCATION_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(GPS_LOCATION_KEY_LATITUDE, data.Latitude);
		values.put(GPS_LOCATION_KEY_LONGITUDE, data.Longitude);
		values.put(GPS_LOCATION_KEY_CHECK, check);
		values.put(GPS_LOCATION_KEY_CREATEDDATE, createddate);
		values.put(GPS_LOCATION_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_GPS_LOCATION, null, values);
		db.close();
	}

	public GPS_Location Get_GPS_Location_Data(int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		GPS_Location gpslocationdata = dbO.new GPS_Location();

		String query = "SELECT * FROM " + TABLE_GPS_LOCATION + " WHERE "
				+ GPS_LOCATION_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			gpslocationdata.Motivation_Id = cursor.getInt(0);
			gpslocationdata.Latitude = cursor.getDouble(1);
			gpslocationdata.Longitude = cursor.getDouble(2);
			gpslocationdata.Check = ConvertIntToBoolean(cursor.getInt(3));

			gpslocationdata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(4));
			gpslocationdata.IsActive = ConvertIntToBoolean(cursor.getInt(5));
		}

		cursor.close();
		db.close();
		return gpslocationdata;
	}

	/*
	 * MOTIVATION_SCHEDULE
	 */

	public void Insert_Motivation_Schedule(Motivation_Schedule data) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		String fordate = ConvertDateToString(data.ForDate);
		String fortime = ConvertTimeToString(data.ForTime);
		// String createddate = ConvertDateToString(data.CreatedDate);
		// int isactive = ConvertBooleanToInt(data.IsActive);
		String createddate = ConvertDateToString(new java.sql.Date(
				new java.util.Date().getTime()));
		int isactive = 1;

		values.put(MOTIVATION_SCHEDULE_KEY_MOTIVATIONID, data.Motivation_Id);
		values.put(MOTIVATION_SCHEDULE_KEY_FORDATE, fordate);
		values.put(MOTIVATION_SCHEDULE_KEY_FORTIME, fortime);
		values.put(MOTIVATION_SCHEDULE_KEY_STATUS, Status_Pending);
		values.put(MOTIVATION_SCHEDULE_KEY_CREATEDDATE, createddate);
		values.put(MOTIVATION_SCHEDULE_KEY_ISACTIVE, isactive);

		// Inserting Row
		db.insert(TABLE_MOTIVATION_SCHEDULE, null, values);
		db.close();
	}

	public Motivation_Schedule Get_Next_Motivation_Pending() {
		DBObjects dbO = new DBObjects();
		Motivation_Schedule motivationscheduledata = dbO.new Motivation_Schedule();

		String query = "SELECT * FROM " + TABLE_MOTIVATION_SCHEDULE
				+ " WHERE " + MOTIVATION_SCHEDULE_KEY_STATUS + "='"
				+ Status_Pending + "' and IsActive = 1 ORDER BY "
				+ MOTIVATION_SCHEDULE_KEY_FORDATE + ","
				+ MOTIVATION_SCHEDULE_KEY_FORTIME + "";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			motivationscheduledata.Motivation_Id = cursor.getInt(0);
			motivationscheduledata.ForDate = ConvertDateStringToDate(cursor
					.getString(1));
			motivationscheduledata.ForTime = ConvertTimeStringToTime(cursor
					.getString(2));
			motivationscheduledata.Status = cursor.getString(3);
			motivationscheduledata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(4));
			motivationscheduledata.IsActive = ConvertIntToBoolean(cursor
					.getInt(5));
		}

		cursor.close();
		db.close();
		return motivationscheduledata;
	}

	public List<Motivation_Schedule> Get_Motivation_Schedule_For_ID(
			int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		List<Motivation_Schedule> motivationschedulelist = new ArrayList<Motivation_Schedule>();

		String query = "SELECT * FROM " + TABLE_MOTIVATION_SCHEDULE + " WHERE "
				+ MOTIVATION_SCHEDULE_KEY_MOTIVATIONID + "=" + Motivation_ID
				+ " and IsActive = 1 ORDER BY "
				+ MOTIVATION_SCHEDULE_KEY_FORDATE + ","
				+ MOTIVATION_SCHEDULE_KEY_FORTIME + "";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			do {
				Motivation_Schedule motivationscheduledata = dbO.new Motivation_Schedule();
				motivationscheduledata.Motivation_Id = cursor.getInt(0);
				motivationscheduledata.ForDate = ConvertDateStringToDate(cursor
						.getString(1));
				motivationscheduledata.ForTime = ConvertTimeStringToTime(cursor
						.getString(2));
				motivationscheduledata.Status = cursor.getString(3);
				motivationscheduledata.CreatedDate = ConvertDateStringToDate(cursor
						.getString(4));
				motivationscheduledata.IsActive = ConvertIntToBoolean(cursor
						.getInt(5));

				motivationschedulelist.add(motivationscheduledata);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return motivationschedulelist;
	}

	public Motivation_Schedule Get_First_Pending_Motivation_Schedule_For_ID(
			int Motivation_ID) {
		DBObjects dbO = new DBObjects();
		Motivation_Schedule motivationscheduledata = dbO.new Motivation_Schedule();

		String query = "SELECT * FROM " + TABLE_MOTIVATION_SCHEDULE
				+ " WHERE " + MOTIVATION_SCHEDULE_KEY_MOTIVATIONID + "="
				+ Motivation_ID + " and " + MOTIVATION_SCHEDULE_KEY_STATUS
				+ "='" + Status_Pending + "' and IsActive = 1 ORDER BY "
				+ MOTIVATION_SCHEDULE_KEY_FORDATE + ","
				+ MOTIVATION_SCHEDULE_KEY_FORTIME + "";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();

			motivationscheduledata.Motivation_Id = cursor.getInt(0);
			motivationscheduledata.ForDate = ConvertDateStringToDate(cursor
					.getString(1));
			motivationscheduledata.ForTime = ConvertTimeStringToTime(cursor
					.getString(2));
			motivationscheduledata.Status = cursor.getString(3);
			motivationscheduledata.CreatedDate = ConvertDateStringToDate(cursor
					.getString(4));
			motivationscheduledata.IsActive = ConvertIntToBoolean(cursor
					.getInt(5));

		}

		cursor.close();
		db.close();
		return motivationscheduledata;
	}

	public void Update_Motivation_Schedule_Status(int Motivation_ID,
			Motivation_Schedule motivationschedule, String Status) {

		/*
		 * String query = "UPDATE " + TABLE_MOTIVATION_SCHEDULE + " SET " +
		 * MOTIVATION_SCHEDULE_KEY_STATUS + "='" + Status + "' WHERE " +
		 * MOTIVATION_SCHEDULE_KEY_MOTIVATIONID + "=" + Motivation_ID + " and "
		 * + MOTIVATION_SCHEDULE_KEY_FORDATE + "='" +
		 * ConvertDateToString(motivationschedule.ForDate) + "' and " +
		 * MOTIVATION_SCHEDULE_KEY_FORTIME + "='" +
		 * ConvertTimeToString(motivationschedule.ForTime) +
		 * "' and IsActive = 1";
		 * 
		 * SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor =
		 * db.rawQuery(query, null);
		 * 
		 * cursor.close();
		 */

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MOTIVATION_SCHEDULE_KEY_STATUS, Status);

		// updating row
		db.update(
				TABLE_MOTIVATION_SCHEDULE,
				values,
				MOTIVATION_SCHEDULE_KEY_MOTIVATIONID + " = ? and "
						+ MOTIVATION_SCHEDULE_KEY_FORDATE + " = ? and "
						+ MOTIVATION_SCHEDULE_KEY_FORTIME + " = ?",
				new String[] {
						String.valueOf(Motivation_ID),
						String.valueOf(ConvertDateToString(motivationschedule.ForDate)),
						String.valueOf(ConvertTimeToString(motivationschedule.ForTime)) });

		db.close();

		if (Status == Status_Backout)
			Update_Blackmail_PendingBackOuts(Motivation_ID);
		if (Status == Status_Executed)
			Update_Motivation_NoOfOccurrences(Motivation_ID);
	}

	/*
	 * GOAL SETUP
	 */
	// @SuppressWarnings("deprecation")
	public void Insert_Goal_Setup(GoalSetupData data) {

		// Get next Motivation ID
		int currentMotivationID = GetNextMotivationId();
		data.MotivationData.Motivation_Id = currentMotivationID;
		data.BlackmailData.Motivation_Id = currentMotivationID;
		data.GPSLocationData.Motivation_Id = currentMotivationID;

		data.MotivationData.NoOfOccurrencesPending = data.MotivationData.NoOfOccurrencesTotal;

		// Insert all data in respective tables
		Insert_Motivation(data.MotivationData);
		Insert_Blackmail(data.BlackmailData);
		Insert_GPS_Location(data.GPSLocationData);

		long millisecondsinaday = (new java.util.Date(1970, 0, 2).getTime())
				- (new java.util.Date(1970, 0, 1).getTime());

		// Set all Motivation Schedules
		for (int i = 0;; i++) {

			DBObjects db = new DBObjects();
			Motivation_Schedule schedule = db.new Motivation_Schedule();

			schedule.Motivation_Id = currentMotivationID;
			schedule.ForDate = new Date(
					data.MotivationData.MotivationStartDate.getTime()
							+ (i * millisecondsinaday));

			// Hack: we stored the time as the milliseconds in hours/seconds and
			// only that
			// Add that to the current day to get the time value.
			schedule.ForTime = new Time(schedule.ForDate.getTime()
					+ data.MotivationData.MotivationTime.getTime());
			
			Time t = data.MotivationData.MotivationTime;
			
			schedule.ForTime = t;
			schedule.Status = Status_Pending;
			schedule.CreatedDate = new java.sql.Date(
					new java.util.Date().getTime());
			schedule.IsActive = true;

			if ((schedule.ForDate.before(data.MotivationData.MotivationEndDate))
					|| (schedule.ForDate
							.equals(data.MotivationData.MotivationEndDate))) {

				Insert_Motivation_Schedule(schedule);
				// AlarmManagerSetup
				AlarmSetup setup = new AlarmSetup();
				//Not doing alarms for the demo.
				//setup.Set_Alarm_For_Schedule(this.FromContext.getApplicationContext(), schedule);
			} else
				break;
		}
	}

	/*
	 * NOTIFICATIONS, CHECK LOCATION
	 */

	public List<Notification_LocationCheck> Get_Pending_Goals_To_Check() {
		DBObjects dbO = new DBObjects();
		List<Notification_LocationCheck> notificationlocationchecklist = new ArrayList<Notification_LocationCheck>();

		SQLiteDatabase db = this.getReadableDatabase();

		ArrayList<Integer> PendingMotivationIds = new ArrayList<Integer>();

		String query = "SELECT " + MOTIVATION_KEY_MOTIVATIONID + " FROM "
				+ TABLE_MOTIVATION + " WHERE " + MOTIVATION_KEY_ISACTIVE
				+ "=1 AND " + MOTIVATION_KEY_ISCOMPLETED + "=0";

		/*
		 * String query = "SELECT * FROM " + TABLE_MOTIVATION +
		 * " WHERE IsActive = 1";
		 */
		Cursor readIDscursor = db.rawQuery(query, null);

		if (readIDscursor != null) {

			readIDscursor.moveToFirst();
			if (readIDscursor.getCount() > 0) {
				do {
					int ID = readIDscursor.getInt(0);
					/*
					 * String text = readIDscursor.getString(1); Date StartDate
					 * = ConvertDateStringToDate(readIDscursor .getString(2));
					 * Date EndDate = ConvertDateStringToDate(readIDscursor
					 * .getString(3)); int not = readIDscursor.getInt(4); int
					 * nop = readIDscursor.getInt(5); Time time =
					 * ConvertTimeStringToTime(readIDscursor .getString(6)); int
					 * lock = readIDscursor.getInt(7); int completed =
					 * readIDscursor.getInt(8); Date crd =
					 * ConvertDateStringToDate(readIDscursor .getString(9)); int
					 * isact = readIDscursor.getInt(10);
					 */
					PendingMotivationIds.add(ID);
				} while (readIDscursor.moveToNext());

				for (int i = 0; i < readIDscursor.getCount(); i++) {
					Notification_LocationCheck notificationlocationcheckdata = dbO.new Notification_LocationCheck();

					notificationlocationcheckdata.MotivationData = Get_Motivation_Data(PendingMotivationIds
							.get(i));
					notificationlocationcheckdata.GPSLocationData = Get_GPS_Location_Data(PendingMotivationIds
							.get(i));
					notificationlocationcheckdata.MotivationSchedule = Get_First_Pending_Motivation_Schedule_For_ID(PendingMotivationIds
							.get(i));

					notificationlocationchecklist
							.add(notificationlocationcheckdata);
				}
			}
		}
		readIDscursor.close();
		db.close();
		return notificationlocationchecklist;
	}
}
