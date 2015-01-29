package com.example.blackmail;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.blackmail.DBObjects.Blackmail;
import com.example.blackmail.DBObjects.GPS_Location;
import com.example.blackmail.DBObjects.GoalSetupData;
import com.example.blackmail.DBObjects.Motivation;

public class BlackmailChoicesActivity extends FragmentActivity {

	public static int FACEBOOK = 1;
	public static int TWITTER = 2;

	public static int BM_TEXT = 1;
	public static int BM_PHOTO = 2;
	private String pathToPicture;
	private boolean photoFromCamera; // False == Import, True == Camera
	private Bitmap takenPicture;

	private int plat;
	private int BMtype;
	private NumberPicker numpicker;
	private Button importPicButton;
	private Button takePicButton;
	private EditText messageBox;
	private ImageView previewPic;
	private boolean dataProvided = true;
	private int pathNum;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blackmail_choices);

		pathToPicture = "";
		pathNum = (new Random()).nextInt(10) + 1;

		// Set default platform choice as Facebook
		RadioButton platRadio = (RadioButton) findViewById(R.id.facebook_radio);
		platRadio.setChecked(true);
		plat = BlackmailChoicesActivity.FACEBOOK;

		// Set default blackmail type as Text
		RadioButton bmRadio = (RadioButton) findViewById(R.id.textBM_radio);
		importPicButton = (Button) findViewById(R.id.takePictureButton);
		takePicButton = (Button) findViewById(R.id.importPictureButton);
		messageBox = (EditText) findViewById(R.id.messageBox);
		previewPic = (ImageView) findViewById(R.id.previewPicture);

		bmRadio.setChecked(true);
		BMtype = BlackmailChoicesActivity.BM_TEXT;

		// Hide the picture buttons
		importPicButton.setVisibility(View.GONE);
		takePicButton.setVisibility(View.GONE);
		previewPic.setVisibility(View.GONE);

		// Set up the backount counter number picker
		numpicker = (NumberPicker) findViewById(R.id.backoutPicker);
		numpicker.setMinValue(0); // Lowest number of backouts is 0
		numpicker.setMaxValue(getIntent().getExtras().getBundle("goalBundle")
				.getInt("numOcc") - 1);
	}

	public void onResume() {
		super.onResume();
		previewPic.invalidate();
	}

	public void platformRadioClicked(View v) {
		boolean isChecked = ((RadioButton) v).isChecked();
		switch (v.getId()) {
		case R.id.facebook_radio:
			if (isChecked)
				plat = BlackmailChoicesActivity.FACEBOOK;
			break;
		case R.id.twitter_radio:
			if (isChecked)
				plat = BlackmailChoicesActivity.TWITTER;
			break;
		default:
		}
	}

	public void onBMTypeClicked(View v) {
		boolean isChecked = ((RadioButton) v).isChecked();
		switch (v.getId()) {
		case R.id.textBM_radio:
			if (isChecked) {
				BMtype = BlackmailChoicesActivity.BM_TEXT;
				// Hide the photo button(s)
				importPicButton.setVisibility(View.GONE);
				takePicButton.setVisibility(View.GONE);
				previewPic.setVisibility(View.GONE);
				// Show the text box
				messageBox.setVisibility(View.VISIBLE);

			}
			break;
		case R.id.pictureBM_radio:
			if (isChecked) {
				BMtype = BlackmailChoicesActivity.BM_PHOTO;
				// Hide the text box
				messageBox.setVisibility(View.GONE);
				// Show the photo button(s)
				importPicButton.setVisibility(View.VISIBLE);
				takePicButton.setVisibility(View.VISIBLE);
				previewPic.setVisibility(View.VISIBLE);
			}
			break;
		default:
		}
	}

	public void takePicture(View v) {
		Uri camImgURI = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory().getPath()
				+ "/blackmail"
				+ pathNum + ".png"));

		Intent inten = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		inten.putExtra(MediaStore.EXTRA_OUTPUT, camImgURI);
		startActivityForResult(inten, 1);
	}

	public void importPicture(View v) {
		Intent inten = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(inten, 2);
	}

	// Return from camera or gallery intent calls
	protected void onActivityResult(int rqCode, int resCode, Intent data) {
		super.onActivityResult(rqCode, resCode, data);
		Log.i("Blackmail", "Entered the activity result!!!!");

		// CAMERA - gets the image data, we could also save to a file if that
		// was required
		if (rqCode == 1 && resCode == Activity.RESULT_OK) {
			pathToPicture = Environment.getExternalStorageDirectory().getPath()
					+ "/blackmail" + pathNum + ".png";
			previewPic.setVisibility(View.VISIBLE);
			previewPic.setImageBitmap(BitmapFactory.decodeFile(pathToPicture));
			previewPic.invalidate();
			photoFromCamera = true; // Set that the image is from the camera
		}

		// GALLERY - Gets the selected image's file path, from there we can get
		// the data.
		else if (rqCode == 2 && resCode == Activity.RESULT_OK && data != null) {
			Uri picture = data.getData();
			String[] filePath = { MediaStore.Images.Media.DATA };
			Cursor cs = getContentResolver().query(picture, filePath, null,
					null, null);
			cs.moveToFirst();
			int column = cs.getColumnIndex(filePath[0]);
			pathToPicture = cs.getString(column); // Voila
			previewPic.setVisibility(View.VISIBLE);
			previewPic.setImageBitmap(BitmapFactory.decodeFile(pathToPicture));
			previewPic.invalidate();
			cs.close();
			photoFromCamera = false; // Not from camera, we are importing it.
		}

		else {
			Toast uhoh = Toast.makeText(getApplicationContext(),
					(CharSequence) "Image data was not properly returned.",
					Toast.LENGTH_SHORT);
			uhoh.show();
		}

	}

	public void finishSetupClicked(View v) {
		// Check to make sure an account exists for the chosen account
		// Check to make sure that the text/photo has been properly chosen
		DBWrapper dbw = new DBWrapper(this);

		// The user chose the photo option
		if (BMtype == BlackmailChoicesActivity.BM_PHOTO
				&& pathToPicture.compareTo("") == 0) {
			Toast notSoFast = Toast.makeText(getApplicationContext(),
					(CharSequence) "You didn't provide a picture!",
					Toast.LENGTH_SHORT);
			notSoFast.show();
			return;
		}
		// The user chose the text option
		else if (BMtype == BlackmailChoicesActivity.BM_TEXT
				&& messageBox.getText().toString().compareTo("") == 0) {
			Toast notSoFast = Toast.makeText(getApplicationContext(),
					(CharSequence) "You didn't write a blackmail message!",
					Toast.LENGTH_SHORT);
			notSoFast.show();
			return;
		}
		
		// check if the API is actually registered or not.
		else if ((plat == FACEBOOK && !dbw
				.CheckAccountSetup(DBWrapper.APIName_Facebook))
				|| (plat == TWITTER && !dbw
						.CheckAccountSetup(DBWrapper.APIName_Twitter))) {

			String p = plat == FACEBOOK ? "Facebook" : "Twitter";
			new AlertDialog.Builder(this)
					.setTitle("No " + p + " account found.")
					.setMessage("Do you want to sign into a " + p + " account?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent create = new Intent(
											getApplicationContext(),
											AccountSetupActivity.class);
									startActivity(create);
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// Don't do anything.
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}

		// Everything checks out, insert it all into the database
		else {
			// Unpackage the bundles to make the DBOS
			Bundle extras = getIntent().getExtras();
			// Unpackaging all of the goal information
			Bundle goalBundle = extras.getBundle("goalBundle");
			Bundle mapBundle = extras.getBundle("mapBundle");

			// Hack warning: Convert time (only hours + minutes) to equivalent
			// milliseconds here.
			// We are technically storing that amount of hours and minutes after
			// what Java thinks is the start of time,
			// in milliseconds.
			long milli = (goalBundle.getInt("checkHour") * 3600000)
					+ (goalBundle.getInt("checkMinute") * 60000);
			int hour = goalBundle.getInt("checkHour");
			int minute = goalBundle.getInt("checkMinute");
			Time t = new Time(hour,minute,0);
			Time tlong = new Time(milli);
			
			DBObjects DBO = new DBObjects();

			// I apologize to the beautiful code gods, for I have sinned...
			Motivation myMotiv = DBO.new Motivation(new Date(
					goalBundle.getLong("startDate")), new Date(
					goalBundle.getLong("endDate")),
					goalBundle.getInt("numOcc"), t,
					goalBundle.getString("goalName"));

			GPS_Location gpsLoc = DBO.new GPS_Location(
					mapBundle.getDouble("goalLat"),
					mapBundle.getDouble("goalLong"),
					mapBundle.getBoolean("beHere"));

			Blackmail bm;
			if (BMtype == BlackmailChoicesActivity.BM_TEXT)
				bm = DBO.new Blackmail(numpicker.getValue(), messageBox
						.getText().toString(), "");
			else { // image
				bm = DBO.new Blackmail(numpicker.getValue(), "", pathToPicture);
			}
			if(plat == 1){
				bm.APITypeId = dbw.GetAPITypeID(DBWrapper.APIName_Facebook);
			}
			else {
				bm.APITypeId = dbw.GetAPITypeID(DBWrapper.APIName_Twitter);
			}
			bm.NoOfBackoutsTotal = numpicker.getValue();
			bm.NoOfBackoutsPending = bm.NoOfBackoutsTotal;

			GoalSetupData gsd = DBO.new GoalSetupData(myMotiv, bm, gpsLoc);

			dbw.Insert_Goal_Setup(gsd);

			Intent backToMain = new Intent(this, MainActivity.class);
			startActivity(backToMain);
		}
	}

}
