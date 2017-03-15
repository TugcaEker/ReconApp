package itech.com.reconapp.types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public Coordinate corners[];
    public int count;
    public Coordinate center;

    private void update() {
        this.count = corners.length;
    }

    public boolean isInside(Coordinate point) {
        int sides = corners.length - 1;
        int j = sides - 1;
        boolean pointStatus = false;
        for (int i = 0; i < sides; i++) {
            if (corners[i].longitude < point.longitude && corners[j].longitude >= point.longitude ||
                    corners[j].longitude < point.longitude && corners[i].longitude >= point.longitude) {
                if (corners[i].latitude + (point.longitude - corners[i].longitude) /
                        (corners[j].longitude - corners[i].longitude) * (corners[j].latitude - corners[i].latitude) < point.latitude) {
                    pointStatus = !pointStatus;
                }
            }
            j = i;
        }
        return pointStatus;
    }

    public Coordinate center() {
        this.update();
        if (corners.length == 1) {
            return corners[0];
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
}
