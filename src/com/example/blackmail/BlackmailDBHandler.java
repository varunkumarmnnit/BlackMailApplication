package com.example.blackmail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// THE DATABASE HANDLER
public class BlackmailDBHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	protected static final int DATABASE_VERSION = 1;

	// Database Name
	protected static final String DATABASE_NAME = "BlackmailDB";

	// Table names
	protected static final String TABLE_USER_APP_DATA = "UserAppData";
	protected static final String TABLE_API_MASTER = "ApiMaster";
	protected static final String TABLE_API_INFO_FACEBOOK = "ApiInfoFacebook";
	protected static final String TABLE_API_INFO_TWITTER = "ApiInfoTwitter";
	protected static final String TABLE_MOTIVATION = "Motivation";
	protected static final String TABLE_BLACKMAIL = "Blackmail";
	protected static final String TABLE_GPS_LOCATION = "GPSLocation";
	protected static final String TABLE_MOTIVATION_SCHEDULE = "MotivationSchedule";

	// Table USER_APP_DATA Column Names
	protected static final String USER_APP_DATA_KEY_USERNAME = "Username";
	protected static final String USER_APP_DATA_KEY_FIRSTNAME = "Firstname";
	protected static final String USER_APP_DATA_KEY_LASTNAME = "Lastname";
	protected static final String USER_APP_DATA_KEY_CONTACTNO = "ContactNo";
	protected static final String USER_APP_DATA_KEY_APPVERSION = "AppVersion";
	protected static final String USER_APP_DATA_KEY_DBVERSION = "DBVersion";
	protected static final String USER_APP_DATA_KEY_CREATEDDATE = "CreatedDate";
	protected static final String USER_APP_DATA_KEY_ISACTIVE = "IsActive";

	// Table API_MASTER Column Names
	protected static final String API_MASTER_KEY_APITYPEID = "ApiTypeId";
	protected static final String API_MASTER_KEY_APINAME = "ApiName";
	protected static final String API_MASTER_KEY_APITABLENAME = "ApiTableName";
	protected static final String API_MASTER_KEY_ACCESSTOKEN = "AccessToken";
	protected static final String API_MASTER_KEY_ACCESSTOKENSECRET = "AccessTokenSecret";
	protected static final String API_MASTER_KEY_CREATEDDATE = "CreatedDate";
	protected static final String API_MASTER_KEY_ISACTIVE = "IsActive";

	// Table API_INFO_FACEBOOK Column Names
	protected static final String API_INFO_FACEBOOK_KEY_MOTIVATIONID = "MotivationId";
	protected static final String API_INFO_FACEBOOK_KEY_ACCESSTOKEN = "AccessToken";
	protected static final String API_INFO_FACEBOOK_KEY_STATUS = "Status";
	protected static final String API_INFO_FACEBOOK_KEY_IMAGEPATH = "ImagePath";
	protected static final String API_INFO_FACEBOOK_KEY_CREATEDDATE = "CreatedDate";
	protected static final String API_INFO_FACEBOOK_KEY_ISACTIVE = "IsActive";

	// Table API_INFO_TWITTER Column Names
	protected static final String API_INFO_TWITTER_KEY_MOTIVATIONID = "MotivationId";
	protected static final String API_INFO_TWITTER_KEY_ACCESSTOKEN = "AccessToken";
	protected static final String API_INFO_TWITTER_KEY_ACCESSTOKENSECRET = "AccessTokenSecret";
	protected static final String API_INFO_TWITTER_KEY_STATUS = "Status";
	protected static final String API_INFO_TWITTER_KEY_IMAGEPATH = "ImagePath";
	protected static final String API_INFO_TWITTER_KEY_CREATEDDATE = "CreatedDate";
	protected static final String API_INFO_TWITTER_KEY_ISACTIVE = "IsActive";

	// Table MOTIVATION Column Names
	protected static final String MOTIVATION_KEY_MOTIVATIONID = "MotivationId";
	protected static final String MOTIVATION_KEY_MOTIVATIONTEXT = "MotivationText";
	protected static final String MOTIVATION_KEY_MOTIVATIONSTARTDATE = "MotivationStartDate";
	protected static final String MOTIVATION_KEY_MOTIVATIONENDDATE = "MotivationEndDate";
	// protected static final String MOTIVATION_KEY_DURATION = "Duration";
	protected static final String MOTIVATION_KEY_NOOFOCCURRENCESTOTAL = "NoOfOccurrencesTotal";
	protected static final String MOTIVATION_KEY_NOOFOCCURRENCESPENDING = "NoOfOccurrencesPending";
	protected static final String MOTIVATION_KEY_MOTIVATIONTIME = "MotivationTime";
	protected static final String MOTIVATION_KEY_ISLOCKED = "IsLocked";
	protected static final String MOTIVATION_KEY_ISCOMPLETED = "IsCompleted";
	protected static final String MOTIVATION_KEY_CREATEDDATE = "CreatedDate";
	protected static final String MOTIVATION_KEY_ISACTIVE = "IsActive";

	// Table BLACKMAIL Column Names
	protected static final String BLACKMAIL_KEY_MOTIVATIONID = "MotivationId";
	protected static final String BLACKMAIL_KEY_APITYPEID = "ApiTypeId";
	protected static final String BLACKMAIL_KEY_BLACKMAILTEXT = "BlackmailText";
	protected static final String BLACKMAIL_KEY_BLACKMAILIMAGEPATH = "BlackmailImagePath";
	protected static final String BLACKMAIL_KEY_NOOFBACKOUTSTOTAL = "NoOfBackoutsTotal";
	protected static final String BLACKMAIL_KEY_NOOFBACKOUTSPENDING = "NoOfBackoutsPending";
	protected static final String BLACKMAIL_KEY_ISVALID = "IsValid";
	protected static final String BLACKMAIL_KEY_STATUS = "Status";
	protected static final String BLACKMAIL_KEY_CREATEDDATE = "CreatedDate";
	protected static final String BLACKMAIL_KEY_ISACTIVE = "IsActive";

	// Table GPS_LOCATION Column Names
	protected static final String GPS_LOCATION_KEY_MOTIVATIONID = "MotivationId";
	protected static final String GPS_LOCATION_KEY_LATITUDE = "Latitude";
	protected static final String GPS_LOCATION_KEY_LONGITUDE = "Longitude";
	protected static final String GPS_LOCATION_KEY_CHECK = "CheckAtLocation";
	protected static final String GPS_LOCATION_KEY_CREATEDDATE = "CreatedDate";
	protected static final String GPS_LOCATION_KEY_ISACTIVE = "IsActive";

	// Table MOTIVATION_SCHEDULE Column Names
	protected static final String MOTIVATION_SCHEDULE_KEY_MOTIVATIONID = "MotivationId";
	protected static final String MOTIVATION_SCHEDULE_KEY_FORDATE = "ForDate";
	protected static final String MOTIVATION_SCHEDULE_KEY_FORTIME = "ForTime";
	protected static final String MOTIVATION_SCHEDULE_KEY_STATUS = "Status";
	protected static final String MOTIVATION_SCHEDULE_KEY_CREATEDDATE = "CreatedDate";
	protected static final String MOTIVATION_SCHEDULE_KEY_ISACTIVE = "IsActive";

	Context FromContext;

	public BlackmailDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		FromContext = context;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// FromContext.deleteDatabase(DATABASE_NAME);
		CREATE_USER_APP_DATA_TABLE(db);
		CREATE_API_MASTER_TABLE(db);
		CREATE_API_INFO_FACEBOOK_TABLE(db);
		CREATE_API_INFO_TWITTER_TABLE(db);
		CREATE_MOTIVATION_TABLE(db);
		CREATE_BLACKMAIL_TABLE(db);
		CREATE_GPS_LOCATION_TABLE(db);
		CREATE_MOTIVATION_SCHEDULE_TABLE(db);
		DBWrapper.isInserted = false;
		// INSERT_API_MASTER_INITIAL();
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_APP_DATA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_API_MASTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_API_INFO_FACEBOOK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_API_INFO_TWITTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTIVATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKMAIL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTIVATION_SCHEDULE);

		// Create tables again
		onCreate(db);
	}

	/*
	 * USER_APP_DATA_TABLE
	 */

	private void CREATE_USER_APP_DATA_TABLE(SQLiteDatabase db) {
		String CREATE_USER_APP_DATA_TABLE = "CREATE TABLE "
				+ TABLE_USER_APP_DATA + "(" + USER_APP_DATA_KEY_USERNAME
				+ " TEXT," + USER_APP_DATA_KEY_FIRSTNAME + " TEXT,"
				+ USER_APP_DATA_KEY_LASTNAME + " TEXT,"
				+ USER_APP_DATA_KEY_CONTACTNO + " INTEGER,"
				+ USER_APP_DATA_KEY_APPVERSION + " INTEGER,"
				+ USER_APP_DATA_KEY_DBVERSION + " INTEGER,"
				+ USER_APP_DATA_KEY_CREATEDDATE + " TEXT,"
				+ USER_APP_DATA_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_USER_APP_DATA_TABLE);
	}

	/*
	 * API_MASTER_TABLE
	 */

	private void CREATE_API_MASTER_TABLE(SQLiteDatabase db) {
		String CREATE_API_MASTER_TABLE = "CREATE TABLE " + TABLE_API_MASTER
				+ "(" + API_MASTER_KEY_APITYPEID + " INTEGER,"
				+ API_MASTER_KEY_APINAME + " TEXT,"
				+ API_MASTER_KEY_APITABLENAME + " TEXT,"
				+ API_MASTER_KEY_ACCESSTOKEN + " TEXT,"
				+ API_MASTER_KEY_ACCESSTOKENSECRET + " TEXT,"
				+ API_MASTER_KEY_CREATEDDATE + " TEXT,"
				+ API_MASTER_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_API_MASTER_TABLE);
	}

	/*
	 * private void INSERT_API_MASTER_INITIAL() { DBObjects db = new
	 * DBObjects(); API_Master apitwitter = db.new API_Master(); API_Master
	 * apifacebook = db.new API_Master();
	 * 
	 * apitwitter.APITypeId = 1; apitwitter.APIName = "Twitter API";
	 * apitwitter.APITableName = TABLE_API_INFO_TWITTER; apitwitter.AccessToken
	 * = ""; apitwitter.AccessTokenSecret = ""; apitwitter.CreatedDate = new
	 * java.sql.Date( new java.util.Date().getTime()); apitwitter.IsActive =
	 * true;
	 * 
	 * apifacebook.APITypeId = 2; apifacebook.APIName = "Facebook API";
	 * apifacebook.APITableName = TABLE_API_INFO_FACEBOOK;
	 * apifacebook.AccessToken = ""; apifacebook.AccessTokenSecret = "";
	 * apifacebook.CreatedDate = new java.sql.Date( new
	 * java.util.Date().getTime()); apifacebook.IsActive = true;
	 * 
	 * DBWrapper wrapper = new DBWrapper(FromContext);
	 * 
	 * wrapper.Insert_API_Master_Data(apitwitter);
	 * wrapper.Insert_API_Master_Data(apifacebook); }
	 */
	/*
	 * API_INFO_FACEBOOK_TABLE
	 */
	private void CREATE_API_INFO_FACEBOOK_TABLE(SQLiteDatabase db) {
		String CREATE_API_INFO_FACEBOOK_TABLE = "CREATE TABLE "
				+ TABLE_API_INFO_FACEBOOK + "("
				+ API_INFO_FACEBOOK_KEY_MOTIVATIONID + " INTEGER,"
				+ API_INFO_FACEBOOK_KEY_ACCESSTOKEN + " TEXT,"
				+ API_INFO_FACEBOOK_KEY_STATUS + " TEXT,"
				+ API_INFO_FACEBOOK_KEY_IMAGEPATH + " TEXT,"
				+ API_INFO_FACEBOOK_KEY_CREATEDDATE + " TEXT,"
				+ API_INFO_FACEBOOK_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_API_INFO_FACEBOOK_TABLE);
	}

	/*
	 * API_INFO_TWITTER_TABLE
	 */
	private void CREATE_API_INFO_TWITTER_TABLE(SQLiteDatabase db) {
		String CREATE_API_INFO_TWITTER_TABLE = "CREATE TABLE "
				+ TABLE_API_INFO_TWITTER + "("
				+ API_INFO_TWITTER_KEY_MOTIVATIONID + " INTEGER,"
				+ API_INFO_TWITTER_KEY_ACCESSTOKEN + " TEXT,"
				+ API_INFO_TWITTER_KEY_ACCESSTOKENSECRET + " TEXT,"
				+ API_INFO_TWITTER_KEY_STATUS + " TEXT,"
				+ API_INFO_TWITTER_KEY_IMAGEPATH + " TEXT,"
				+ API_INFO_TWITTER_KEY_CREATEDDATE + " TEXT,"
				+ API_INFO_TWITTER_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_API_INFO_TWITTER_TABLE);
	}

	/*
	 * MOTIVATION_TABLE
	 */
	private void CREATE_MOTIVATION_TABLE(SQLiteDatabase db) {
		String CREATE_MOTIVATION_TABLE = "CREATE TABLE " + TABLE_MOTIVATION
				+ "("
				+ MOTIVATION_KEY_MOTIVATIONID
				+ " INTEGER,"
				+ MOTIVATION_KEY_MOTIVATIONTEXT
				+ " TEXT,"
				+ MOTIVATION_KEY_MOTIVATIONSTARTDATE
				+ " TEXT,"
				+ MOTIVATION_KEY_MOTIVATIONENDDATE
				+ " TEXT,"
				// + MOTIVATION_KEY_DURATION + "INTEGER,"
				+ MOTIVATION_KEY_NOOFOCCURRENCESTOTAL + " INTEGER,"
				+ MOTIVATION_KEY_NOOFOCCURRENCESPENDING + " INTEGER,"
				+ MOTIVATION_KEY_MOTIVATIONTIME + " TEXT,"
				+ MOTIVATION_KEY_ISLOCKED + " INTEGER,"
				+ MOTIVATION_KEY_ISCOMPLETED + " INTEGER,"
				+ MOTIVATION_KEY_CREATEDDATE + " TEXT,"
				+ MOTIVATION_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_MOTIVATION_TABLE);
	}

	/*
	 * BLACKMAIL_TABLE
	 */
	private void CREATE_BLACKMAIL_TABLE(SQLiteDatabase db) {
		String CREATE_BLACKMAIL_TABLE = "CREATE TABLE " + TABLE_BLACKMAIL + "("
				+ BLACKMAIL_KEY_MOTIVATIONID + " INTEGER,"
				+ BLACKMAIL_KEY_APITYPEID + " INTEGER,"
				+ BLACKMAIL_KEY_BLACKMAILTEXT + " TEXT,"
				+ BLACKMAIL_KEY_BLACKMAILIMAGEPATH + " TEXT,"
				+ BLACKMAIL_KEY_NOOFBACKOUTSTOTAL + " INTEGER,"
				+ BLACKMAIL_KEY_NOOFBACKOUTSPENDING + " INTEGER,"
				+ BLACKMAIL_KEY_ISVALID + " INTEGER," + BLACKMAIL_KEY_STATUS
				+ " TEXT," + BLACKMAIL_KEY_CREATEDDATE + " TEXT,"
				+ BLACKMAIL_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_BLACKMAIL_TABLE);
	}

	/*
	 * GPS_LOCATION_TABLE
	 */
	private void CREATE_GPS_LOCATION_TABLE(SQLiteDatabase db) {
		String CREATE_GPS_LOCATION_TABLE = "CREATE TABLE " + TABLE_GPS_LOCATION
				+ "(" + GPS_LOCATION_KEY_MOTIVATIONID + " INTEGER,"
				+ GPS_LOCATION_KEY_LATITUDE + " INTEGER,"
				+ GPS_LOCATION_KEY_LONGITUDE + " INTEGER,"
				+ GPS_LOCATION_KEY_CHECK + " INTEGER,"
				+ GPS_LOCATION_KEY_CREATEDDATE + " TEXT,"
				+ GPS_LOCATION_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_GPS_LOCATION_TABLE);
	}

	/*
	 * MOTIVATION_SCHEDULE_TABLE
	 */
	private void CREATE_MOTIVATION_SCHEDULE_TABLE(SQLiteDatabase db) {
		String CREATE_MOTIVATION_SCHEDULE_TABLE = "CREATE TABLE "
				+ TABLE_MOTIVATION_SCHEDULE + "("
				+ MOTIVATION_SCHEDULE_KEY_MOTIVATIONID + " INTEGER,"
				+ MOTIVATION_SCHEDULE_KEY_FORDATE + " TEXT,"
				+ MOTIVATION_SCHEDULE_KEY_FORTIME + " TEXT,"
				+ MOTIVATION_SCHEDULE_KEY_STATUS + " TEXT,"
				+ MOTIVATION_SCHEDULE_KEY_CREATEDDATE + " TEXT,"
				+ MOTIVATION_SCHEDULE_KEY_ISACTIVE + " INTEGER)";

		db.execSQL(CREATE_MOTIVATION_SCHEDULE_TABLE);
	}
}
