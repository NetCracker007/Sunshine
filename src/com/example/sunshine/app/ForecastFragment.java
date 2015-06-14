package com.example.sunshine.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ForecastFragment extends Fragment{
	//private String[] dataForecast;
	private ArrayList<String> data;
	private static ArrayAdapter<String> adapter;
	public final static String DETAIL_PASS="com.example.sunshine.app.weatherdata";
	public ForecastFragment() {
		}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
	{
		inflater.inflate(R.menu.forecastfragment, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId()==R.id.action_refresh)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String location = prefs.getString(getString(R.string.pref_location_key),
			getString(R.string.pref_default_display_name));
			String unit;
			prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
			unit = prefs.getString(getString(R.string.unit),getString(R.string.default_unit));
			new FetchWeatherTask(location,unit).execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
			return true;
		}
		else if(item.getItemId()==R.id.show_map)
			showMap();
		return super.onOptionsItemSelected(item);
	}

	public static  ArrayAdapter<String> getAdapter()
	{
		return adapter;
	}
	
	public void showMap()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String location = prefs.getString(getString(R.string.pref_location_key),
		getString(R.string.pref_default_display_name));
		Uri geoLocation=Uri.parse("geo:0,0?q="+location);
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setData(geoLocation);
	    
	        startActivity(intent);
	    
		
    }
	
	    public void updateWeather()
	    {
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String location = prefs.getString(getString(R.string.pref_location_key),
			getString(R.string.pref_default_display_name));
			String unit;
			prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
			unit = prefs.getString(getString(R.string.unit),getString(R.string.default_unit));
			new FetchWeatherTask(location,unit).execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
			
	    }
	    @Override
	    public void onStart()
	    {
	    	super.onStart();
	    	updateWeather();
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
		data=new ArrayList<String>();
		/*data.add(new String("Monday-Sunny-88/63"));
		data.add(new String("Tuesday-Sunny-88/63"));
		data.add(new String("Wednesday-Sunny-78/63"));
		data.add(new String("Thursday-Sunny-88/60"));
		data.add(new String("Friday-Cloudy-80/60"));
		data.add(new String("Saturday-Sunny-78/63"));
		data.add(new String("Sunday-Rainy-68/50"));*/
		adapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview1,data);
		ListView list=(ListView)rootView.findViewById(R.id.listview_forecast);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			/*Toast forecast_toast=Toast.makeText(getActivity(), adapter.getItem(arg2),Toast.LENGTH_LONG);
			forecast_toast.show();*/
				
				Intent intent=new Intent(getActivity(),DetailActivity.class);
				intent.putExtra(DETAIL_PASS,adapter.getItem(arg2));
				startActivity(intent);
				
			}
			
		});
		return rootView;
		}

          
}





