package com.example.goforlunch.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.interfaces.ListItemClickListener;
import com.example.goforlunch.R;
import com.example.goforlunch.databinding.HeaderBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.User;
import com.example.goforlunch.view.MapFragment;
import com.example.goforlunch.view.RecyclerFragment;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that manages :
 * -display around restaurants on a map
 * -display around restaurants on a recyclerView
 * -display workmates on a recyclerView
 * -the research of a restaurant or workmate
 * -A NavigationDrawer menu.
 * This activity implements three interfaces :
 * -OnNavigationItemSelectedListener for the NavigationDrawer
 * -LocationListener to know the location of the user
 * -ListItemClickListener : an Interface to manage the click on a restaurant
 * either the click was perform on the map or recyclerView.
 */
public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener,
        ListItemClickListener {

    /**
     * Intent TAG for detail activity
     */
    public static final String LIST_LIKERS = "LIKERS";
    public static final String UID_RESTAURANT = "UID";

    /**
     * Shared Preferences TAG
     */
    public static final String CURRENTID = "USER_ID";
    public static final String CURRENTMAIL = "USER EMAIL";
    public static final String CURRENTPHOTO = "USER PHOTO";
    public static final String CURRENTNAME = "USER NAME";

    /**
     * Fragment identifier.
     */
    public static final int MAP_FRAGMENT = 0;
    public static final int RESTAURANT_FRAGMENT = 1;
    public static final int WORKER_FRAGMENT = 2;
    public static final int SORT_NAME_LIST = 5;
    public static final int SORT_RATIO_LIST = 6;
    public static final int SORT_HEADCOUNT_LIST = 7;
    public static final int SORT_DISTANCE_LIST = 8;

    private BottomNavigationView mBottomNavigationView;
    private RecyclerFragment mRecyclerFragment;
    private MapFragment mMapFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    private NetworkViewModel mNetworkViewModel;
    private LocationManager mLocationManager;
    private SharedPreferences mPreferences;

    /**
     * Search autocomplete.
     */
    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private ArrayList<String> mData = new ArrayList<>();
    private List<AutocompletePrediction> mPredictions = new ArrayList<>();
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    /**
     * Id of the place selected by the user.
     * Send to DetailActivity.
     */
    private String mPlaceId;

    /**
     * Id of the current user.
     */
    private String mCurrentId;
    /**
     * Id of the current restaurant choice by the user to lunch.
     * Need to "YOUR LUNCH"
     */
    private String mCurrentRestaurantId;

    /**
     * Geolocations of the user.
     */
    private LatLng mInitialPosition;
    /**
     * Area show by the map.
     */
    private LocationBias mBias;

    /**
     * Intent for start detailActivity when the user click on an item.
     */
    private Intent mIntent;
    /**
     * Boolean determine if the user has clicked on an item or not
     * Use to execute a code in an observable only if its true.
     */
    private boolean mItemclick = false;

    /**
     * Integer to know if we display the MAP, the list of restaurants or the workmates.
     */
    private int mSelectedFragment;

    /**
     * We initialize and observe the view model in onResume to continue
     * to observe the view model even if the method onCreate is not call.
     * Typically when we come on the map activity after a back pressed button.
     * <p>
     * We recover, if required, the id of the restaurant where the user want to be for lunch.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setViewModel();
        if (mBias != null) {
            initViewModel();
            observeViewModel();
        }
        mCurrentRestaurantId = mPreferences.getString(DetailActivity.CURRENT_RESTAURANT, "");
        mIntent = new Intent(this, DetailActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bindView();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");

        setNavigationDrawerHeader();
        mNavigationView.setNavigationItemSelectedListener(this);

        setActionBar();
        setLocationManager();

    }

    private void bindView() {
        mBottomNavigationView = findViewById(R.id.mainactivity_bottom_navigation);
        mDrawerLayout = findViewById(R.id.map_activity_drawer_layout);
        mNavigationView = findViewById(R.id.map_activity_navigation_drawer);
        mToolbar = findViewById(R.id.map_activity_toolbar);
    }

    /**
     * I use databinding to fill the hedar of the NavigationDrawer with good informations.
     */
    private void setNavigationDrawerHeader() {
        String currentName = mPreferences.getString(MapActivity.CURRENTNAME, "");
        String currentEmail = mPreferences.getString(MapActivity.CURRENTMAIL, "");
        String currentPhoto = mPreferences.getString(MapActivity.CURRENTPHOTO, "");
        User user = new User(currentName, currentEmail, currentPhoto);

        HeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header, mNavigationView, true);
        headerBinding.setUser(user);
        headerBinding.executePendingBindings();
    }

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

    /**
     * Display the "sort icon" only on "restaurant fragmen"
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSelectedFragment != RESTAURANT_FRAGMENT)
            menu.findItem(R.id.sort_item).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }


    @SuppressLint("MissingPermission")
    private void setLocationManager() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /**
     * VIEW MODEL
     */

    private void setViewModel() {
        mNetworkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(NetworkViewModel.class);
        if (mNetworkViewModel.getFragmentIdObservable() == null)
            mNetworkViewModel.initFragment(R.id.bottom_menu_map);
    }

    private void initViewModel() {
        mNetworkViewModel.initTotalUsers();
        mNetworkViewModel.init(mCurrentId, mBias);
        mNetworkViewModel.initReservedRestaurant();
    }

    private void observeViewModel() {
        mNetworkViewModel.getTotalUsersObservable().observe(this, this::updateTotalUsers);
        mNetworkViewModel.getPredictionObservable().observe(this, this::updateResults);
        mNetworkViewModel.getmLikersObservable().observe(this, this::updateLikers);
        mNetworkViewModel.getLocationObservable().observe(this, this::updateLocation);
        mNetworkViewModel.getFragmentIdObservable().observe(this, this::updateFragment);
    }

    private void updateTotalUsers(Integer totalUsers) {
        mNetworkViewModel.setTotalusers(totalUsers);
    }

    /**
     * Observed prediction for the search feature.
     * We set predictions in an adapter.
     * The adapter allows searchAutocomplete to display
     * the results of the research.
     * <p>
     * We set click listener to manage the click on one
     * result displayed.
     *
     * @param predictions list of place return by AutocompletePrediction
     *                    when the user search a restaurant.
     */
    private void updateResults(List<AutocompletePrediction> predictions) {
        if ((mSelectedFragment == MAP_FRAGMENT) || (mSelectedFragment == RESTAURANT_FRAGMENT)) {
            mData.clear();
            mPredictions.clear();
            mPredictions = predictions;
            for (AutocompletePrediction pred : predictions) {
                mData.add(pred.getFullText(STYLE_BOLD).toString());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.autocompletion, mData);
            mSearchAutoComplete.setAdapter(arrayAdapter);

            mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                String placeData = (String) parent.getItemAtPosition(position);
                AutocompletePrediction pred = mPredictions.get(mData.indexOf(placeData));
                if (mSelectedFragment == MAP_FRAGMENT) {
                    mPlaceId = pred.getPlaceId();
                    mNetworkViewModel.newPos(pred.getPlaceId());
                } else itemClick(pred.getPlaceId());
                mSearchView.setIconified(true);
            });
        }
    }

    private void updateLikers(List<String> likers) {
        if (mItemclick) {
            mIntent.putExtra(MapActivity.LIST_LIKERS, (ArrayList<String>) likers);
            startActivity(mIntent);
            mItemclick = false;
        }
    }

    /**
     * Centralise la carte sur la position latlng et n'affiche que le restaurant
     * sélectionné.
     *
     * @param latLng position of the selected restaurant in search autocomplete
     */
    private void updateLocation(LatLng latLng) {
        if (mSelectedFragment == MAP_FRAGMENT) mMapFragment.updateUIAutocomplete(latLng, mPlaceId);
    }

    /**
     * Display appropriate fragment.
     */
    public void updateFragment(int id) {
        if (id == R.id.bottom_menu_map) {
            updateMapFragment();
        } else if (id == R.id.bottom_menu_list) {
            updateRecyclerFragment(RESTAURANT_FRAGMENT);
        } else if (id == R.id.bottom_menu_worker) {
            updateRecyclerFragment(WORKER_FRAGMENT);
        } else if (id == R.id.sort_name_item) {
            updateRecyclerFragment(SORT_NAME_LIST);
        } else if (id == R.id.sort_ratio_item) {
            updateRecyclerFragment(SORT_RATIO_LIST);
        } else if (id == R.id.sort_headcount_item) {
            updateRecyclerFragment(SORT_HEADCOUNT_LIST);
        } else if (id == R.id.sort_distance_item) {
            updateRecyclerFragment(SORT_DISTANCE_LIST);
        }
    }

    private void configureBottomView() {
        mBottomNavigationView.setOnItemSelectedListener(item -> updateFragmentId(item.getItemId()));
    }

    private boolean updateFragmentId(int itemId) {
        mNetworkViewModel.changeFragment(itemId);
        return true;
    }

    /**
     * Fragment that display the map.
     */
    private void updateMapFragment() {
        getSupportActionBar().setTitle(R.string.hungry);
        if (mInitialPosition != null) {
            mMapFragment = MapFragment.newInstance(mInitialPosition);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.top_view_container, mMapFragment)
                    .commit();
        }
        mSelectedFragment = MAP_FRAGMENT;
    }

    /**
     * Fragment that display the recyclerview.
     * the content of the recyclerview depend of the int"list"
     *
     * @param list int that determined if we display workmates or restaurants
     */
    private void updateRecyclerFragment(int list) {
        if (list == WORKER_FRAGMENT) {
            getSupportActionBar().setTitle(R.string.available_workmates);
            mSelectedFragment = WORKER_FRAGMENT;
        } else {
            getSupportActionBar().setTitle(R.string.hungry);
            mSelectedFragment = RESTAURANT_FRAGMENT;
        }
        mRecyclerFragment = RecyclerFragment.newInstance(list, mInitialPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_view_container, mRecyclerFragment)
                .commit();
    }

    /**
     * Initialize the menu and the searchView.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        initSearchView();
        return true;
    }

    public void initSearchView() {
        mSearchAutoComplete = mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((mSelectedFragment == MAP_FRAGMENT) || (mSelectedFragment == RESTAURANT_FRAGMENT)) {
                    mNetworkViewModel.newQuery(newText);
                } else if (mSelectedFragment == WORKER_FRAGMENT) {
                    mRecyclerFragment.updateList(newText);
                }
                return true;
            }
        });
    }

    /**
     * Interface that manage the selection of an item in the menu.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mNetworkViewModel.changeFragment(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /**
     * Interface that manage the selection of an item in the navigation drawer
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_lunch_item) {
            if ((mCurrentRestaurantId != null) && (!mCurrentRestaurantId.isEmpty()))
                itemClick(mCurrentRestaurantId);
            else Toast.makeText(this, R.string.no_restaurant, Toast.LENGTH_LONG).show();
        } else if (itemId == R.id.menu_settings_item) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_logout_item) {
            FirebaseAuth.getInstance().signOut();
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            finish();
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

    /**
     * Implementation of the ListItemClickListener
     *
     * @param placeId Id of the place that will be detailed in the detail activity
     */
    @Override
    public void itemClick(String placeId) {
        mItemclick = true;
        mNetworkViewModel.newPos(placeId);
        mIntent.putExtra(MapActivity.UID_RESTAURANT, placeId);
    }

    /**
     * Implementation of LocationListener
     *
     * @param location geolocation of the user
     */
    @Override
    public void onLocationChanged(Location location) {
        mInitialPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mLocationManager.removeUpdates(this);
        mBias = restaurantsArea(mInitialPosition);

        initViewModel();
        observeViewModel();
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

    private LocationBias restaurantsArea(LatLng latLng) {
        double delta = 0.01;
        return RectangularBounds.newInstance(
                new LatLng(latLng.latitude - delta, latLng.longitude - delta),
                new LatLng(latLng.latitude + delta, latLng.longitude + delta));
    }
}