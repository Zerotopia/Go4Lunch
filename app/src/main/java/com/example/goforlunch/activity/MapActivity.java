package com.example.goforlunch.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.ListItemClickListener;
import com.example.goforlunch.R;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.view.MapFragment;
import com.example.goforlunch.view.RecyclerFragment;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

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
    private static final String TAG = "MAPACTIVITYTAG";
    private static final String RATIO = "RATING";
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

    private ArrayList<String> mData = new ArrayList<>();
    private List<AutocompletePrediction> mPredictions = new ArrayList<>();
    private List<String> mLikers = new ArrayList<>();
    private String mPlaceId;
    private SharedPreferences mPreferences;
    private String mCurrentId;

    private Toolbar mToolbar;

    private LatLng mInitialposition;
    private LocationBias mBias;

    private LocationManager mLocationManager;
  //  private List<Double> mRatioLike;

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
       // setViewModel();
        Log.d(TAG, "onResume: exit resume");
        mNetworkViewModel.initTotalUsers();
        mNetworkViewModel.getTotalUsersObservable().observe(this, this::updateTotalUsers);
        Log.d(TAG, "onResume: exit resume");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creation");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "onCreate: findviewbyid");
        mBottomNavigationView = findViewById(R.id.mainactivity_bottom_navigation);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
        mDrawerLayout = findViewById(R.id.map_activity_drawer_layout);
        mNavigationView = findViewById(R.id.map_activity_navigation_drawer);
        mToolbar = findViewById(R.id.map_activity_toolbar);
        Log.d(TAG, "onCreate: setactionbar");
        setActionBar();
        Log.d(TAG, "onCreate: setlocation");
        setLocationManager();
        Log.d(TAG, "onCreate: setnavigation");
        mNavigationView.setNavigationItemSelectedListener(this);
        Log.d(TAG, "onCreate: setviewmodel");
        setViewModel();
        Log.d(TAG, "onCreate: exitcreate");

    }

    /**
     * VIEWMODEL OBSERVERS
     */
    private void updateTotalUsers(Integer totalUsers) {
        Log.d(TAG, "updateTotalUsers: utilistion mBias");
        mNetworkViewModel.init(mCurrentId, mBias, totalUsers);
        observeViewModel();
    }

    private void observeViewModel() {
        mNetworkViewModel.getPredictionObservable().observe(this, this::updateResults);
        mNetworkViewModel.getmLikersObservable().observe(this, this::updateLikers);
        mNetworkViewModel.getLocationObservable().observe(this, this::updateLocation);
        mNetworkViewModel.getFragmentIdObservable().observe(this, this::updateFragment);
        //mNetworkViewModel.getRatioObservable().observe(this, this::updateRatioList);
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
                if (mSelectedFragment == MAP_FRAGMENT) {
                    mPlaceId = pred.getPlaceId();
                    mNetworkViewModel.newPos(pred.getPlaceId());
                } else {
                    itemClick(pred.getPlaceId());
                }

            });
        }
    }

    private void updateLikers(List<String> likers) {
        mLikers = likers;
    }

    private void updateLocation(LatLng latLng) {
        if (mSelectedFragment == MAP_FRAGMENT) mMapFragment.updateUIAutocomplete(latLng, mPlaceId);
    }


    /*******************************************************************************************/
    private void configureBottomView() {
        Log.d(TAG, "configureBottomView: entre");
       // updateMapFragment();
        Log.d(TAG, "configureBottomView: setNavigationlistner");
        mBottomNavigationView.setOnNavigationItemSelectedListener(item ->
                updateFragmentId(item.getItemId()));
    }

    private boolean updateFragmentId(int itemId) {
        Log.d(TAG, "updateFragmentId: update fragmentId");
        mNetworkViewModel.changeFragment(itemId);
        return true;
    }

    public void updateFragment(int id) {
        mNetworkViewModel.getLocationObservable().observe(this, this::updateLocation);
        switch (id) {
            case R.id.bottom_menu_map:
                Log.d(TAG, "updateFragment: map");
                updateMapFragment();
                return;
            case R.id.bottom_menu_list:
                Log.d(TAG, "updateFragment: list");
                updateRecyclerFragment(true);
                return;
            case R.id.bottom_menu_worker:
                Log.d(TAG, "updateFragment: work");
                updateRecyclerFragment(false);
                return;
            default:
        }
    }

    private void updateMapFragment() {
        Log.d(TAG, "updateMapFragment: utilisation mIitialisation");
        if (mInitialposition != null) {
            mMapFragment = MapFragment.newInstance(mInitialposition);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.top_view_container, mMapFragment)
                    .commit();
        }
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
/**************************************************************************/
    /**
     * INTERFACE IMPLEMENTATION
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        initSearchView(mSearchView);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            sessionToken = AutocompleteSessionToken.newInstance();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

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


    @Override
    public void itemClick(String placeId) {
        mNetworkViewModel.newPos(placeId);
        Intent intent = new Intent(this, DetailActivity.class);
//      intent.putExtra(MapActivity.URL_IMAGE, url);
//      intent.putExtra(MapActivity.NAME_RESTAURANT, place.getName());
        intent.putExtra(MapActivity.UID_RESTAURANT, placeId);
//      intent.putExtra(MapActivity.ADDR_RESTAURANT, place.getAddress());
        intent.putExtra(MapActivity.LIST_LIKERS, (ArrayList<String>) mLikers);
        //intent.putExtra(MapActivity.RATIO,mR)
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: Initialization mInitialization");
        mInitialposition = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d(TAG, "remove mLocation manager");
        mLocationManager.removeUpdates(this);
        Log.d(TAG, "onLocationChanged: Initialisation mBias");
        mBias = restaurantsArea(mInitialposition);
        Log.d(TAG, "onLocationChanged: configure bottomview");
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

    public LocationBias getBias() {
        return mBias;
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    /***************************************************************/
    /**
     * annexe functions
     */

    private void setActionBar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @SuppressLint("MissingPermission")
    private void setLocationManager() {
//        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

//        } else {
//            EasyPermissions.requestPermissions(this, "Need Location", 123, perms);
//        }
    }

    private void setViewModel() {
        mNetworkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(NetworkViewModel.class);
        mNetworkViewModel.initTotalUsers();
        if (mNetworkViewModel.getFragmentIdObservable() == null) {
            mNetworkViewModel.initFragment(R.id.bottom_menu_map);
            Log.d(TAG, "setViewModel: null");
        } else Log.d(TAG, "setViewModel: nonnull");

        mNetworkViewModel.getTotalUsersObservable().observe(this, this::updateTotalUsers);
    }

    public void initSearchView(SearchView searchView) {
        mSearchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchView.setQueryHint(getString(R.string.search_hint));
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
                if ((mSelectedFragment == MAP_FRAGMENT) || (mSelectedFragment == RESTAURANT_FRAGMENT)) {
                    handler.removeCallbacksAndMessages(null);

                    handler.postDelayed(() -> {
                        mNetworkViewModel.newQuery(newText);
                    }, 300);
                } else if (mSelectedFragment == WORKER_FRAGMENT) {
                    mRecyclerFragment.updateList(newText);
                }
                return true;
            }
        });
    }

    private LocationBias restaurantsArea(LatLng latLng) {
        double delta = 0.01;
        return RectangularBounds.newInstance(
                new LatLng(latLng.latitude - delta, latLng.longitude - delta),
                new LatLng(latLng.latitude + delta, latLng.longitude + delta));
    }

    public interface InterfaceListener {
       void updateUI (NearByPlace nearbyPlace, List<String> reservedRestaurant);
    }
}
