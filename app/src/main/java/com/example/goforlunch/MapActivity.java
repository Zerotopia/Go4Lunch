package com.example.goforlunch;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.User;
import com.example.goforlunch.view.MapFragment;
import com.example.goforlunch.view.RecyclerFragment;
import com.example.goforlunch.viewmodel.PredictionViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        RecyclerFragment.AdapterListener,
        MapFragment.MapMarkerListener {

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
    private PredictionViewModel mPredictionViewModel;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private ArrayAdapter<String> mArrayAdapter;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private ArrayList<String> mData = new ArrayList<>();
    private List<AutocompletePrediction> mPredictions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mBottomNavigationView = findViewById(R.id.mainactivity_bottom_navigation);
        Log.d(TAG, "onCreate: start mapactivity");
        configureBottomView();

        mDrawerLayout = findViewById(R.id.map_activity_drawer_layout);
        mNavigationView = findViewById(R.id.map_activity_navigation_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        mPredictionViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this, "")).get(PredictionViewModel.class);
        mPredictionViewModel.init();
        observeViewModel();

    }

    private void observeViewModel() {
        mPredictionViewModel.getPredictionObservable().observe(this, this::updateResults);
        Log.d("TAG", "observeViewModel: nameobserve");
      //  mPredictionViewModel.getLocationObservable().observe(this, this::updateLocation);
    }

    private void updateLocation(LatLng latLng) {
        if (mSelectedFragment == MAP_FRAGMENT) mMapFragment.updateUI(latLng);
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
                mPredictionViewModel.newPos(pred.getPlaceId());
        });}
    }

    private void configureBottomView() {
        Log.d(TAG, "configureBottomView: entre");
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
        mMapFragment = MapFragment.newInstance(new LatLng(0, 0));
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
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
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
                        mPredictionViewModel.newQuery(newText);
                       // getPlacePredictions(newText);
                    }, 300);
                } else if (mSelectedFragment == WORKER_FRAGMENT) {
                    Log.d(TAG, "onQueryTextChange: " + (mSelectedFragment == WORKER_FRAGMENT));
                    mRecyclerFragment.updateList(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            sessionToken = AutocompleteSessionToken.newInstance();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public AutocompleteSessionToken getSessionToken() {
        return sessionToken;
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
    public void setSearchViewAdapter(List<User> users, RecyclerView recyclerView) {
//        ArrayList<String> data = new ArrayList<>();
//        for (User user : users) data.add(user.getFirstName() + " " + user.getLastName());
//        mArrayAdapter = new ArrayAdapter<>(this, R.layout.autocompletion, data);
//        mSearchAutoComplete.setAdapter(mArrayAdapter);
//        mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
//            String userData = (String) parent.getItemAtPosition(position);
//            List<User> selectedUser = new ArrayList<>();
//            selectedUser.add(users.get(data.indexOf(userData)));
//            WorkerAdapter newWorkerAdapter = new WorkerAdapter(selectedUser, false);
//            recyclerView.setAdapter(newWorkerAdapter);
//        });
        //WorkerAdapter newWorkerAdapeter = new WorkerAdapter(us)
    }

    @Override
    public void setSearchMarker(GoogleMap map) {
      //  if (mSearchAutoComplete != null)
      // mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
       //     String placeData = (String) parent.getItemAtPosition(position);
         //   AutocompletePrediction pred = mPredictions.get(mData.indexOf(placeData));
            // pred.getPlaceId()
      // });
      //  else Log.d(TAG, "setSearchMarker: setNULLL");

    }
}
