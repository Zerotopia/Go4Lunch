package com.example.go4lunch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
//import com.example.go4lunch.view.RecyclerFragment;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_KEY = 42;
    private static final String TAG = "TAG";
    private SignInButton mGoogleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginButton mFacebookLoginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: mainactivity ");

        mGoogleSignInButton = findViewById(R.id.google_login_button);
        mGoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_KEY);

            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = findViewById(R.id.facebook_login_button);

        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            startMapActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


       // startSignInActivity();
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_KEY) {
            if (resultCode == RESULT_OK) {
                startMapActivity();
            }
        } else mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

//    private void startSignInActivity() {
//        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
//                .Builder(R.layout.login_layout)
//                .setGoogleButtonId(R.id.google_login_button)
//                .setFacebookButtonId(R.id.facebook_login_button)
//                .build();
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(Arrays.asList(
//                                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                new AuthUI.IdpConfig.FacebookBuilder().build()
//                        ))
//                      //  .setTheme(R.style.SignInTheme)
//                        //.setLogo(R.drawable.ic_graphic_3433081)
//                        .setAuthMethodPickerLayout(authMethodPickerLayout)
//                        .build(), SIGN_IN_KEY);
//    }
}
