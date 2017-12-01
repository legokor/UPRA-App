package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import gnd.legokor.hu.flightcontroller.model.Direction;
import gnd.legokor.hu.flightcontroller.service.LocationService;
import gnd.legokor.hu.flightcontroller.service.SensorService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver sensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Direction currentDirection = intent.getParcelableExtra(SensorService.KEY_SENSOR);
            updateDeviceDirection(currentDirection);
        }
    };
    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String magnetoAccuracy = intent.getStringExtra(SensorService.KEY_MAGNETOMETER);
            String acceleroAccuracy = intent.getStringExtra(SensorService.KEY_ACCELEROMETER);
            if (magnetoAccuracy != null) {
                updateMagnetometerAccuracy(magnetoAccuracy);
            }
            if (acceleroAccuracy != null) {
                updateAccelerometerAccuracy(acceleroAccuracy);
            }
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location currentLocation = intent.getParcelableExtra(LocationService.KEY_LOCATION);
            updateDeviceLocation(currentLocation);
        }
    };

    private void updateDeviceDirection(Direction currentDirection) {
        DecimalFormat fmt = new DecimalFormat("+#,##0;-#");

        TextView currentAzimuthView = findViewById(R.id.currentAzimuth);
        TextView desiredAzimuthView = findViewById(R.id.desiredAzimuth);
        TextView azimuthDifferenceView = findViewById(R.id.azimuthDifference);
        TextView currentElevationView = findViewById(R.id.currentElevation);
        TextView desiredElevationView = findViewById(R.id.desiredElevation);
        TextView elevationDifferenceView = findViewById(R.id.elevationDifference);

        int desiredAzimuth = 0;
        int desiredElevation = 0;
        String azimuthDifference = "-";
        String elevationDifference = "-";

        currentAzimuthView.setText(String.format("%d°", currentDirection.azimuth));
        desiredAzimuthView.setText(String.format("%d°", desiredAzimuth));
        azimuthDifferenceView.setText(azimuthDifference);
        currentElevationView.setText(String.format("%d°", currentDirection.elevation));
        desiredElevationView.setText(String.format("%d°", desiredElevation));
        elevationDifferenceView.setText(elevationDifference);
    }

    private void updateMagnetometerAccuracy(String accuracy) {
        TextView magnetometerState = findViewById(R.id.magnetometerState);
        magnetometerState.setText(accuracy);
    }

    private void updateAccelerometerAccuracy(String accuracy) {
        TextView accelerometerState = findViewById(R.id.accelerometerState);
        accelerometerState.setText(accuracy);
    }

    private void updateDeviceLocation(Location mCurrentLocation) {
        TextView deviceLatitude = findViewById(R.id.deviceLatitude);
        TextView deviceLongitude = findViewById(R.id.deviceLongitude);
        TextView deviceAltitude = findViewById(R.id.deviceAltitude);
        TextView deviceUpdated = findViewById(R.id.deviceLocationUpdated);

        deviceLatitude.setText(Double.toString(mCurrentLocation.getLatitude()));
        deviceLongitude.setText(Double.toString(mCurrentLocation.getLongitude()));
        deviceAltitude.setText(Double.toString(mCurrentLocation.getAltitude()));
        deviceUpdated.setText(DateFormat.getTimeInstance().format(new Date()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = new Intent(getApplicationContext(), SensorService.class);
        startService(i);
        Intent i2 = new Intent(getApplicationContext(), LocationService.class);
        startService(i2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(sensorReceiver, new IntentFilter(SensorService.BR_NEW_SENSOR));
        LocalBroadcastManager.getInstance(this).registerReceiver(stateReceiver, new IntentFilter(SensorService.BR_NEW_ACCURACY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(LocationService.BR_NEW_LOCATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sensorReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stateReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_antenna) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
