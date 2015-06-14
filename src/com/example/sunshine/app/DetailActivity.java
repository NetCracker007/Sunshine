package com.example.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

   	private String forecastdata;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this,SettingsActivity.class));
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public  class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
			setHasOptionsMenu(true);
			
			
		}
		
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			            // Inflate the menu; this adds items to the action bar if it is present.
			            inflater.inflate(R.menu.detailfragment, menu);
			
			            // Retrieve the share menu item
			            MenuItem menuItem = menu.findItem(R.id.action_share);
			
			            // Get the provider and hold onto it to set/change the share intent.
			            ShareActionProvider mShareActionProvider =
			                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
			
			            if (mShareActionProvider != null ) {
			            	                mShareActionProvider.setShareIntent(createShareForecastIntent());
			            	            } else {
			            	                Log.d("DETAIL_FRAGMENT", "Share Action Provider is null?");
			            	            }
			            	        }
		
		
		 private Intent createShareForecastIntent() {
			             Intent shareIntent = new Intent(Intent.ACTION_SEND);
			             shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			             shareIntent.setType("text/plain");
			             Intent root=getIntent();
			             forecastdata=root.getStringExtra(ForecastFragment.DETAIL_PASS);
			             shareIntent.putExtra(Intent.EXTRA_TEXT,
			                     forecastdata);
			             return shareIntent;
			         }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_detail,
					container, false);
			setContentView(R.layout.fragment_detail);
			Intent intent=getIntent();
			forecastdata=intent.getStringExtra(ForecastFragment.DETAIL_PASS);	
			TextView text=(TextView)findViewById(R.id.forecast_data);
			text.setText(forecastdata);
			
			return rootView;
		}
	}
}
