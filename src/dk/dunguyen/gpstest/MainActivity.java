package dk.dunguyen.gpstest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	private Button endTrial;
	private String gpsProvider = "gps";
	private String networkProvider = "network";
	private String passiveProvider = "passive";

	private ArrayList<ArrayList<Float>> gpsData;
	private ArrayList<ArrayList<Float>> networkData;
	private ArrayList<ArrayList<Float>> passiveData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		latitudeField = (TextView) findViewById(R.id.latitude);
		longitudeField = (TextView) findViewById(R.id.longitude);
		providerField = (TextView) findViewById(R.id.provider);
		accuracyField = (TextView) findViewById(R.id.accuracy);
		updateField = (TextView) findViewById(R.id.lastUpdate);
		//create data
		gpsData = new ArrayList<ArrayList<Float>>();
		networkData = new ArrayList<ArrayList<Float>>();
		passiveData = new ArrayList<ArrayList<Float>>();

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
		endTrial = (Button) findViewById(R.id.endTrial);
		endTrial.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location!=null)
		{
			float lat = (float) location.getLatitude();
			float lng = (float) location.getLongitude();
			int acc = (int) location.getAccuracy();
			latitudeField.setText(String.valueOf(lat));
			longitudeField.setText(String.valueOf(lng));
			providerField.setText("Provider: " + provider);
			accuracyField.setText("Accuracy: " + String.valueOf(acc));
			updateField.setText("Last updated: " + String.valueOf((System.currentTimeMillis()-location.getTime())/1000.0));
			ArrayList<Float> temp = new ArrayList<Float>();
			temp.add(lat);
			temp.add(lng);
			temp.add((float) acc);
			temp.add((float) location.getTime());
			if(provider.equals(gpsProvider)) {
				if(gpsData.size()!=0){
					ArrayList<Float> inner = gpsData.get(gpsData.size()-1);
					if((float) location.getTime()!= inner.get(inner.size()-1))
					{
						gpsData.add(temp);
					}
				} else {
					gpsData.add(temp);
				}
			} else if (provider.equals(networkProvider)) {
				if(networkData.size()!=0){
					ArrayList<Float> inner = networkData.get(networkData.size()-1);
					if((float) location.getTime()!= inner.get(inner.size()-1))
					{
						networkData.add(temp);
					}
				} else {
					networkData.add(temp);
				}
			} else if (provider.equals(passiveProvider)) {
				if(passiveData.size()!=0){
					ArrayList<Float> inner = passiveData.get(passiveData.size()-1);
					if((float) location.getTime()!= inner.get(inner.size()-1))
					{
						passiveData.add(temp);
					}
				} else {
					passiveData.add(temp);
				}
			}
		} else {
			providerField.setText("Provider: " + provider);
			latitudeField.setText("Location not available");
			longitudeField.setText("Location not available");
			accuracyField.setText("Location not available");
			updateField.setText("Location not available");
		}
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
		switch(v.getId()) {
		case R.id.getLocation:
			if(provider.equals(gpsProvider)) {
				provider = networkProvider;
			} else if(provider.equals(networkProvider)){
				provider = passiveProvider;
			} else {
				provider = gpsProvider;
			}
			Location location = locationManager.getLastKnownLocation(provider);
			onLocationChanged(location);
			break;
		case R.id.endTrial:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"dunguyen90@gmail.com"});
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, dataToString());
			emailIntent.setType("plain/text");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
			break;
		}

	}

	private String dataToString()
	{
		String s = "";
		for(ArrayList<Float> array : gpsData)
		{
			for(Float f : array)
			{
				s += f.toString() + ",";
			}
			s+="\n";
		}
		s+="----------------------- \n";
		for(ArrayList<Float> array : networkData)
		{
			for(Float f : array)
			{
				s += f.toString() + ",";
			}
			s+="\n";
		}
		s+="----------------------- \n";
		for(ArrayList<Float> array : passiveData)
		{
			for(Float f : array)
			{
				s += f.toString() + ",";
			}
			s+="\n";
		}

		return s;
	}

}
