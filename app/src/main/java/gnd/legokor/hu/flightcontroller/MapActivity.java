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
import com.google.android.gms.maps.model.MarkerOptions;
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
        LatLng sydney = new LatLng(47.4734, 19.0598);
        map.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void receiveCoordinates(final Coordinates coordinates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                line = line.add(new LatLng(coordinates.latitude, coordinates.longitude));
                for (LatLng point : line.getPoints()) {
                    Log.i("POINT", point.toString());
                }
                map.addPolyline(line);
                Log.i("MAIN", "Adding coordinates^");
            }
        });
    }
}
