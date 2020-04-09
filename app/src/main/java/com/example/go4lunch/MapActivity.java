package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private BottomNavigationView mBottomNavigationView;


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
                RecyclerFragment recyclerFragment = RecyclerFragment.newInstance(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_view_container,recyclerFragment)
                        .commit();
                return true;
            case R.id.bottom_menu_worker:
                Log.d(TAG, "updateFragment: work");
                RecyclerFragment recyclerFragments = RecyclerFragment.newInstance(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.top_view_container,recyclerFragments)
                        .commit();
                return true;
            default: return false;
        }
    }
}
