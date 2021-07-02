package com.example.goforlunch.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.goforlunch.R;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
//import com.example.go4lunch.view.RecyclerFragment;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
        {

    private static final int SIGN_IN_KEY = 42;
    private static final String TAG = "TAG";
    private Button mGoogleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Button mFacebookLoginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private LoginManager mLoginManager;
    private boolean mConnectWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: mainactivity ");

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInButton = findViewById(R.id.google_login_button);
//        int heightbefore = mGoogleSignInButton.getMinimumHeight();
//        //   mGoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        int heightafter = mGoogleSignInButton.getMinimumHeight();

        Log.d(TAG, "onCreate: before option");
        ;
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Log.d(TAG, "onCreate: client assign");
        mGoogleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, SIGN_IN_KEY);

        });
        Log.d(TAG, "onCreate: setclicklistner");

        mFacebookLoginButton = findViewById(R.id.facebook_login_button);

        mFacebookLoginButton.setOnClickListener(view -> {
            authLoginWithFacebook();
        });


        // mFacebookLoginButton.setHeight(mGoogleSignInButton.getMeasuredHeight());

//        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                startMapActivity();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });


        // startSignInActivity();
    }

    private void authLoginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        mCallbackManager = CallbackManager.Factory.create();
        Log.d(TAG, "authLoginWithFacebook: -----------------------------------------");
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mConnectWithGoogle = false;
                AccessToken access_token = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(access_token,
                        (object, response) -> {
                            try {
                              //  String email = object.getString("email");
                               // String facebook_uid = object.getString("id");
                               // String social_id = object.getString("id");
                               // String first_name = object.getString("first_name");
                               // String last_name = object.getString("last_name");
                               // String name = object.getString("name");
                                JSONObject pict = object.getJSONObject("picture");
                                JSONObject data = pict.getJSONObject("data");
                                //String link = data.getString("url");
//                                String link = object.getgetString("link");
                                setUserInfo(
                                        object.getString("id"),
                                        object.getString("name"),
                                        object.getString("email"),
                                        data.getString("url"));
                                startMapActivity();
//                                );
//                                String picture = "https://graph.facebook.com/" + facebook_uid + "/picture?type=large";
//                                Log.d(TAG,  " picture"+picture);
//                                Log.d(TAG, "onSuccess: email :: " + email);
//                                Log.d(TAG, "onSuccess: ID :: " + facebook_uid);
//                               // Log.d(TAG, "onSuccess: FNAME :: " + first_name);
//                               // Log.d(TAG, "onSuccess: LNAME :: " + last_name);
//                                Log.d(TAG, "onSuccess: name :: " + name);
//                                Log.d(TAG, "onSuccess: pict ::" + pict.toString());
//                               // Log.d(TAG, "onSuccess: pictObj :: ");
//                                Log.d(TAG, "onSuccess: link ::" + link);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "onSuccess: ERROR");
                            }



                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

                @Override
                public void onCancel () {

                }

                @Override
                public void onError (FacebookException error){

                }
            }
        );
        }

        private void startMapActivity () {
            Log.d(TAG, "startMapActivity: call");

            if (hasLocationPermissions()) {
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
            } else
                EasyPermissions.requestPermissions(
                        MainActivity.this,
                        getString(R.string.permission_message),
                        123,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
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

                } else Log.d(TAG, "onActivityResult: result fail" + resultCode);
            } else mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        private void firebaseAuthWithGoogle (String idToken){
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
                                mConnectWithGoogle = true;
                                //Log.d(TAG, "onComplete: " + user.toString() + ":: " + user.getEmail());
                                // user.sendEmailVerification();
                                //////////////////////Create User ///////////////////////////////////////////////
                                Log.d("CONNEXION", "onComplete:  before ifusernull");
                                if (user != null) {
                                    Log.d("CONNEXION", "onComplete: userOK");
                                    setUserInfo(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                    startMapActivity();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                // Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }

        private boolean hasLocationPermissions () {
            return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

        @Override
        public void onPermissionsGranted ( int requestCode, @NonNull List<String> perms){
            startMapActivity();
        }

        @Override
        public void onPermissionsDenied ( int requestCode, @NonNull List<String> perms){

        }

        private void setUserInfo(String id, String displayName, String email, String urlPhoto) {
            UserManager.getUser(id).addOnSuccessListener(documentSnapshot ->
            {
                if (documentSnapshot.toObject(User.class) == null)
                    UserManager.createUser(id, displayName, email, urlPhoto);

            });
            Log.d("CONNEXION", "onComplete:  start preference");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor edit = preferences.edit();
            Log.d("CONNEXION", "onComplete: id = " + id);
            edit.putString(MapActivity.CURRENTID, id);
            edit.apply();
        }

//        @Override
//        public void logout() {
//            if (mConnectWithGoogle) {
//                mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
//                    FirebaseAuth.getInstance().signOut();
//                });
//            }
//            else {
//                LoginManager.getInstance().logOut();
//            }
//        }


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
