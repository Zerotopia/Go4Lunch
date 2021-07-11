package com.example.goforlunch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.goforlunch.R;

/**
 * Activity launch by clicking on "Settings" in the NavigationDrawer.
 * Allows the user to activate/desactivate the notification.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String NOTIFICATION_ENABLE = "Notification enabled";
    private SharedPreferences mSharedPreferences;

    /**
     * We simply save the choice of the user like a boolean in the shared preferences.
     * The AlarmReceiver will be build notification or not depending of this boolean.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat switchCompat = findViewById(R.id.switch_notification);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean checked = mSharedPreferences.getBoolean(NOTIFICATION_ENABLE, true);
        switchCompat.setChecked(checked);

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putBoolean(NOTIFICATION_ENABLE, b);
            edit.apply();
        });
    }
}