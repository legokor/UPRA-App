package gnd.legokor.hu.flightcontroller.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationService extends Service implements LocationListener {

    public static final String BR_NEW_LOCATION = "BR_NEW_LOCATION";
    public static final String KEY_LOCATION = "KEY_LOCATION";

    private LocationManager locMan;
    private boolean locationMonitorRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!locationMonitorRunning) {
            locationMonitorRunning = true;
            locMan = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,100, 0, this);
        }
        Log.i("SERVICE", "Location service started");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("SERVICE", "destroy");
        if (locMan != null) {
            locMan.removeUpdates(this);
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
