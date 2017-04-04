package com.itech.reconapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.IHUDConnectivity;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.itech.reconapp.heading.HeadingEventListener;
import com.itech.reconapp.heading.HeadingManager;
import com.itech.reconapp.misc.CallBackListener;
import com.itech.reconapp.misc.DownloadFileTask;
import com.itech.reconapp.misc.HUDManager;
import com.itech.reconapp.misc.RESTClient;
import com.itech.reconapp.types.Coordinate;
import com.itech.reconapp.types.Polygon;
import com.itech.reconapp.tasks.*;

/*, LocationListener*/
public class GuidanceActivity extends Activity implements HeadingEventListener, IHUDConnectivity, CallBackListener, LocationListener {
    public static final String PREFS_NAME = "iTechPref";
    SharedPreferences settings;

    private boolean TESTMODE = false;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // meters
    private static final long MIN_TIME_BW_UPDATES = 200 * 1 * 1; // milliseconds
    private final double MAX_ACCURACY = 20.0; // meters

    private LocationManager mLocationManager;

//    private String MainPath = "http://45.76.85.216/";
    private TextView view_Text;
    private Location currentLocation;
    private Coordinate currentCoordinate;
    private RelativeLayout relative_layout;
    private List<Polygon> polygons = new ArrayList<>();
    boolean mIsResumed = false;

    private float currentPitch = 0;

    HeadingManager headingManager = null;

    CountDownTimer countDownTimer, countDownTimer2 = null;
    public boolean operated = true;

    private HUDConnectivityManager mHUDConnectivityManager = null;

    public boolean allReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.load("/system/lib/libreconinstruments_jni.so");
        settings = getSharedPreferences(PREFS_NAME, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        view_Text = (TextView) findViewById(R.id.deneme_alan);
        relative_layout = (RelativeLayout) findViewById(R.id.relative_container);

        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);

                getJSON2();
                allReady = true;

