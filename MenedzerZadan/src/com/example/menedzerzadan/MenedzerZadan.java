package com.example.menedzerzadan;



import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.menedzerzadan.R;
/**
 * Klasa bedaca pierwsza aktywnoscia.
 * Glownym zadaniem klasy jest pobranie daty, wyslanie jej do nastepnej aktywnosci oraz przejscie do nastepnej aktywnosci
 * @author Dawid Plichta
 *
 */
public class MenedzerZadan extends Activity { 
	private LocationManager locationManager;
	private boolean gpsEnabled;
	private ConnectivityManager connectivity;

	@Override 
	/**
	 * Metoda odpowiedzialna na wyswietlenie kalendarza i dwoch przyciskow (dalej i wyjdz)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_entry);
		final Intent intent = new Intent(MenedzerZadan.this, ListaZadan.class);
		final Bundle b=new Bundle();
		final DatePicker datePicker = (DatePicker) this.findViewById(R.id.datePicker1);
		final Button nextButton=(Button) this.findViewById(R.id.editDayTaskButton);
	  	final Button exitButton=(Button) this.findViewById(R.id.addDayTaskButton);
	  	
	  	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	  	gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isGPSEnabledDialog();
	  	
		nextButton.setOnClickListener(new Button.OnClickListener(){
			/*
			 * Metoda obsluguj�ca klikni�cie na przycisk "dalej".
			 * Jej zadania to: 
			 * - pobranie ustawionej na kalendarzu daty
			 * - wpisanie jej w odpowiedniej postaci do stringa
			 * - wyslanie stringa do nastepnej aktywnosci 
			 * - przejscie do nastepnej aktywnosci
			 * 
			 */ 
         	  @Override 
         	  public void onClick(View arg0) {
         		  String value = String.valueOf(datePicker.getMonth())+"-"+String.valueOf(datePicker.getDayOfMonth());
 		          b.putString("name", value);
 		          intent.putExtras(b);
 	              startActivity(intent);             		   
         	  }});
			/* 
			 * Metoda obsluguj�ca klikni�cie na przycisk "wyjdz"
			 * Jej zadaniem jest wyjscie z aplikacji
			 * 
			 */
		     exitButton.setOnClickListener(new Button.OnClickListener(){
		          @Override
		      	  public void onClick(View arg0) {
	        	  System.exit(0);         		   
		          }});
		     
		     startService(new Intent(this, GPSService.class));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(this, GPSService.class));
	}
	
	public void isGPSEnabledDialog() {
        if (!gpsEnabled) {
        	new AlertDialog.Builder(this)
            .setTitle(R.string.enable_gps)
            .setMessage(R.string.enable_gps_dialog)
            .setCancelable(true)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!gpsEnabled) {
                    	Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settingsIntent);
                    }
                }
            })
            .show();
        }
	}
	
	public boolean isConn() {
	    if (connectivity.getActiveNetworkInfo() != null) {
	        if (connectivity.getActiveNetworkInfo().isConnected())
	            return true;
	    }
	    return false;
	}

}
