package itech.com.reconapp.types;

public class Coordinate {
    public int EarthRadius = 6371;
    public double latitude;
    public double longitude;

    Coordinate(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }


    public double distance(Coordinate point) {
        double dLat = Math.toRadians((point.latitude - latitude));
        double dLong = Math.toRadians((point.longitude - longitude));

        double startLatInRadians = Math.toRadians(latitude);
        double endLatInRadians = Math.toRadians(point.latitude);

        double a = haversine(dLat) + Math.cos(latitude) * Math.cos(point.latitude) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EarthRadius * c;

    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