        headingManager = new HeadingManager(this);
        // !!!! -------- IMPORTANT NOTICE -------- !!!! //
        //Note: This following line is necessary for HUDConnectivityManager to run properly
        // !!!! -------- IMPORTANT NOTICE -------- !!!! //
    }

    private void getJSON2(){
        String url =  settings.getString("api_path", "") + "gpslog/areas/31";
        new GetPolygonsTask(url, mHUDConnectivityManager).setListener(this).execute();
    }

    private void getJSON() {
        String url =  settings.getString("api_path", "") + "gpslog/areas/31";
        int count = 0;
        try {
            JSONObject reader = null;
            HUDHttpRequest request = null;
            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            List<String> ContentTypeList = new ArrayList<String>();
            List<String> AuthList = new ArrayList<String>();
            ContentTypeList.add("application/json");
            AuthList.add("Basic ZTE5NDE5MEBtZXR1LmVkdS50cjoxMjM0NTY3OA==");
            headers.put("Content-Type", ContentTypeList);
            headers.put("Authorization", AuthList);
            try{
                request = new HUDHttpRequest(HUDHttpRequest.RequestMethod.GET, new URL(url), headers);
                HUDHttpResponse response = mHUDConnectivityManager.sendWebRequest(request);
                if (response.hasBody()) {
                    reader = new JSONObject(response.getBodyString());
                }

            }catch (Exception e){
                Log.d("dwada",e.getMessage());

            }

            JSONObject areas = reader;
            Iterator iterator = areas.keys();
            int i = 0;
            while (iterator.hasNext()) {
                i++;
                String area_key = (String) iterator.next();
                JSONObject area = areas.getJSONObject(area_key);
                Polygon polygon = new Polygon();
                polygon.initilizeFromJSON(area);
                count++;
                polygons.add(polygon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePolygonViews() {
        int i = 0;
        for (Polygon p : polygons) {
           /* her polygon için yazı ekle */
            ImageView polygonal_view = new ImageView(this);
            TextView polygonal_text = new TextView(this);

            Log.d("dwadwadwadwada",String.valueOf(p.type));
            if(p.type.equals("SearchArea")){
                polygonal_view.setImageResource(R.drawable.red_full);
                polygonal_text.setText("Search");
            }else{
                polygonal_view.setImageResource(R.drawable.blue_full);
                polygonal_text.setText("Secure");

            }
                /* ne tarafta oldugun hesapla */

            double bearing = currentCoordinate.bearing(p.center);
            Log.d("WIDTH", String.valueOf(polygonal_text.getWidth()));

            polygonal_view.setScaleType(ImageView.ScaleType.FIT_XY);
            polygonal_view.setX(94);
            polygonal_view.setY(0);
            polygonal_text.setX(94);
            polygonal_text.setY(0);
            polygonal_text.setTextSize(15);
            polygonal_text.setPadding(0, 22, 0, 0);
            polygonal_text.setWidth(240);
            polygonal_text.setHeight(240);
            polygonal_text.setGravity(Gravity.CENTER_HORIZONTAL);
            relative_layout.addView(polygonal_view);
            relative_layout.addView(polygonal_text);

            Log.d("bearing =", String.valueOf(bearing));

            p.view = polygonal_view;
            p.text_view = polygonal_text;
            p.isReady = true;
        }

    }


    @Override
    public void onResume() {
        Log.i("onResume", "onResume");
        super.onResume();

        headingManager.start();
        mHUDConnectivityManager.register(this);

        mIsResumed = true;
    }

    @Override
    public void onPause() {
        Log.i("onPause", "onPause");

        headingManager.stop();

        mIsResumed = false;
        mHUDConnectivityManager.unregister(this);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "onDestroy");
        headingManager.stop();
        mIsResumed = false;
        mHUDConnectivityManager.unregister(this);
        super.onDestroy();
    }

    public void updateLocation() {

        if (TESTMODE) {
            currentCoordinate = new Coordinate(39.891885, 32.783280);
            return;
        }

        currentCoordinate = new Coordinate(39.891885, 32.783280);
        Log.d("GPSSSGELIYO", "GPS Enabled");

        try {
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            if (mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPSSS", "GPS Enabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onHeadingChanged(float heading) {
        if(!allReady) return;
        try {
            if (!this.mIsResumed || (Float.isNaN(heading))) {
                return;
            }

            float newHeading = heading;
            operated = true;
            currentPitch = newHeading;
            for (Polygon p : polygons) {
                if (p.isReady) {
                    double bearing = currentCoordinate.bearing(p.center);
                    float totalRotation = (float) (-1 * (bearing + currentPitch));
                    p.view.setRotation(totalRotation);
//                    p.text_view.setBackgroundColor(0x8800FF00);
                    double distance = currentCoordinate.distance(p.center);

                    if(p.type.equals("SearchArea")){

                        if(distance < 1){
                            p.text_view.setText("Search:"+System.getProperty ("line.separator") + String.valueOf(new DecimalFormat("#").format(distance * 1000)) + "m");
                        }else{
                            p.text_view.setText("Search:"+System.getProperty ("line.separator") + String.valueOf(new DecimalFormat("#.00").format(distance)) + "km");
                        }
                    }else{
                        if(distance < 1){
                            p.text_view.setText("Secure:"+ System.getProperty ("line.separator") + String.valueOf(new DecimalFormat("#").format(distance * 1000)) + "m");
                        }else{
                            p.text_view.setText("Secure:"+ System.getProperty ("line.separator") + String.valueOf(new DecimalFormat("#.00").format(distance)) + "km");
                        }
                    }
//                    p.text_view.setText(String.valueOf(currentPitch));
//                    view_Text.setText(String.valueOf(bearing));
                    p.text_view.setRotation(totalRotation);
//                    //p.view.invalidate();

                }
            }
        } catch (java.lang.IllegalArgumentException e) {
            Log.d("TUGCA", e.getMessage());
        }

    }

    @Override
    public void onDeviceName(String s) {

    }

    @Override
    public void onConnectionStateChanged(ConnectionState connectionState) {

    }

    @Override
    public void onNetworkEvent(NetworkEvent networkEvent, boolean b) {

    }

    @Override
    public void callback(JSONObject finalResult) {
        System.out.print("CALLBACK");
        Log.i("CALLBACK","CALLBACK");
        JSONObject areas = finalResult;
        Iterator iterator = areas.keys();
        int i = 0;
        while (iterator.hasNext()) {
            Log.i("CALLBACK","CALLBACK123123123");

            i++;
            String area_key = (String) iterator.next();
            JSONObject area = null;
            try {
                area = areas.getJSONObject(area_key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Polygon polygon = new Polygon();
            polygon.initilizeFromJSON(area);
            polygons.add(polygon);
        }

        updateLocation();
        updatePolygonViews();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
        view_Text.setText(String.valueOf(location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}