package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import gnd.legokor.hu.flightcontroller.model.Coordinates;
import gnd.legokor.hu.flightcontroller.model.Direction;
import gnd.legokor.hu.flightcontroller.service.LocationService;
import gnd.legokor.hu.flightcontroller.service.SensorService;

import static java.lang.Math.abs;

public class AntennaActivity extends BaseActivity {

    Direction desiredDirection = new Direction();

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
        if (desiredDirection != null) {
            desiredAzimuth = desiredDirection.azimuth;
            desiredElevation = desiredDirection.elevation;
            if (abs(desiredAzimuth - currentDirection.azimuth) > 10) {
                azimuthDifferenceView.setTextColor(Color.RED);
            } else {
                azimuthDifferenceView.setTextColor(Color.rgb(0, 100, 0));
            }
            if (abs(desiredElevation - currentDirection.elevation) > 10) {
                elevationDifferenceView.setTextColor(Color.RED);
            } else {
                elevationDifferenceView.setTextColor(Color.rgb(0, 100, 0));
            }
            azimuthDifference = fmt.format(desiredAzimuth - currentDirection.azimuth);
            elevationDifference = fmt.format(desiredElevation - currentDirection.elevation);
        }

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

    @Override
    public void receiveUavCoordinates(Coordinates coordinates) {
        TextView deviceLatitude = findViewById(R.id.balloonLatitude);
        TextView deviceLongitude = findViewById(R.id.balloonLongitude);
        TextView deviceAltitude = findViewById(R.id.balloonAltitude);
        TextView deviceUpdated = findViewById(R.id.balloonLocationUpdated);

        deviceLatitude.setText(Double.toString(coordinates.lat));
        deviceLongitude.setText(Double.toString(coordinates.lng));
        deviceAltitude.setText(Double.toString(coordinates.alt));
        deviceUpdated.setText(DateFormat.getTimeInstance().format(new Date()));
    }

    @Override
    public void onConnectionFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AntennaActivity.this, "Failed to connect to the server", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void receiveDeviceCoordinates(Location mCurrentLocation) {
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
        setLayout(R.layout.activity_antenna);
        super.onCreate(savedInstanceState);

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
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sensorReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stateReceiver);
        super.onPause();
    }

}
