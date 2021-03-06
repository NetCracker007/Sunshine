package com.example.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

public class FetchWeatherTask extends AsyncTask<String,Void,String[]>
{
   // private FetchWeatherTask data = new FetchWeatherTask();
	String postalcode;
	String unit;
	 public FetchWeatherTask(String post,String unit)
    {
   	 this.postalcode=post;
   	 this.unit=unit;
    }
	 
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        
        if(unit.equals("1"))
        { 
       	 roundedHigh=9*roundedHigh/5+32;
       	 roundedLow=9*roundedLow/5+32;
        }
        
        
        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        //System.out.println("No Problem so far");
        
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray =(JSONArray) forecastJson.getJSONArray(OWM_LIST);

      //  Log.i("JSONtoArray",weatherArray.toString());
        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

        /*for (String s : resultStrs) {
            Log.v("ForecastFragment", "Forecast entry: " + s);*/
        
        return resultStrs;

    }
    protected String[] doInBackground(String... url1)
    {
	 
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		 
		// Will contain the raw JSON response as a string.
		String forecastJsonStr = null;
		 
		try {
		    // Construct the URL for the OpenWeatherMap query
		    // Possible parameters are available at OWM's forecast API page, at
		    // http://openweathermap.org/API#forecast
		    URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+postalcode+"&mode=json&units=metric&cnt=7");
		 
		    // Create the request to OpenWeatherMap, and open the connection
		    urlConnection = (HttpURLConnection) url.openConnection();
		    urlConnection.setRequestMethod("GET");
		    urlConnection.connect();
		 
		    // Read the input stream into a String
		    InputStream inputStream = urlConnection.getInputStream();
		    StringBuffer buffer = new StringBuffer();
		    if (inputStream == null) {
		        // Nothing to do.
		        forecastJsonStr = null;
		    }
		    reader = new BufferedReader(new InputStreamReader(inputStream));
		 
		    String line;
		    while ((line = reader.readLine()) != null) {
		        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
		        // But it does make debugging a *lot* easier if you print out the completed
		        // buffer for debugging.
		        buffer.append(line + "\n");
		    }
		 
		    if (buffer.length() == 0) {
		        // Stream was empty.  No point in parsing.
		        forecastJsonStr = null;
		    }
		    forecastJsonStr = buffer.toString();
		  //  System.out.println("json:"+forecastJsonStr);
		} catch (IOException e) {
			System.out.println("here");
		    e.printStackTrace();
			Log.e("PlaceholderFragment", "Error ", e);
		    // If the code didn't successfully get the weather data, there's no point in attempting
		    // to parse it.
		    forecastJsonStr = null;
		} finally{
		    if (urlConnection != null) {
		        urlConnection.disconnect();
		    }
		    if (reader != null) {
		        try {
		            reader.close();
		        } catch (final IOException e) {
		            Log.e("PlaceholderFragment", "Error closing stream", e);
		        }
		    }
		}
		Log.i("FetchWeatherTask","BackGround success");
	try {
	//	System.out.println(forecastJsonStr);
		return getWeatherDataFromJson(forecastJsonStr,7);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return null;
	/**Log.i("FetchWeatherTask","BackGround success");
		System.out.println("Background!");
		return null;**/
	}
	protected void onPostExecute(String[] json)
	{
	   
		Log.i("OnPostExecute",json[0]);
		//dataForecast=json;
		ArrayAdapter<String> adapter=ForecastFragment.getAdapter();
		adapter.clear();
		for(String dayforecast:json)
		{
			adapter.add(dayforecast);
		}
		
	
	}
	protected void onPreExecute()
	{
		//System.out.println(json);
		Log.i("OnPreExecute","Going to fetch");
		System.out.println("Pre!");
	}
}

