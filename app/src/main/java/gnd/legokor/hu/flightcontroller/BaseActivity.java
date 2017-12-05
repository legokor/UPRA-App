package gnd.legokor.hu.flightcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import gnd.legokor.hu.flightcontroller.http.CoordinatesListener;
import gnd.legokor.hu.flightcontroller.model.Coordinates;
import gnd.legokor.hu.flightcontroller.service.LocationService;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CoordinatesReceiver {

    private int layout;
    private OkHttpClient client;

    public void setLayout(int l) {
        layout = l;
    }

    @Override
    public abstract void receiveUavCoordinates(final Coordinates coordinates);

    @Override
    public abstract void onConnectionFailure();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location currentLocation = intent.getParcelableExtra(LocationService.KEY_LOCATION);
            receiveDeviceCoordinates(currentLocation);
        }
    };

    protected abstract void receiveDeviceCoordinates(Location currentLocation);

    @Override
    protected void onResume() {
        super.onResume();
        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            Request request = new Request.Builder()
                    .url(preferences.getString("server_url", ""))
                    .build();
            client.newWebSocket(request, new CoordinatesListener(this));
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, R.string.malformedUrl, Toast.LENGTH_LONG).show();
        }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_antenna) {
            Intent intent = new Intent(this, AntennaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            intentSettings.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT,
                    SettingsActivity.FragmentSettingsBasic.class.getName());
            intentSettings.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            intentSettings.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intentSettings);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}