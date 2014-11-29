package com.example.cardsui_project1;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
{
	public static long INTERVAL_FIFTEEN_MINUTES = 60 * 1000L;
	public static TextView homeLocationTextview, alarmTextView;
	public static NotificationCompat.Builder mBuilder;
	public static int alarmHour, alarmMinute;
	public static Boolean alarmSet = false;
	public PendingIntent trafficAlarmPendingIntent;
	public AlarmManager trafficAlarmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		alarmTextView = (TextView) findViewById(R.id.alarmTextView);
		homeLocationTextview = (TextView) findViewById(R.id.homeLocationTextview);
		mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.alarm_clock_icon)
				.setContentTitle("Traffic Jaam")
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		// setAlarmTextView.setCompoundDrawablesWithIntrinsicBounds(drawable.alarm_clock_icon,
		// , null, null);
	}

	public void onLeadTimeClick(View v)
	{
		LeadTimeDialogFragment leadTimeDialogFragment = new LeadTimeDialogFragment();
		leadTimeDialogFragment.show(getSupportFragmentManager(),
				"leadTimeDialog");
	}

	public void onSetAlarmButtonClick(View v)
	{
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	public void onCancelAlarmButtonClick(View v)
	{
		if(trafficAlarmManager!=null)
		{
			trafficAlarmManager.cancel(trafficAlarmPendingIntent);
			alarmTextView.setText(R.string.setAlarm);
			Toast.makeText(getApplicationContext(), "Alarm cancelled", Toast.LENGTH_LONG).show();
		}
	}
	
	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it after
			// setting max date
			DatePickerDialog dpDialog = new DatePickerDialog(getActivity(),
					this, year, month, day);
			dpDialog.getDatePicker().setMaxDate(new Date().getTime());
			return dpDialog;
		}

		// Method to detect when Date is selected by user from DatePicker and
		// then return the selected value
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
		}
	}

	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			alarmSet = false;

			// Create a new instance of TimePickerDialog and return it
			final TimePickerDialog tpDialog = new TimePickerDialog(getActivity(),
					this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
			tpDialog.setCancelable(true);
			tpDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							alarmSet = false;
							tpDialog.dismiss();
						}
					});
			tpDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Set",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							alarmSet = true;
						}
					});
			return tpDialog;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			alarmHour = hourOfDay;
			alarmMinute = minute;
			if (alarmSet)
			{
				trafficAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				Intent trafficAlarmIntent = new Intent(getActivity(),
						TrafficJaamAlertService.class);
				trafficAlarmPendingIntent = PendingIntent.getService(
						getActivity(), 0, trafficAlarmIntent, 0);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, alarmHour - 2);
				calendar.set(Calendar.MINUTE, alarmMinute);
				trafficAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), INTERVAL_FIFTEEN_MINUTES,
						trafficAlarmPendingIntent);
				alarmTextView.setText(alarmHour + " : " + alarmMinute);
				Toast.makeText(getActivity(), "Alarm set for " + alarmHour + " : " + alarmMinute, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class LeadTimeDialogFragment extends DialogFragment
	{
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			final TextView leadTimeTextView = (TextView) findViewById(R.id.leadTimeTextview);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Lead Time");
			builder.setMessage("Set Lead Time (mins):");
			final EditText leadTimeEditText = new EditText(MainActivity.this);
			// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			// 1,
			// 2);
			// leadTimeEditText.setLayoutParams(lp);
			leadTimeEditText.setWidth(10);
			leadTimeEditText
					.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
			int maxLength = 2;
			InputFilter[] fArray = new InputFilter[1];
			fArray[0] = new InputFilter.LengthFilter(maxLength);
			leadTimeEditText.setFilters(fArray);
			builder.setView(leadTimeEditText);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog,
								int whichButton)
						{
							if (!leadTimeEditText.getText().toString()
									.isEmpty())
							{
								int leadTime = Integer
										.parseInt(leadTimeEditText.getText()
												.toString());
								leadTimeTextView.setText(Integer
										.toString(leadTime) + " mins");
							} else
								leadTimeTextView
										.setText(R.string.set_lead_time);
							dismiss();
						}
					});
			return builder.create();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
