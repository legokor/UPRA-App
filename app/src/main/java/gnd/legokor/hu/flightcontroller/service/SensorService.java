package gnd.legokor.hu.flightcontroller.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;

import gnd.legokor.hu.flightcontroller.model.Direction;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    public static final String BR_NEW_SENSOR = "BR_NEW_SENSOR";
    public static final String BR_NEW_ACCURACY = "BR_NEW_ACCURACY";

    public static final String KEY_SENSOR = "KEY_SENSOR";

    public static final String KEY_MAGNETOMETER = "KEY_MAGNETOMETER";
    public static final String KEY_ACCELEROMETER = "KEY_ACCELEROMETER";

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    private Direction currentDirection = new Direction();

    private long lastUpdated = new Date().getTime();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE", "Sensor service started");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            currentDirection.azimuth = (int) (Math.toDegrees(mOrientation[0]) >= 0 ? Math.toDegrees(mOrientation[0]) : Math.toDegrees(mOrientation[0]) + 360);
            currentDirection.elevation = (int) Math.toDegrees(mOrientation[1]);

            long currentTime = new Date().getTime();
            if (currentTime - lastUpdated > 200) {
                lastUpdated = currentTime;
                Log.i("SERVICE", "Location changed");
                Intent intent = new Intent(BR_NEW_SENSOR);
                intent.putExtra(KEY_SENSOR, currentDirection);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    private String getAccuracy(int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_NO_CONTACT: return "No contact";
            case SensorManager.SENSOR_STATUS_UNRELIABLE: return "Unreliable";
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW: return "Low accuracy";
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM: return "Medium accuracy";
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH: return "High accuracy";
        }
        return "Undefined";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == mAccelerometer) {
            Intent intent = new Intent(BR_NEW_ACCURACY);
            intent.putExtra(KEY_ACCELEROMETER, getAccuracy(accuracy));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else if (sensor == mMagnetometer) {
            Intent intent = new Intent(BR_NEW_ACCURACY);
            intent.putExtra(KEY_MAGNETOMETER, getAccuracy(accuracy));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
