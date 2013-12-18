package com.aeroditya.fusedlocation;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

public class LocationService extends IntentService {

	private String TAG = this.getClass().getSimpleName();
	

	public LocationService() {
		super("Fused Location");
	}

	public LocationService(String name) {
		super("Fused Location");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Location location = intent
				.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
		if (location != null) {
			StringBuilder data = new StringBuilder("[" + location.getLatitude()
					+ "," + location.getLongitude() + "]");
			data.append(",");
			data.append(location.getAccuracy());

			// Write location, accuracy, battery level to LOG FILE
			Log2File.log(data.toString());

		}

	}

}
