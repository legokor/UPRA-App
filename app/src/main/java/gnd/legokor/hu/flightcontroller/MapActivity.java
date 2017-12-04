package gnd.legokor.hu.flightcontroller;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import gnd.legokor.hu.flightcontroller.model.Coordinates;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private PolylineOptions line = null;
    private GoogleMap map = null;
    private Marker deviceLocation = null;

    protected void receiveDeviceCoordinates(Location location) {
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
        setLayout(R.layout.activity_map);
        super.onCreate(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        line = new PolylineOptions().color(Color.RED).width(5);
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
    public void receiveUavCoordinates(final Coordinates coordinates) {
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

    @Override
    public void onConnectionFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MapActivity.this, "Failed to connect to the server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
