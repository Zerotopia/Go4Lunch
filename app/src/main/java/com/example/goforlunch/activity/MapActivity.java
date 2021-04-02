package com.example.goforlunch.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.goforlunch.ListItemClickListener;
import com.example.goforlunch.NotificationWorker;
import com.example.goforlunch.R;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.view.MapFragment;
import com.example.goforlunch.view.RecyclerFragment;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.example.goforlunch.viewmodel.PredictionViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        //  RecyclerFragment.AdapterListener,
        //   MapFragment.MapMarkerListener,
        LocationListener,
        ListItemClickListener {

    public static final String URL_IMAGE = "URLIMAGE";
    public static final String NAME_RESTAURANT = "NAMERESTAURANT";
    public static final String ADDR_RESTAURANT = "ADDRESSE";
    public static final String LIST_LIKERS = "LIKERS";
    public static final String UID_RESTAURANT = "UID";
    public static final String CURRENTID = "USER_ID";
    private static final String TAG = "TAG";
    private BottomNavigationView mBottomNavigationView;
    private RecyclerFragment mRecyclerFragment;
    private MapFragment mMapFragment;
    private AutocompleteSessionToken sessionToken;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    public static final int MAP_FRAGMENT = 0;
    public static final int RESTAURANT_FRAGMENT = 1;
    public static final int WORKER_FRAGMENT = 2;
    private int mSelectedFragment;
    private NetworkViewModel mNetworkViewModel;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private ArrayAdapter<String> mArrayAdapter;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    // private FragmentViewModelListener mFragmentViewModelListener;

    private ArrayList<String> mData = new ArrayList<>();
    private List<AutocompletePrediction> mPredictions = new ArrayList<>();
    private List<String> mLikers = new ArrayList<>();
    private String mPlaceId;
    private SharedPreferences mPreferences;
    private String mCurrentId;

    private Toolbar mToolbar;

    private LatLng mInitialposition;
    private LocationBias mBias;

    //private FusedLocationProviderClient mFusedLocationProviderClient;
    LocationManager mLocationManager;

    @Override
    protected void onResume() {
        super.onResume();
        mNetworkViewModel.initTotalUsers();
        mNetworkViewModel.getTotalUsersObservable().observe(this,this::updateTotalUsers);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        mBottomNavigationView = findViewById(R.id.mainactivity_bottom_navigation);
        Log.d(TAG, "onCreate: start mapactivity");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
        Log.d(TAG, "onActivityCreated: ID" + mCurrentId);
       // configureBottomView();
        //  mFragmentViewModelListener = (mSelectedFragment == MAP_FRAGMENT) ? mMapFragment : mRecyclerFragment;
        Log.d("TAGGGGGGGGGGG", "onCreate: interface : " + (mSelectedFragment == MAP_FRAGMENT));

        mDrawerLayout = findViewById(R.id.map_activity_drawer_layout);
        mNavigationView = findViewById(R.id.map_activity_navigation_drawer);
        mToolbar = findViewById(R.id.map_activity_toolbar);

        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //mInitialposition =  new LatLng(47.390289, 0.688850);
        //mBias = restaurantsArea(mInitialposition);
//                RectangularBounds.newInstance(
//            new LatLng(47.38545, 0.67909), // SW lat, lng
//
//            new LatLng(47.39585, 0.69519) );// NE lat, lng
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        } else {
            EasyPermissions.requestPermissions(this, "Need Location", 123, perms);
        }

        mNavigationView.setNavigationItemSelectedListener(this);

        mNetworkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(NetworkViewModel.class);

        //mNetworkViewModel.init(mCurrentId, mBias);
       // observeViewModel();



        // createNotificationChannel();
        //NotificationWorker notificationWorker = new NotificationWorker(this, )

//        Calendar currentDate = Calendar.getInstance();
//        Calendar notificationDate = Calendar.getInstance();
//
//        notificationDate.set(Calendar.HOUR_OF_DAY, 12);
//        notificationDate.set(Calendar.SECOND, 0);
//        notificationDate.set(Calendar.MINUTE, 0);
//
//        if (notificationDate.before(currentDate))
//            notificationDate.add(Calendar.HOUR, 24);
//
//        long delay = notificationDate.getTimeInMillis() - currentDate.getTimeInMillis();
//
////        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
////        NotificationCompat.Builder notificationBuild =
////                new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
////                        .setSmallIcon(R.drawable.ic_baseline_check_24)
////                        .setContentTitle("notif titre")
////                        .setContentText("Une notification")
////                        .setPriority(NotificationCompat.PRIORITY_HIGH);
////        notificationManager.notify(42, notificationBuild.build());
//
//
//        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequest);

    }

    private void updateTotalUsers(Integer totalUsers) {
        mNetworkViewModel.init(mCurrentId,mBias,totalUsers);
        observeViewModel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "CHANNEL",
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CHANNEL_DESCRIPTION");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    public NetworkViewModel getNetworkViewModel() {
        return mNetworkViewModel;
    }

    private void observeViewModel() {
        mNetworkViewModel.getPredictionObservable().observe(this, this::updateResults);
        mNetworkViewModel.getmLikersObservable().observe(this, this::updateLikers);
        Log.d("TAG", "observeViewModel: nameobserve");
        mNetworkViewModel.getLocationObservable().observe(this, this::updateLocation);
        // mFragmentViewModelListener.observeFragmentViewModel(mNetworkViewModel);

    }

    private void updateLikers(List<String> likers) {
        mLikers = likers;
    }

    private void updateLocation(LatLng latLng) {
        if (mSelectedFragment == MAP_FRAGMENT) mMapFragment.updateUI(latLng, mPlaceId);
    }

    private void updateResults(List<AutocompletePrediction> predictions) {
        if ((mSelectedFragment == MAP_FRAGMENT) || (mSelectedFragment == RESTAURANT_FRAGMENT)) {
            mData.clear();
            mPredictions.clear();
            mPredictions = predictions;
            for (AutocompletePrediction pred : predictions) {
                mData.add(pred.getFullText(STYLE_BOLD).toString());
            }
            mArrayAdapter = new ArrayAdapter<>(this, R.layout.autocompletion, mData);
            mSearchAutoComplete.setAdapter(mArrayAdapter);
            mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                String placeData = (String) parent.getItemAtPosition(position);
                AutocompletePrediction pred = mPredictions.get(mData.indexOf(placeData));

                Log.d(TAG, "updateResults: "
                        + parent.toString() + ";;;"
                        + view.toString() + "::::"
                        + position + ":::::"
                        + id);
                if (mSelectedFragment == MAP_FRAGMENT) {
                    mPlaceId = pred.getPlaceId();
                    mNetworkViewModel.newPos(pred.getPlaceId());
                } else {
                    Log.d(TAG, "updateResults: startactivityyyyyyyyyyyyyyyyyyyy");
                    itemClick(pred.getPlaceId());

                }

            });
        }
    }

    private void configureBottomView() {
        Log.d(TAG, "configureBottomView: entre");
        updateMapFragment();
        mBottomNavigationView.setOnNavigationItemSelectedListener(item ->
                updateFragment(item.getItemId()));
    }

    public boolean updateFragment(int id) {
        switch (id) {
            case R.id.bottom_menu_map:
                Log.d(TAG, "updateFragment: map");
                updateMapFragment();
                return true;
            case R.id.bottom_menu_list:
                Log.d(TAG, "updateFragment: list");
                updateRecyclerFragment(true);
                return true;
            case R.id.bottom_menu_worker:
                Log.d(TAG, "updateFragment: work");
                updateRecyclerFragment(false);
                return true;
            default:
                return false;
        }
    }

    private void updateMapFragment() {
        mMapFragment = MapFragment.newInstance(mInitialposition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_view_container, mMapFragment)
                .commit();
        mSelectedFragment = MAP_FRAGMENT;
    }

    private void updateRecyclerFragment(boolean list) {
        mRecyclerFragment = RecyclerFragment.newInstance(list);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_view_container, mRecyclerFragment)
                .commit();
        mSelectedFragment = (list) ? RESTAURANT_FRAGMENT : WORKER_FRAGMENT;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        initSearchView(mSearchView);
        return true;
    }

    public void initSearchView(SearchView searchView) {
        mSearchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchView.setQueryHint("Search");
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //  progressBar.setIndeterminate(true);
                if ((mSelectedFragment == MAP_FRAGMENT) || (mSelectedFragment == RESTAURANT_FRAGMENT)) {
                    // Cancel any previous place prediction requests
                    handler.removeCallbacksAndMessages(null);

                    // Start a new place prediction request in 300 ms
                    handler.postDelayed(() -> {
                        mNetworkViewModel.newQuery(newText);
                        // getPlacePredictions(newText);
                    }, 300);
                } else if (mSelectedFragment == WORKER_FRAGMENT) {
                    Log.d(TAG, "onQueryTextChange: " + (mSelectedFragment == WORKER_FRAGMENT));
                    mRecyclerFragment.updateList(newText);
                }
                return true;
            }
        });
