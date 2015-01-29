package com.example.blackmail;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class GoalChoicesActivity extends FragmentActivity {

	private EditText nameBox;
	private TimePicker startTime;
	private TimePicker endTime;
	private DatePicker startDate;
	private DatePicker endDate;
	private NumberPicker occPick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goal_choices);

		nameBox = (EditText) findViewById(R.id.goal_name_box);
		startTime = (TimePicker) findViewById(R.id.startTimePick);
		//endTime = (TimePicker) findViewById(R.id.endTimePick);
		startDate = (DatePicker) findViewById(R.id.startDatePick);
		endDate = (DatePicker) findViewById(R.id.endDatePick);
		occPick = (NumberPicker) findViewById(R.id.occurrencePicker);

		occPick.setMinValue(1);
		occPick.setMaxValue(100); // We could pick this value more
									// intelligently...

		// Used for setting and comparing dates
		Calendar cal = Calendar.getInstance();
		startDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
		cal.add(Calendar.HOUR_OF_DAY, 1);
		//endTime.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		//endTime.setCurrentMinute(cal.get(Calendar.MINUTE));
		cal.add(Calendar.HOUR_OF_DAY, -1);
		cal.add(Calendar.MONTH, 1);
		endDate.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

	}

	public void continueButtonClicked(View v) {
		// Compare the two times
		/*
		int startHour = startTime.getCurrentHour();
		int startMinute = startTime.getCurrentMinute();
		int endHour = endTime.getCurrentHour();
		int endMinute = endTime.getCurrentMinute();*/
		int checkHour = startTime.getCurrentHour();
		int checkMinute = startTime.getCurrentMinute();
		boolean occIsGood = false;
		boolean dateIsGood = false;
		boolean nameIsGood = false;

		nameIsGood = !nameBox.getText().toString().equals("");

		// Does the time range to check make sense? (Is the start actually
		// before the end?)
		// Note: Hour is in 24 hour format, so no worries about AM/PM
		//timeIsGood = (startHour < endHour || (startHour == endHour && startMinute < endMinute));
	    //timeIsGood = true; 

		// Does the date range make sense?
		Calendar start = Calendar.getInstance();
		start.set(startDate.getYear(), startDate.getMonth(),
				startDate.getDayOfMonth());
		Calendar end = Calendar.getInstance();
		end.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth());

		dateIsGood = start.compareTo(end) <= 0;
		//Makes sure the number of occurences chosen is possible
       
        long maxOcc = TimeUnit.DAYS.convert(end.getTimeInMillis() - start.getTimeInMillis(), TimeUnit.MILLISECONDS);
        if(maxOcc == 0) maxOcc = 1;
        occIsGood = occPick.getValue() <= maxOcc;
        // party on Wayne, party on Garth
		if (nameIsGood && dateIsGood && occIsGood) {
			Intent goToMap = new Intent(getApplicationContext(),
					GoalMapActivity.class);

			// Bundle up the goodness
			Bundle goalBundle = new Bundle();
			goalBundle.putInt("numOcc", occPick.getValue());
			goalBundle.putInt("checkHour", checkHour);
			goalBundle.putInt("checkMinute", checkMinute);
			goalBundle.putString("goalName", nameBox.getText().toString());
			goalBundle.putLong("startDate", start.getTime().getTime());
			goalBundle.putLong("endDate", end.getTime().getTime());

			goToMap.putExtra("goalBundle", goalBundle);
			startActivity(goToMap);

		} else {
			// Make the appropriate toast
			String errorString = "Can't continue because:\n";
			if (!nameIsGood)
				errorString += "You forgot to name your goal!\n";
			if (!dateIsGood)
				errorString += "Goal start date is not before goal end date.\n";
			if (!occIsGood){
				errorString += "Number of occurances too high. Changing to maximum number of occurances, " + maxOcc + ", for you.";
			    occPick.setValue((int)maxOcc); //If someone set a date difference long enough to break the conversion I'd be impressed.
			}
			Toast notSoFast = Toast.makeText(getApplicationContext(),
					(CharSequence) errorString, Toast.LENGTH_LONG);
			notSoFast.show();
		}
	}

}
