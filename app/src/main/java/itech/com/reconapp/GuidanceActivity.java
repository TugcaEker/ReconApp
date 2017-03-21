package itech.com.reconapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.IHUDConnectivity;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itech.com.reconapp.misc.DownloadFileTask;

public class GuidanceActivity extends Activity implements IHUDConnectivity  {
    private String MainPath = "http://5636db49.ngrok.io/myapp/";
    private HUDConnectivityManager mHUDConnectivityManager = null;
    private TextView view_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.load("/system/lib/libreconinstruments_jni.so");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        view_Text = (TextView) findViewById(R.id.deneme_alan);

        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);

        if(mHUDConnectivityManager.isHUDConnected()){
            view_Text.setText("Connected");
        }else{
            view_Text.setText("Connection Fil");
        }

        /* Download test */
        String url = MainPath + "person/list";

        HUDHttpRequest request = null;
        try {

            /* Prepare headers */
            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            List<String> ContentTypeList = new ArrayList<String>();
            List<String> AuthList = new ArrayList<String>();
            ContentTypeList.add("application/json");
            AuthList.add("Basic ZTE5NDE5MEBtZXR1LmVkdS50cjoxMjM0NTY3OA==");
            headers.put("Content-Type", ContentTypeList);
            headers.put("Authorization", AuthList);

            request = new HUDHttpRequest(HUDHttpRequest.RequestMethod.GET, url);
            request.setHeaders(headers);

            HUDHttpResponse response = mHUDConnectivityManager.sendWebRequest(request);
            if (response.hasBody()) {
                view_Text.setText(response.getBodyString());
            }
        } catch (Exception e) {
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