//        mSearchAutoComplete.setOnItemClickListener((adapterView, view, pos, id) -> {
//            Log.d(TAG, "initSearchView: " + adapterView.toString() + pos + id);
//           // Toast.makeText(this,adapterView.toString() + pos + id,Toast.LENGTH_SHORT);
//
//        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            sessionToken = AutocompleteSessionToken.newInstance();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
//
//    public AutocompleteSessionToken getSessionToken() {
//        return sessionToken;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_item:
                updateMapFragment();
                break;
            case R.id.menu_restaurant_item:
                updateRecyclerFragment(true);
                break;
            case R.id.menu_worker_item:
                updateRecyclerFragment(false);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

//    @Override
//    public void setSearchViewAdapter(List<User> users, RecyclerView recyclerView) {
//        ArrayList<String> data = new ArrayList<>();
//        for (User user : users) data.add(user.getUserName());
//        mArrayAdapter = new ArrayAdapter<>(this, R.layout.autocompletion, data);
//        mSearchAutoComplete.setAdapter(mArrayAdapter);
//        mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
//            String userData = (String) parent.getItemAtPosition(position);
//            List<User> selectedUser = new ArrayList<>();
//            selectedUser.add(users.get(data.indexOf(userData)));
//            WorkerAdapter newWorkerAdapter = new WorkerAdapter(selectedUser, false);
//            recyclerView.setAdapter(newWorkerAdapter);
//        });
//        //WorkerAdapter newWorkerAdapeter = new WorkerAdapter(us)
//    }

