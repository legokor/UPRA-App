package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, CoordinatesReceiver {

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
        setContentView(R.layout.activity_2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
