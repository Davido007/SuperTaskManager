package com.example.menedzerzadan;

import com.example.menedzerzadan.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class MapActivity extends FragmentActivity implements OnMarkerClickListener, OnMapClickListener {

    private GoogleMap mMap;
	private DatabaseHandler db;
	private Intent data = new Intent();
	private double radius;
	private int markerCount = 0;
	private Circle circle;
	private Marker marker;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        radius = getIntent().getDoubleExtra("radius", 0.0);
        db = new DatabaseHandler(this);
        setUpMapIfNeeded();
    }


	@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(); //inicjalizacja mapy
    }
	
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    }
    
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);
            if (mMap != null) {
                 setUpMap(); //przygotowanie map, ustawienie położenia na mapie na przebytą trasę
            }
        }
    }


    private void setUpMap() {
    	final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
				@Override
                public void onGlobalLayout() {
                	
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    
                    /*if(GPSService.startingLocation!=null) {
                    	bounds.include(GPSService.startingLocation);
                    } else return;*/
                    
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                      } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                      }
                      //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
                      if(GPSService.startingLocation!=null) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(GPSService.startingLocation, 18));
                }
            });
        }

    }
    
    @Override
    public boolean onMarkerClick(final Marker marker) {
/*    	
    	marker.remove();
    	circle.remove();
    	markerCount--;
    	data.removeExtra("latitude");
    	data.removeExtra("longitude");
    	setResult(RESULT_CANCELED);*/
    	return false;
    }
    
	    @Override
		public void onBackPressed() {
			super.onBackPressed();
		}


		@Override
		public void onMapClick(LatLng arg0) {
			if (markerCount==0) {
				// TODO Auto-generated method stub
				marker = mMap.addMarker(new MarkerOptions()
						.position(arg0)
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				circle = mMap.addCircle(new CircleOptions().center(arg0)
						.radius(radius)
						.fillColor(Color.argb(120, 0, 0, 255)));
				markerCount++;
				data.putExtra("latitude", arg0.latitude);
				data.putExtra("longitude", arg0.longitude);
				setResult(RESULT_OK, data);
			} else {
				marker.remove();
		    	circle.remove();
		    	markerCount--;
		    	data.removeExtra("latitude");
		    	data.removeExtra("longitude");
		    	markerCount = 0;
		    	setResult(RESULT_CANCELED);
			}
			

		}
		
}
