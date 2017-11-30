package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.TimeUnit;

import gnd.legokor.hu.flightcontroller.http.CoordinatesListener;
import gnd.legokor.hu.flightcontroller.model.Coordinates;
import gnd.legokor.hu.flightcontroller.service.LocationService;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, CoordinatesReceiver {

    private OkHttpClient client;
    private PolylineOptions line = null;
    private GoogleMap map = null;
    private Marker deviceLocation = null;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location currentLocation = intent.getParcelableExtra(LocationService.KEY_LOCATION);
            updateDeviceLocation(currentLocation);
            Log.i("SERVICE", "LOcation updated");
        }
    };

    private void updateDeviceLocation(Location location) {
        if (map == null) {
            return;
        }
        if (deviceLocation != null) {
            deviceLocation.remove();
        }
        deviceLocation = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You"));
        deviceLocation.showInfoWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        line = new PolylineOptions().color(Color.RED).width(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8000")
                .build();

        client.newWebSocket(request, new CoordinatesListener(this));

        Intent i = new Intent(getApplicationContext(), LocationService.class);
        startService(i);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,
                new IntentFilter(LocationService.BR_NEW_LOCATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng initialPosition = new LatLng(47.4734, 19.0598);
        map.moveCamera(CameraUpdateFactory.newLatLng(initialPosition));
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    public void receiveCoordinates(final Coordinates coordinates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("COORD", coordinates.toString());
                LatLng latLng = new LatLng(coordinates.lat, coordinates.lng);
                // TODO: remove previous line
                line = line.add(latLng);
                map.addPolyline(line);
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }
}
