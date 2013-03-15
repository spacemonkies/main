package com.spacemonkeys.util;

	import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

	public class MSLLocationManager extends Service implements LocationListener {

		private final Context mContext;

		// flag for GPS status
		boolean isGPSEnabled = false;

		// flag for network status
		boolean isNetworkEnabled = false;

		// flag for GPS status
		boolean canGetLocation = false;

		protected Location location; // location
		protected double latitude; // latitude
		protected double longitude; // longitude

		protected final Geocoder mGeocoder;

		// The minimum distance to change Updates in meters
		private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters

		// The minimum time between updates in milliseconds
		private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1; // 1 minute

		// Declaring a Location Manager
		protected final LocationManager mLocationManager;

		public MSLLocationManager(final Context pContext) {
			this.mContext = pContext;
			this.mGeocoder = new Geocoder(pContext);
			this.mLocationManager = (LocationManager) pContext.getSystemService(LOCATION_SERVICE);
			this.getLocation(false);
		}

		/**
		 * Function to check GPS/wifi enabled
		 * @return boolean
		 * */
		public boolean canGetLocation() {
			return this.canGetLocation;
		}

		public String getAddress(final Location pLocation) {
			List<Address> add = null;

			try {
				add = mGeocoder.getFromLocation(pLocation.getLatitude(), pLocation.getLongitude(), 1);
				Log.e("@MSLLocationManager", "add equals" + add.toString());
			} catch (final IOException e) {
				Log.e("@MSLLocationManager", "CRASHING INSIDE MSLLocationManager.getAddress() TRY/CATCH");
				Log.e("@MSLLocationManager", "pLocation equals " + pLocation.toString());
				Log.e("@MSLLocationManager", "mGeocoder equals "  + mGeocoder.toString());
				e.printStackTrace();
				Log.e("@MSLLocationManager", "DYING NOW");
			}

			Log.e("@MSLLocationManager", "DEAD NOW");
			Log.e("@MSLLocationManager", "DEATH'S ADDRESS IS " + add.toString());
			final String line1 = add.get(0).getAddressLine(0);
			final String line2 = add.get(0).getAddressLine(1);

			final String address = line1 + "\n " + line2;

			return address;

		}

		public Location getLocation(final boolean pPopDialog) {
				// getting GPS status
				isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

				// getting network status
				isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (!isGPSEnabled && !isNetworkEnabled) {
					if (pPopDialog) showSettingsAlert();
				} else {
					this.canGetLocation = true;
					if (isNetworkEnabled) {
						mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (mLocationManager != null) {
							location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
					// if GPS Enabled get lat/long using GPS Services
					if (isGPSEnabled) {
						if (location == null) {
							mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
							if (mLocationManager != null) {
								location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								if (location != null) {
									latitude = location.getLatitude();
									longitude = location.getLongitude();
								}
							}
						}
					}
				}
			return location;
		}

		@Override
		public IBinder onBind(final Intent arg0) {
			return null;
		}

		@Override
		public void onLocationChanged(final Location location) {
		}

		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {
		}

		@Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
		}

		/**
		 * Function to show settings alert dialog
		 * On pressing Settings button will lauch Settings Options
		 * */
		public void showSettingsAlert(){
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

	        // Setting Dialog Title
	        alertDialog.setTitle("Location settings");

	        // Setting Dialog Message
	        alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	            @Override
				public void onClick(final DialogInterface dialog,final int which) {
	            	final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            	mContext.startActivity(intent);
	            }
	        });

	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            @Override
				public void onClick(final DialogInterface dialog, final int which) {
	            dialog.cancel();
	            }
	        });

	        // Showing Alert Message
	        alertDialog.show();
		}

		/**
		 * Stop using GPS listener
		 * Calling this function will stop using GPS in your app
		 * */
		public void stopUsingGPS(){
			if(mLocationManager != null){
				mLocationManager.removeUpdates(MSLLocationManager.this);
			}
		}

	}