package dk.dunguyen.gpstest;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener, OnClickListener{
	private TextView latitudeField;
	private TextView longitudeField;
	private TextView providerField;
	private TextView accuracyField;
	private TextView updateField;
	private LocationManager locationManager;
	private String provider;
	private Button getLocation;
	private String gpsProvider = "gps";
	private String networkProvider = "network";
	private String passiveProvider = "passive";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		latitudeField = (TextView) findViewById(R.id.latitude);
		longitudeField = (TextView) findViewById(R.id.longitude);
		providerField = (TextView) findViewById(R.id.provider);
		accuracyField = (TextView) findViewById(R.id.accuracy);
		updateField = (TextView) findViewById(R.id.lastUpdate);
		
		//Get Location Manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		provider = "network";
		Location location = locationManager.getLastKnownLocation(provider);
		
		if(location != null) {
			System.out.println("Provider " + provider + " has been selected. ");
			onLocationChanged(location);
		} else {
			latitudeField.setText("Location not available");
			longitudeField.setText("Location not available");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
		getLocation = (Button) findViewById(R.id.getLocation);
		getLocation.setOnClickListener(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		float lat = (float) location.getLatitude();
		float lng = (float) location.getLongitude();
		int acc = (int) location.getAccuracy();
		latitudeField.setText(String.valueOf(lat));
		longitudeField.setText(String.valueOf(lng));
		providerField.setText("Provider: " + provider);
		accuracyField.setText("Accuracy: " + String.valueOf(acc));
		updateField.setText("Last updated: " + String.valueOf((System.currentTimeMillis()-location.getTime())/1000.0));
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.getLocation) {
			if(provider.equals(gpsProvider)) {
				provider = networkProvider;
			} else if(provider.equals(networkProvider)){
				provider = passiveProvider;
			} else {
				provider = gpsProvider;
			}
			Location location = locationManager.getLastKnownLocation(provider);
			onLocationChanged(location);
		}
	}
	
	
}
