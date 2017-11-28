package gnd.legokor.hu.flightcontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.TimeUnit;

import gnd.legokor.hu.flightcontroller.http.CoordinatesListener;
import gnd.legokor.hu.flightcontroller.model.Coordinates;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, CoordinateReceiver {

    private OkHttpClient client;
    private PolylineOptions line = null;
    private GoogleMap map = null;

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
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8000")
                .build();

        client.newWebSocket(request, new CoordinatesListener(this));
    }

    @Override
    protected void onPause() {
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
