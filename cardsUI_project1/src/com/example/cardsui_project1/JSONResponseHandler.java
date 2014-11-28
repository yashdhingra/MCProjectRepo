package com.example.cardsui_project1;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONResponseHandler implements ResponseHandler <String>
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