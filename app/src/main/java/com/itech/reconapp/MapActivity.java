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

}



