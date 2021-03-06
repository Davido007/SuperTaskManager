package com.example.menedzerzadan;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.menedzerzadan.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {

	private LocationManager lm;
	private LocationListener locationListener;
	private static long minTimeMillis = 1000; //minimalny czas do wykonania onLocationChanged
	private static long minDistanceMeters = 1; //minimalny dystans do wykonania onLocationChanged
	private static float minAccuracyMeters = 500; //próg dokładności jaki każdy punkt musi spełnić, aby być zapisanym do tabeli, 35
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	private NotificationManager mNotificationManager;
	private static final String tag = "GPSService";
	private SharedPreferences prefs;
	private GPSStatusListener gpsStatusListener;
	private DatabaseHandler db;
	public static LatLng startingLocation;
	private ArrayList<Location> locList;
	private double[][] markerLocations;
	private ArrayList<Double> radiusList;
	private final DateFormat timestampFormat = DateFormat.getDateTimeInstance();
	private String currentDate;
	private ArrayList<String> actionsList;
	private ArrayList<String> telephoneList;
	private ArrayList<String> smsTextList;
	
	private void startLoggerService() { //jeśli internet jest włączony, oba provider-y będą działać
		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				minTimeMillis, 
				minDistanceMeters,
				locationListener);
		if(isConn()) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				minTimeMillis, 
				minDistanceMeters,
				locationListener);	
		}
				
		
		Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		startingLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
		//initDatabase(); //utworzenie tabeli przechwytującej punkty
		
	}
	
	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
	}

	public class MyLocationListener implements LocationListener {
		
		private Editor editor;
		private boolean firstFix;
		
		@Override
		public void onLocationChanged(Location loc) {
			firstFix = prefs.getBoolean("firstFix", false);
			double currentLocation[] = new double[2];
			if (loc != null /*&& firstFix*/) { //zbieranie punkt�w, gdy �ledzenie trasy zosta�o w��czone oraz je�li jest fix
				try {
					Log.i("GPSSerive", "New Point: "+loc.getLatitude()+" "+loc.getLongitude());
					if (loc.hasAccuracy() && loc.getAccuracy() <= minAccuracyMeters) { //punkt zostaje zapisany, je�li minimalna dok�adno�� jest przekroczona
						ArrayList<Integer> markerIndices = isLocationInMarkersRange(locList, loc);
						if(!markerIndices.isEmpty()) {
							for(int i=0; i<markerIndices.size(); i++) {
								int currentIndex = markerIndices.get(i);
								if(actionsList.get(currentIndex).equalsIgnoreCase(Action.MUTEPHONE)) {
									MutePhoneAction mute = new MutePhoneAction();
									mute.execute(getApplicationContext());
									Log.i("GPSService", "MutePhone executed");
									actionsList.remove(currentIndex);
									locList.remove(currentIndex);
									radiusList.remove(currentIndex);
									markerIndices.remove(i);
									//db.updateTaskExecuted(startTime, endTime, startDate, description);
								}
								if(actionsList.get(markerIndices.get(i)).equalsIgnoreCase(Action.SENDSMS)) {
									SendSMSAction sms = new SendSMSAction(getApplicationContext());
									sms.setDestinationAddress(telephoneList.get(currentIndex)); //zamienic na wartosc pobrana z bazy danych
									sms.setText(smsTextList.get(currentIndex)); //zamienic na wartosc pobrana z bazy danych
									sms.execute(getApplicationContext());
									Log.i("GPSService", "SendSMS executed");
									actionsList.remove(currentIndex);
									locList.remove(currentIndex);
									radiusList.remove(currentIndex);
									markerIndices.remove(i);
								}
							}
						}
					} 
				} catch (Exception e) {
					Log.e(tag , e.toString());
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			String showStatus = null;
			if (status == LocationProvider.AVAILABLE)
				showStatus = "Available";
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
				showStatus = "Temporarily Unavailable";
			if (status == LocationProvider.OUT_OF_SERVICE)
				showStatus = "Out of Service";
			if (status != lastStatus && showingDebugToast) {
				Toast.makeText(getBaseContext(),
						"new status: " + showStatus,
						Toast.LENGTH_SHORT).show();
			}
			
			lastStatus = status;
		}



	}

	public class GPSStatusListener implements GpsStatus.Listener {
		private GpsStatus mStatus;
		private boolean firstFix;
		private Editor editor;
		
		@Override
		public void onGpsStatusChanged(int event) { //sprawdza czy pojawił się fix
			mStatus = lm.getGpsStatus(mStatus);
		    switch (event) {
		        case GpsStatus.GPS_EVENT_STARTED: break;
		        case GpsStatus.GPS_EVENT_STOPPED: break;
		        case GpsStatus.GPS_EVENT_FIRST_FIX: firstFix = true;
			    									editor = prefs.edit();
													editor.putBoolean("firstFix", firstFix);
													editor.commit(); 
													break;
		        case GpsStatus.GPS_EVENT_SATELLITE_STATUS: break;
		        default: break;
		    }	
		}
	}
	
	@Override
	public void onCreate() {
		locList = new ArrayList<Location>();
		db = new DatabaseHandler(this);
		Calendar cal = Calendar.getInstance();
		currentDate = timestampFormat.format(cal.getTime()).substring(0, timestampFormat.format(cal.getTime()).length()-9);
		markerLocations = db.getTasksCoordinatesForDay(currentDate);
		radiusList = db.getTasksRadiiForDay(currentDate);
		actionsList = db.getActionsForDay(currentDate);
		telephoneList = db.getTelephoneForDay(currentDate);
		smsTextList = db.getSMSTextForDay(currentDate);
		
		if (markerLocations!=null) {
			for (int j = 0; j < markerLocations.length; j++) {
				Location loc = new Location("imaginaryprovider");
				loc.setLongitude(markerLocations[j][0]);
				loc.setLatitude(markerLocations[j][1]);
				locList.add(loc);
			}
		}
		prefs = getSharedPreferences("preferences", 0);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		super.onCreate();
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		gpsStatusListener = new GPSStatusListener();
		lm.addGpsStatusListener(gpsStatusListener);
		startLoggerService();
		showNotification();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		locList.clear();
		radiusList.clear();
		actionsList.clear();
		telephoneList.clear();
		smsTextList.clear();
		markerLocations = db.getTasksCoordinatesForDay(currentDate);
		radiusList = db.getTasksRadiiForDay(currentDate);
		actionsList = db.getActionsForDay(currentDate);
		telephoneList = db.getTelephoneForDay(currentDate);
		smsTextList = db.getSMSTextForDay(currentDate);
		
		if (markerLocations!=null) {
			for (int j = 0; j < markerLocations.length; j++) {
				Location loc = new Location("imaginaryprovider");
				loc.setLongitude(markerLocations[j][0]);
				loc.setLatitude(markerLocations[j][1]);
				locList.add(loc);
			}
		}
		
		return Service.START_NOT_STICKY;
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		shutdownLoggerService();
		mNotificationManager.cancel(1);
	}

	private void showNotification() {
		Intent intent = new Intent(this, MenedzerZadan.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT); //jeśli aplikacja jest uruchomiona (nie sama usługa) to kliknięcie notyfikacji nic nie zrobi
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Paths+")
		        .setContentText("SuperTaskManager działa w tle")
				.setContentIntent(pIntent);
				mNotificationManager.notify(1 , mBuilder.build());
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public boolean isConn() { //sprawdzenie czy jest połączenie z internetem
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
	
	private double calculateDistance(Location newLocation, Location taskLocation) { //obliczanie dystansu (tylko dla przebytej trasy)
		double distance = 0;
		distance = newLocation.distanceTo(taskLocation);
		return distance;
	}
	
	/**
	 * Zwraca tablice ArrayList<Integer> indeksów listy markerów locList, w pobliżu których znajduje się nowa pozycja
	 * @param locationList
	 * @param newLocation
	 * @return 
	 */
	private ArrayList<Integer> isLocationInMarkersRange(ArrayList<Location> locationList, Location newLocation) {
		ArrayList<Integer> markersInRange = new ArrayList<Integer>();
		for(int i = 0; i<locList.size(); i++) {
			double distance = calculateDistance(newLocation, locList.get(i));
			Log.i("GPSService", "New points distance: "+distance);
			if(distance<=radiusList.get(i)) {
				markersInRange.add(i);
			}
		}
		
		return markersInRange;
	}

	public class LocalBinder extends Binder {
		GPSService getService() {
			return GPSService.this;
		}
	}

}

