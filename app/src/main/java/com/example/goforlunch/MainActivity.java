package com.example.goforlunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
//import com.example.go4lunch.view.RecyclerFragment;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_KEY = 42;
    private static final String TAG = "TAG";
    private SignInButton mGoogleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginButton mFacebookLoginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: mainactivity ");

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInButton = findViewById(R.id.google_login_button);
        mGoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        Log.d(TAG, "onCreate: before option");;
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        Log.d(TAG, "onCreate: client assign");
        mGoogleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN_KEY);

        });
        Log.d(TAG, "onCreate: setclicklistner");
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
        Log.d(TAG, "startMapActivity: call");
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: call");
        if (requestCode == SIGN_IN_KEY) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getIdToken());
                    firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                Log.d(TAG, "onActivityResult: resultOK");

            } else Log.d(TAG, "onActivityResult: result fail");
        } else mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d(TAG, "firebaseAuthWithGoogle: get credential");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Log.d(TAG, "onComplete: " + user.toString() + ":: " + user.getEmail());
                           // user.sendEmailVerification();

                            startMapActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }

                        // ...
                    }
                });
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
