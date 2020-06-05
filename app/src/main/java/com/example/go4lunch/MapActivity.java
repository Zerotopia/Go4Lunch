package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunch.view.MapFragment;
import com.example.go4lunch.view.RecyclerFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private BottomNavigationView mBottomNavigationView;
    private RecyclerFragment mRecyclerFragment;
    private AutocompleteSessionToken sessionToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mBottomNavigationView = findViewById(R.id.mainactivity_bottom_navigation);
        Log.d(TAG, "onCreate: start mapactivity");
        configureBottomView();
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
                MapFragment mapFragment = MapFragment.newInstance(new LatLng(0,0));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_view_container, mapFragment)
                        .commit();
                return true;
            case R.id.bottom_menu_list:
                Log.d(TAG, "updateFragment: list");
                mRecyclerFragment = RecyclerFragment.newInstance(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_view_container,mRecyclerFragment)
                        .commit();
                return true;
            case R.id.bottom_menu_worker:
                Log.d(TAG, "updateFragment: work");
                mRecyclerFragment = RecyclerFragment.newInstance(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_view_container,mRecyclerFragment)
                        .commit();
                return true;
            default: return false;
        }
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
}
