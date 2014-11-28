package com.example.cardsui_project1;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.TextView;

public class TrafficTime
{
	public class AHTTPCdata extends AsyncTask<Void, Void, String>
	{
		double from_lat,from_lng,to_lat,to_lng;
		String URL2=null;
				
		private final static String URL = "http://route.cit.api.here.com/routing/7.2/calculateroute.json?app_id=CpZ6H3WU0DDVzlrtSwmb&app_code=94H3m6P6YHmCDTL3dOCeWA";
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

		protected void onPreExecute()
		{
			URL2 = URL+"&waypoint0=geo!"+from_lat+","+from_lng+"&waypoint1=geo!"+to_lat+","+to_lng+"&mode=fastest;car;traffic:enabled";
		}

		@Override
		protected String doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			//&waypoint0=geo!1.1,2.2&waypoint1=geo!2.2,1.1&mode=fastest;car;traffic:enabled";
			System.out.println(URL2);
			HttpGet request = new HttpGet(URL2);
			JSONResponseHandler responseHandler = new JSONResponseHandler();
			try
			{
				return mClient.execute(request, responseHandler);
			}
			catch (ClientProtocolException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String res)
		{
			if (null != mClient)
				mClient.close();
			MainActivity.homeLocationTextview.setText(res);	
		}
	}
		
	private class JSONResponseHandler implements ResponseHandler <String>
	{
		public String handleResponse(HttpResponse hr)
		{
			
			String basicResponse = null, fkd="you're fucked";
			try
			{ 
				basicResponse= new BasicResponseHandler().handleResponse(hr);
				 
				//basicResponse=basicResponse.substring(basicResponse.lastIndexOf('(')+1,basicResponse.lastIndexOf(')'));
			}
			catch (HttpResponseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result=null;
			JSONObject jo1=null,jo=null ,responseObject=null;
			JSONArray route = null;
			Integer travelTime = 0;
			if (basicResponse!=null)
			{
				try
				{ 
					responseObject = (JSONObject) new JSONTokener(basicResponse).nextValue();
					responseObject= responseObject.getJSONObject("response");
					route = responseObject.getJSONArray("route");
					jo=(JSONObject)route.getJSONObject(0);
					jo1=(JSONObject)jo.getJSONObject("summary");
					travelTime = jo1.getInt("travelTime")/60;
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return travelTime.toString();
			}
			else
			{
				return fkd;
			}
		}
	}
}
