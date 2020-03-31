package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_KEY = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSignInActivity();
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()
                        ))
                        .setTheme(R.style.SignInTheme)
                        .setLogo(R.drawable.ic_graphic_3433081)
                        .build(), SIGN_IN_KEY);
    }
}
