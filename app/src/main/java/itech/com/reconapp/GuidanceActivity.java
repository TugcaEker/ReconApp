package itech.com.reconapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.IHUDConnectivity;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import itech.com.reconapp.misc.DownloadFileTask;
import itech.com.reconapp.misc.RESTClient;
import itech.com.reconapp.types.Polygon;

public class GuidanceActivity extends Activity implements IHUDConnectivity  {
    private String MainPath = "http://5636db49.ngrok.io/myapp/";
    private HUDConnectivityManager mHUDConnectivityManager = null;
    private TextView view_Text;

    private List<Polygon> polygons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.load("/system/lib/libreconinstruments_jni.so");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        view_Text = (TextView) findViewById(R.id.deneme_alan);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative_container);

        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);

        if(mHUDConnectivityManager.isHUDConnected()){
            view_Text.setText("Connected");
        }else{
            view_Text.setText("Connection Fil");
        }

        /* Download test */
        String url = MainPath + "gpslog/areas/31";
        int count = 0;
        try {

            RESTClient http = new RESTClient(mHUDConnectivityManager);

            JSONObject areas = http.execute(url, HUDHttpRequest.RequestMethod.GET);
            Iterator iterator = areas.keys();
            while(iterator.hasNext()){
                String area_key = (String) iterator.next();
                JSONObject area = areas.getJSONObject(area_key);

                Polygon polygon = new Polygon();
                polygon.initilizeFromJSON(area);
                count++;
                polygons.add(polygon);

                /* her polygon için yazı ekle */
                TextView polygonal_view = new TextView(this);
                polygonal_view.setText(String.valueOf(polygon.center.latitude) + ", " + String.valueOf(polygon.center.longitude));

                rl.addView(polygonal_view);

            }


        } catch (Exception e) {
            view_Text.setText(e.toString());

            e.printStackTrace();
        }

    }

    private void downloadTest(){
        String url, comment;

    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Register the IHUDConnectivity to HUDConnectivityManager
        mHUDConnectivityManager.register(this);
    }

    @Override
    public void onPause()
    {
        // Unregister the IHUDConnectivity from HUDConnectivityManager
        mHUDConnectivityManager.unregister(this);
        super.onPause();
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
}