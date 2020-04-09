package com.example.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
public static final String TAG = "TAG";
    
    private static final String LATITUDE = "LAT";
    private static final String LONGITUDE = "LNG";
    private GoogleMap mMap;

    @NonNull
    public static MapFragment newInstance(LatLng coordinate) {
        MapFragment mapFragment = new MapFragment();
        Bundle arg = new Bundle();
        arg.putDouble(LATITUDE, coordinate.latitude);
        arg.putDouble(LONGITUDE, coordinate.longitude);
        mapFragment.setArguments(arg);
        return mapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mapView = inflater.inflate(R.layout.map,container,false);
        //if (getFragmentManager() != null) {
            Log.d(TAG, "onCreateView: not null ");
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_fragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
                Log.d(TAG, "onCreateView: nonnll nonnull");
            }
            else Log.d(TAG, "onCreateView: nonnull null");
       // }
     //   else Log.d(TAG, "onCreateView: null");
        return mapView;
    }

    private LatLng initialPosition() {
        if (getArguments() != null)
        return new LatLng(getArguments().getDouble(LATITUDE), getArguments().getDouble(LONGITUDE));
        else return new LatLng(47.365682, 0.745302);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: entre");
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(initialPosition()).title("Centre du monde"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(4));
    }


}
