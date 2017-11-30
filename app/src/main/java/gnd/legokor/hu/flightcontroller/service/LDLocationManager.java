package gnd.legokor.hu.flightcontroller.service;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

class LDLocationManager  {

    private LocationListener listener;
    private LocationManager locMan;

    LDLocationManager(Context context, LocationListener listener) {
        this.listener = listener;
        locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    void startLocationMonitoring() {
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,100, 100, listener);
    }

    void stopLocationMonitoring() {
        if (locMan != null) {
            locMan.removeUpdates(listener);
        }
    }
}
