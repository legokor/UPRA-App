package gnd.legokor.hu.flightcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_NO_HEADERS = ":android:no_headers";
    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
    public static final String KEY_START_SERVICE = "start_service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(
                this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(
                this).unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_START_SERVICE.equals(key)) {
            boolean startService = sharedPreferences.getBoolean(KEY_START_SERVICE, false);
            // TODO: Service indítása/leállítása
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.fragmentsettings, target);
    }

    public static class FragmentSettingsBasic extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mainsettings);
        }
    }

}