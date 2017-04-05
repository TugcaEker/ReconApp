package com.itech.reconapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.reconinstruments.maplibrary.mapfragment.MapFragment;

import com.reconinstruments.maplibrary.mapfragment.subclass.MapFragment_Find;
import com.reconinstruments.maplibrary.mapview.MapView;

public class MapActivity extends FragmentActivity {

    private static final String TAG = "MapActivity";

    MapFragment mMapFragment = null;
    MapView mMap = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState != null) {
            return;
        }
        String mapIdentifier = getPackageName();
        try {
            mMapFragment = new MapFragment_Find();
            mMap = new MapView(this, mapIdentifier);
            mMapFragment.bindMap(mMap, false);
            mMapFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().
                    add(R.id.map_activity_container, mMapFragment).commit();
        } catch (Exception e) {
            mMapFragment = null;
            mMap = null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent("RECON_ACTIVATE_GPS");
        intent.putExtra("RECON_GPS_CLIENT", TAG);
        sendBroadcast(intent);
    }

    @Override
    public void onPause()   {
        super.onPause();
        Intent intent = new Intent("RECON_DEACTIVATE_GPS");
        intent.putExtra("RECON_GPS_CLIENT", TAG);
        sendBroadcast(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMapFragment != null && mMapFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mMapFragment != null && mMapFragment.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}



