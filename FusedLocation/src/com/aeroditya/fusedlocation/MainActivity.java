package com.aeroditya.fusedlocation;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		OnItemSelectedListener {

	private String TAG = this.getClass().getSimpleName();
	static String KEY_BATTERY_LEVEL = "com.aeroditya.fusedlocation.battery";
	static String LogDir = "Fused-Location";
	SharedPreferences sharedPref;
	Editor editor;

	private TextView txtConnectionStatus;
	private EditText etLocationInterval;
	private Spinner prioritySpinner;
	private EditText etDisplacementInterval;

	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Intent mIntentService;
	private PendingIntent mPendingIntent;
	private int locationPriority;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtConnectionStatus = (TextView) findViewById(R.id.txtConnectionStatus);
		etLocationInterval = (EditText) findViewById(R.id.etLocationInterval);
		etDisplacementInterval = (EditText) findViewById(R.id.etDisplacementInterval);

		prioritySpinner = (Spinner) findViewById(R.id.priority_spinner);
		prioritySpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.priority_array,
						android.R.layout.simple_spinner_item);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		prioritySpinner.setAdapter(spinnerAdapter);

		mIntentService = new Intent(this, LocationService.class);
		mPendingIntent = PendingIntent.getService(this, 1, mIntentService, 0);

		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			locationclient = new LocationClient(this, this, this);
			locationclient.connect();
		} else {
			Toast.makeText(this, "Google Play Service Error " + resp,
					Toast.LENGTH_LONG).show();

		}

	}
	

	public void onToggleClicked(View view) {

		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			String minDisp = etDisplacementInterval.getText().toString();
			String updInterval = etLocationInterval.getText().toString();
			SimpleDateFormat formater = new SimpleDateFormat("HH-mm-ss");
			String priority;
			if (locationPriority == LocationRequest.PRIORITY_HIGH_ACCURACY)
				priority = "priHI";
			else if (locationPriority == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
				priority = "priBAL";
			else
				priority = "priLOW";

			String LogfileName = priority + "_updInt" + updInterval
					+ "_minDisp" + minDisp + "_"
					+ formater.format(System.currentTimeMillis()) + ".log";
			Log2File.init(LogDir, LogfileName);

			locationrequest = LocationRequest.create();
			locationrequest.setPriority(locationPriority)
					.setInterval(Long.parseLong(updInterval))
					.setSmallestDisplacement(Float.parseFloat(minDisp));
			locationclient.requestLocationUpdates(locationrequest,
					mPendingIntent);
		} else {
			locationclient.removeLocationUpdates(mPendingIntent);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent.getItemAtPosition(pos).toString()
				.equalsIgnoreCase("HIGH_ACCURACY")) {
			locationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
			Toast.makeText(this, "HIGH_ACCURACY", Toast.LENGTH_SHORT).show();
		} else if (parent.getItemAtPosition(pos).toString()
				.equalsIgnoreCase("BALANCED_POWER_ACCURACY")) {
			locationPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
			Toast.makeText(this, "BALANCED_POWER_ACCURACY", Toast.LENGTH_SHORT)
					.show();
		} else {
			locationPriority = LocationRequest.PRIORITY_NO_POWER - 1;
			Toast.makeText(this, "LOW_POWER", Toast.LENGTH_SHORT).show();
		}

	}

	public void onNothingSelected(AdapterView<?> parent) {
		locationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationclient != null)
			locationclient.disconnect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "onConnected");
		txtConnectionStatus.setText("Connection Status : Connected");

	}

	@Override
	public void onDisconnected() {
		Log.i(TAG, "onDisconnected");
		txtConnectionStatus.setText("Connection Status : Disconnected");

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "onConnectionFailed");
		txtConnectionStatus.setText("Connection Status : Fail");

	}

}
