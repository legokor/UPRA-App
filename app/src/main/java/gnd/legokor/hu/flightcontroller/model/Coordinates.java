package gnd.legokor.hu.flightcontroller.model;

public class Coordinates {
    public double lng;
    public double lat;
    public double alt;

    @Override
    public String toString() {
        return "Longitude: " + lng + ", Latitude: " + lat + ", Altitude: " + alt;
    }
}
