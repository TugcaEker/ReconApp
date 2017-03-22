package itech.com.reconapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
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

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Handler;
import itech.com.reconapp.misc.DownloadFileTask;
import itech.com.reconapp.misc.RESTClient;
import itech.com.reconapp.types.Coordinate;
import itech.com.reconapp.types.Polygon;
/*, LocationListener*/
public class GuidanceActivity extends Activity implements HeadLocationListener {

    private boolean TESTMODE = true;

    private String MainPath = "http://45.76.85.216/";
//    private String MainPath = "http://5636db49.ngrok.io/myapp/";
    private HUDConnectivityManager mHUDConnectivityManager = null;
    private TextView view_Text;
    private Location currentLocation;
    private Coordinate currentCoordinate;
    private  RelativeLayout relative_layout;
    private List<Polygon> polygons = new ArrayList<>();
    boolean mIsResumed = false;
    float mUserHeading = 0.0f;
    private HUDHeadingManager mHUDHeadingManager = null;
    boolean canWorkNow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.load("/system/lib/libreconinstruments_jni.so");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        view_Text = (TextView) findViewById(R.id.deneme_alan);
        relative_layout = (RelativeLayout) findViewById(R.id.relative_container);

        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);

        /* Download test */
//        String url = MainPath + "gpslog/areas/31";
        String url = MainPath + "json.json";
        int count = 0;
        try {

            RESTClient http = new RESTClient(mHUDConnectivityManager);

            JSONObject areas = http.execute(url, HUDHttpRequest.RequestMethod.GET);
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
        updateLocation();
        updatePolygonViews();
        mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);

    }

    private void updatePolygonViews(){
        int i = 0;
        for(Polygon p : polygons){
           /* her polygon için yazı ekle */
            ImageView polygonal_view = new ImageView(this);
            TextView polygonal_text = new TextView(this);
            polygonal_view.setImageResource(R.drawable.red_full);
                /* ne tarafta oldugun hesapla */

            double bearing = currentCoordinate.bearing(p.center);
            polygonal_text.setText(String.valueOf(bearing));
            polygonal_view.setScaleType(ImageView.ScaleType.FIT_XY);
            Log.d("bearing =",String.valueOf(bearing));
            polygonal_view.setX(94);
            polygonal_view.setY(0);
            relative_layout.addView(polygonal_view);
            relative_layout.addView(polygonal_text);
            p.view = polygonal_view;
            p.isReady = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResumed = true;
    }

    @Override
    public void onPause() {
        // Unregister the IHUDConnectivity from HUDConnectivityManager
        mIsResumed = false;
        super.onPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        mIsResumed = true;
        mHUDHeadingManager.register(this);
        handler.post(runnableCode);
    }



    public void updateLocation() {

        if(TESTMODE){
            currentCoordinate = new Coordinate(39.893767, 32.801269);
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            final Criteria criteria = new Criteria();
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            final String provider = lm.getBestProvider(criteria, true);

            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }

    }

/*
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        view_Text.setText("degisti" + String.valueOf(location.getLongitude()));
    }
*/


    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if(!canWorkNow){
            Log.d("1","bekle");
            return;
        }
        canWorkNow = false;
        if(!this.mIsResumed || (Float.isNaN(yaw))) {
            return;
        }

        float newHeading = yaw;
        if(mUserHeading > 270.0f && newHeading < 90.0f) {
            mUserHeading = mUserHeading - 360.0f;
        } else if (mUserHeading < 90.0f && newHeading > 270.0f) {
            newHeading = newHeading - 360.0f;
        }

        mUserHeading = (float) ((4.0*mUserHeading + newHeading)/5.0); // smooth heading

        /* Sinir kontrol */
        if(mUserHeading > 360.0f) mUserHeading -= 360.0f;
        else if(mUserHeading < 000.0f) mUserHeading += 360.0f;

        for(Polygon p : polygons){
            if(p.isReady){
                double bearing = currentCoordinate.bearing(p.center);
                p.view.setRotation(-(float)(bearing + mUserHeading));
            }

        }
    }


    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
            canWorkNow = true;
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 500);
        }
    };
}