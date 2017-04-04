package com.itech.reconapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.itech.reconapp.misc.*;

import com.itech.reconapp.types.Polygon;

public class GetPolygonsTask extends AsyncTask<Void, Void, Boolean> {

    String mUrl;
    String mComment;
    CallBackListener mListener;
    HUDConnectivityManager mHUDConnectivityManager;
    JSONObject finalResult;

    public GetPolygonsTask(String url, HUDConnectivityManager _mHUDConnectivityManager) {
        mUrl = url;
        mHUDConnectivityManager = _mHUDConnectivityManager;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        boolean result = false;
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
            request = new HUDHttpRequest(HUDHttpRequest.RequestMethod.GET, new URL(mUrl), headers);
            HUDHttpResponse response = mHUDConnectivityManager.sendWebRequest(request);
            if (response.hasBody()) {
                reader = new JSONObject(response.getBodyString());
            }

        }catch (Exception e){
            Log.d("dwada",e.getMessage());

        }

        finalResult = reader;
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mListener.callback(this.finalResult);
    }


    public GetPolygonsTask setListener(CallBackListener listener){
        mListener = listener;
        return this;
    }
/*JSONObject areas = reader;
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
        }*/

}