//    @Override
//    public void setSearchMarker(GoogleMap map) {
////        if (mSearchAutoComplete != null)
////            mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
////                String placeData = (String) parent.getItemAtPosition(position);
////                AutocompletePrediction pred = mPredictions.get(mData.indexOf(placeData));
////                itemClick(pred.getPlaceId());
////
////            });
////        else Log.d(TAG, "setSearchMarker: setNULLL");
//
//    }

    @Override
    public void itemClick(String placeId) {
        mNetworkViewModel.newPos(placeId);
        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(MapActivity.URL_IMAGE, url);
//        intent.putExtra(MapActivity.NAME_RESTAURANT, place.getName());
        intent.putExtra(MapActivity.UID_RESTAURANT, placeId);
//        intent.putExtra(MapActivity.ADDR_RESTAURANT, place.getAddress());
        intent.putExtra(MapActivity.LIST_LIKERS, (ArrayList<String>) mLikers);
        Log.d("TAG", "onrestaurantclick: before detailactivity : url ");
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
         mInitialposition = new LatLng(location.getLatitude(), location.getLongitude());
        //mInitialposition =  new LatLng(47.390289, 0.688850);
        Log.d(TAG, "onLocationChanged: ----------------------------------------------------");
        Log.d(TAG, "onLocationChanged: " + mInitialposition.toString());
        Log.d(TAG, "onLocationChanged: ++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        mLocationManager.removeUpdates(this);
       // mLocationManager = null;
        mBias = restaurantsArea(mInitialposition);
        configureBottomView();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public interface FragmentViewModelListener {
        void observeFragmentViewModel(NetworkViewModel networkViewModel);
    }


    public LatLng getInitialposition() {
        return mInitialposition;
    }

    public LocationBias getBias() {
        return mBias;
    }

    private LocationBias restaurantsArea(LatLng latLng) {
        double delta = 0.01;
        return RectangularBounds.newInstance(
                new LatLng(latLng.latitude - delta, latLng.longitude - delta),
                new LatLng(latLng.latitude + delta, latLng.longitude + delta));
    }
}
