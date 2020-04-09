package com.example.go4lunch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
//import com.example.go4lunch.RecyclerFragment;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_KEY = 42;
    private static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: mainactivity ");
        startSignInActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_KEY) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);

            }
        }
    }

    private void startSignInActivity() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.login_layout)
                .setGoogleButtonId(R.id.google_login_button)
                .setFacebookButtonId(R.id.facebook_login_button)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()
                        ))
                      //  .setTheme(R.style.SignInTheme)
                        .setLogo(R.drawable.ic_graphic_3433081)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .build(), SIGN_IN_KEY);
    }
}
