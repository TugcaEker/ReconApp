package itech.com.reconapp.types;

public class Coordinate {
    public String directionNames[] = {"N","NNE", "NE","ENE","E", "ESE","SE","SSE", "S","SSW", "SW","WSW", "W","WNW", "NW","NNW", "N"};
    public int EarthRadius = 6371;
    public double latitude;
    public double longitude;

    public Coordinate(double lat, double lon) {
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

    public double bearing(Coordinate to) {
        double diffrenceOfLongitudes = to.longitude - longitude;
        double yAxis = Math.sin(diffrenceOfLongitudes) * Math.cos(to.latitude);
        double xAxis = Math.cos(latitude) * Math.sin(to.latitude) - Math.sin(latitude) * Math.cos(to.latitude) * Math.cos(diffrenceOfLongitudes);

        return ( Math.toDegrees(Math.atan2(yAxis, xAxis)) + 360 ) % 360;
    }

    public String bearingInDirectionNames(Coordinate to){
        double angle = bearing(to);
        return directionNames[(int) (angle / 22.5) % 16];
    }
}

