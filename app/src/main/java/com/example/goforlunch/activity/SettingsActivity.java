package com.example.goforlunch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.goforlunch.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat switchCompat = findViewById(R.id.switch_notification);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean checked = mSharedPreferences.getBoolean(DetailActivity.NOTIFICATION_ENABLE,true);
        switchCompat.setChecked(checked);

        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putBoolean(DetailActivity.NOTIFICATION_ENABLE,b);
            edit.apply();
        });
    }
}