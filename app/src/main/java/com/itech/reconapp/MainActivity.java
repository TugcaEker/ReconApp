package com.itech.reconapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.reconinstruments.ui.list.ReconListView;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.SimpleListItem;
import com.reconinstruments.ui.list.StandardListItem;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.ArrayList;

public class MainActivity extends SimpleListActivity {
    public static final String PREFS_NAME = "iTechPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("api_path", "http://c4ce9463.ngrok.io/myapp/");
        editor.commit();

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list_standard_layout);
        Intent IntentA = new Intent(this, CameraActivity.class);
        Intent IntentB = new Intent(this, GuidanceActivity.class);
        Intent IntentC = new Intent(this, CompassActivity.class);
        Intent IntentD = new Intent(this, MapActivity.class);
        setContents(
                new ListItem("Open Camera", IntentA),
                new ListItem("Guide Me", IntentB),
                new ListItem("Map", IntentD),
                new ListItem("Compass", IntentC)

        );
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause()  {
        super.onPause();
    }

}