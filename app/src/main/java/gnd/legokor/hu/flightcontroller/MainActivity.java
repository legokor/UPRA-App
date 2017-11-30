package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DecimalFormat;

import gnd.legokor.hu.flightcontroller.model.Direction;
import gnd.legokor.hu.flightcontroller.service.SensorService;

public class MainActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(getApplicationContext(), SensorService.class);
        startService(i);
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
