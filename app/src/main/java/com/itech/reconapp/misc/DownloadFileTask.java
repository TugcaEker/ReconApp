package com.itech.reconapp.misc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;
import com.reconinstruments.os.metrics.HUDMetricsID;

import com.itech.reconapp.AltitudeModel;
import com.itech.reconapp.MetricView;
import com.itech.reconapp.R;
public class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {

    String mUrl;
    String mComment;
    HUDConnectivityManager connection;

    public DownloadFileTask(HUDConnectivityManager _connection, String url) {
        mUrl = url;
        connection = _connection;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean result = false;
        try {
            HUDHttpRequest request = new HUDHttpRequest(HUDHttpRequest.RequestMethod.GET, mUrl);
            HUDHttpResponse response = connection.sendWebRequest(request);
            if (response.hasBody()) {
                mComment = "response bodySize:" + response.getBody().length;
                result = true;
            }
        } catch (Exception e) {
            mComment = "failed to download file: " + e.getMessage();
            e.printStackTrace();
            return false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}