package itech.com.reconapp.types;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public Coordinate corners[];

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
}
