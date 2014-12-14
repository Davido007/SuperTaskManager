package com.example.menedzerzadan;

import com.example.menedzerzadan.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMarkerClickListener{

    private GoogleMap mMap;
	private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        db = new DatabaseHandler(this);
        setUpMapIfNeeded();
       
    }


	@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(); //inicjalizacja mapy
    }
	
    
    
    
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setOnMarkerClickListener(this);
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
                    
                    if(GPSService.startingLocation!=null) bounds.include(GPSService.startingLocation);
                    else return;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                      } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                      }
                      mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
                }
            });
        }

    }
    
    @Override
    public boolean onMarkerClick(final Marker marker) {
    	
    	//dialog z opcją usunięcia markera?
    	return false;
    }
    
	    @Override
		public void onBackPressed() {
			super.onBackPressed();
		}
}
