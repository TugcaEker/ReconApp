package itech.com.reconapp.types;

import android.media.Image;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public List<Coordinate> corners = new ArrayList<>();
    public int count;
    public Coordinate center;
    private String key;
    public String type;
    public ImageView view;
    public boolean isReady = false;

    private void update() {
        this.count = corners.size();
    }

    public boolean isInside(Coordinate point) {
        int sides = corners.size() - 1;
        int j = sides - 1;
        boolean pointStatus = false;
        for (int i = 0; i < sides; i++) {
            if (corners.get(i).longitude < point.longitude && corners.get(j).longitude >= point.longitude ||
                    corners.get(j).longitude < point.longitude && corners.get(i).longitude >= point.longitude) {
                if (corners.get(i).latitude + (point.longitude - corners.get(i).longitude) /
                        (corners.get(j).longitude - corners.get(i).longitude) * (corners.get(j).latitude - corners.get(i).latitude) < point.latitude) {
                    pointStatus = !pointStatus;
                }
            }
            j = i;
        }
        return pointStatus;
    }

    public Coordinate center() {
        this.update();
        if (corners.size() == 1) {
            return corners.get(0);
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (Coordinate c : corners) {
            double latitude = c.latitude * Math.PI / 180;
            double longitude = c.longitude * Math.PI / 180;

            x += Math.cos(latitude) * Math.cos(longitude);
            y += Math.cos(latitude) * Math.sin(longitude);
            z += Math.sin(latitude);
        }

        int total = this.count;

        x = x / total;
        y = y / total;
        z = z / total;

        double center_lon = Math.atan2(y, x);
        double center_lat = Math.atan2(z, Math.sqrt(x * x + y * y));
        return new Coordinate(
                center_lat * 180 / Math.PI,
                center_lon * 180 / Math.PI
        );
    }

    public void setCenter(){
        Coordinate calculated = center();
        this.center = calculated;
    }

    public void addCorner(double lat, double lon){
        corners.add(new Coordinate(lat, lon));
    }

    public void addCorner(Coordinate corner){
        corners.add(corner);
    }


    public void initilizeFromJSON(JSONObject json){
        try {
            setKey(json.getString("key"));
            type = json.getString("type_name");

            JSONArray coordinateList = json.getJSONArray("coordinates");
            for(int i = 0; i < coordinateList.length(); i++){
                JSONObject singleCoordinate = coordinateList.getJSONObject(i);
                addCorner(singleCoordinate.getDouble("latitude"), singleCoordinate.getDouble("longitude"));
            }

            setCenter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
