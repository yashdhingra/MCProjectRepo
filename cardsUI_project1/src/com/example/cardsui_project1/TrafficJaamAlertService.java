package com.example.cardsui_project1;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TrafficJaamAlertService extends Service
{
	double from_lat, from_lng, to_lat, to_lng;
	static Date alarmTime;
	static String alarmTimeString;
	String URL2 = null;

	private final static String URL = "http://route.cit.api.here.com/routing/7.2/calculateroute.json?app_id=CpZ6H3WU0DDVzlrtSwmb&app_code=94H3m6P6YHmCDTL3dOCeWA";

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler
	{
		public ServiceHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
			JSONResponseHandler responseHandler = new JSONResponseHandler();
			String trafficTimeInMins = ":-(";
			from_lat = 28.527829;
			from_lng = 77.205799;
			to_lat = 28.545926;
			to_lng = 77.270579;
			URL2 = URL+"&waypoint0=geo!"+from_lat+","+from_lng+"&waypoint1=geo!"+to_lat+","+to_lng+"&mode=fastest;car;traffic:enabled";
			HttpGet request = new HttpGet(URL2);
			
			try
			{
				trafficTimeInMins = mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if (null != mClient)
				mClient.close();
			
//			Date alarmTime = new Date();
//			alarmTime.setHours(10);
//			alarmTime.setMinutes(20);
//			
//			Date traff
//			if(((new Date()).getTime() - alarmTime.getTime()) < ())
			MainActivity.mBuilder.setContentText(trafficTimeInMins + "minutes to work.");
			NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(0, MainActivity.mBuilder.build());
			
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate()
	{
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy()
	{
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
