package com.example.goforlunch.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.goforlunch.R;
import com.example.goforlunch.repository.UserManager;
import com.example.goforlunch.databinding.ActivityMainBinding;
import com.example.goforlunch.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Connexion screen
 * This activity implements EasyPermissions.PermissionCallbacks to manage
 * the permission about the location of the user.
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> mResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivity(this);

        mAuth = FirebaseAuth.getInstance();
        initGoogleSignIn();
    }

    /**
     * Initialize googleSignInClient and configure callback for google Sign In.
     */
    private void initGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuth(GoogleAuthProvider.getCredential(account.getIdToken(), null));
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(this, R.string.google_signin_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Google button click.
     */
    public void authLoginWithGoogle() {
        mResultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    /**
     * Facebook button click.
     */
    public void authLoginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken access_token = loginResult.getAccessToken();
                        firebaseAuth(FacebookAuthProvider.getCredential(access_token.getToken()));
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, R.string.facebook_cancel, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, R.string.facebook_error, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /**
     * Call back for Facebook SignIn.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager != null)
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Firebase authentication.
     */
    private void firebaseAuth(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            setUserInfo(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                            startMapActivity();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    /**
     * @param id          user id
     * @param displayName user name
     * @param email       user email
     * @param urlPhoto    user profil image
     *                    <p>
     *                    If it is the first connexion of the user then we create the user in firebase database.
     *                    Save user info in Shared Preferences.
     */
    private void setUserInfo(String id, String displayName, String email, String urlPhoto) {
        UserManager.getUser(id).addOnSuccessListener(documentSnapshot ->
        {
            if (documentSnapshot.toObject(User.class) == null)
                UserManager.createUser(id, displayName, email, urlPhoto);

        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(MapActivity.CURRENTID, id);
        edit.putString(MapActivity.CURRENTNAME, displayName);
        edit.putString(MapActivity.CURRENTMAIL, email);
        edit.putString(MapActivity.CURRENTPHOTO, urlPhoto);
        edit.apply();
    }

    /**
     * If we have the location permission we start map activity.
     */
    private void startMapActivity() {
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

    /**
     * @return true if we have the location permission else return false.
     */
    private boolean hasLocationPermissions() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Implementation of EasyPermission interface
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startMapActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
    }
}
