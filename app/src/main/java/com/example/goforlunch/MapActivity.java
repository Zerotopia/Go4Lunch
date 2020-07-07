package com.example.goforlunch;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.goforlunch.view.MapFragment;
import com.example.goforlunch.view.RecyclerFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TAG";
    private BottomNavigationView mBottomNavigationView;
    private RecyclerFragment mRecyclerFragment;
    private AutocompleteSessionToken sessionToken;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;


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
    }



    private void configureBottomView() {
        Log.d(TAG, "configureBottomView: entre");
        mBottomNavigationView.setOnNavigationItemSelectedListener(item ->
                updateFragment(item.getItemId()));
    }

    public boolean updateFragment(int id) {
        switch(id) {
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
            default: return false;
        }
    }

    private void updateMapFragment () {
        MapFragment mapFragment = MapFragment.newInstance(new LatLng(0,0));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_view_container, mapFragment)
                .commit();
    }

    private void updateRecyclerFragment(boolean list) {
        mRecyclerFragment = RecyclerFragment.newInstance(list);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.top_view_container,mRecyclerFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
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

    public AutocompleteSessionToken getSessionToken() {
        return sessionToken;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_item: updateMapFragment();
            break;
            case R.id.menu_restaurant_item: updateRecyclerFragment(true);
            break;
            case R.id.menu_worker_item: updateRecyclerFragment(false);
            break;
            default: break;
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
}
