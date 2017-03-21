package itech.com.reconapp.misc;

import android.os.AsyncTask;

import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.os.connectivity.http.HUDHttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RESTClient {

    /* apide sadece bunlar kullaniliyor simdilik */
    public enum Methods{
        GET,
        POST,
        PATCH,
        PUT
    }

    public Map<String, String> commonHeaders;

    HUDConnectivityManager connection;

    public RESTClient(HUDConnectivityManager _connection){
        connection = _connection;
    }

    public JSONObject execute(String url, HUDHttpRequest.RequestMethod _method) throws JSONException {
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
            request = new HUDHttpRequest(_method, url);
            request.setHeaders(headers);

            HUDHttpResponse response = connection.sendWebRequest(request);
            if (response.hasBody()) {
                reader = new JSONObject(response.getBodyString());
            }
        }catch (Exception e){

        }

        return  reader;
    }



}