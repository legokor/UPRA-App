package gnd.legokor.hu.flightcontroller.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service implements LocationListener {

    public static final String BR_NEW_LOCATION = "BR_NEW_LOCATION";
    public static final String KEY_LOCATION = "KEY_LOCATION";

    private LDLocationManager ldLocationManager = null;
    private boolean locationMonitorRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!locationMonitorRunning) {
            locationMonitorRunning = true;
            ldLocationManager = new LDLocationManager(getApplicationContext(), this);
            ldLocationManager.startLocationMonitoring();
        }
        Log.i("SERVICE", "Location service started");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("SERVICE", "destroy");
        if (ldLocationManager != null) {
            ldLocationManager.stopLocationMonitoring();
        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("SERVICE", "Location changed");
        Intent intent = new Intent(BR_NEW_LOCATION);
        intent.putExtra(KEY_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("STATUS", s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("ENABLED", s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("DISABLED", s);
    }
}
