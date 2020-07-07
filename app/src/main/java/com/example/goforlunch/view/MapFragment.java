package com.example.goforlunch.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.R;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.viewmodel.NetworkViewModel;
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
    private NearByPlace mNearByPlace;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: mapfragment super");
        final NetworkViewModel networkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        Log.d(TAG, "onActivityCreated: mapfragment viewModel");
        networkViewModel.init();
        Log.d(TAG, "onActivityCreated: mapfragment init");
        observeViewModel(networkViewModel);
        Log.d(TAG, "onActivityCreated: mapfragment observe");
    }

    private void observeViewModel(NetworkViewModel networkViewModel) {
        Log.d(TAG, "observeViewModel: mapfragment in observe");
        networkViewModel.getNetworkObservable().observe(this, this::updateNearByPlace);
        Log.d(TAG, "observeViewModel: mapgrgment fin observe");
           }

    private void updateNearByPlace(NearByPlace nearByPlace) {
        mNearByPlace = nearByPlace;
        Log.d(TAG, "updateNearByPlace: mapfragment");
        if ((mMap != null)) {
            Log.d(TAG, "updateNearByPlace: mmapnn mapfragment");
            if (nearByPlace.getResults() != null) Log.d(TAG, "updateNearByPlace: cool mapfragment");
            else Log.d(TAG, "updateNearByPlace: Hmmmm mapfragment");
            for (Place p : nearByPlace.getResults()) {
                mMap.addMarker(new MarkerOptions().position(p.getGeometry().getCoordinate()).title(p.getName() + p.getGeometry().getCoordinate().toString()));
                Log.d(TAG, "updateNearByPlace: mark mapfragment");
            }
        } else Log.d(TAG, "updateNearByPlace: null mapfragment");

    }

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
        View mapView = inflater.inflate(R.layout.map, container, false);
        //if (getFragmentManager() != null) {
        Log.d(TAG, "onCreateView: not null mapfragment ");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            Log.d(TAG, "onCreateView: nonnll nonnull mapfragment");
        } else Log.d(TAG, "onCreateView: nonnull null mapfragment");
        // }

        Log.d(TAG, "onCreateView: aftercall mapfragment");
        //   else Log.d(TAG, "onCreateView: null");
        return mapView;
    }

    private LatLng initialPosition() {
        return new LatLng(47.390289,0.688850);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: entre mapfragment");
        mMap = googleMap;
        Log.d(TAG, "onCreateView: retrocall mapfragment");
        if (mNearByPlace != null) {
            for (Place p : mNearByPlace.getResults()) {
                Log.d(TAG, "onMapReady: marker mapfragment");
                mMap.addMarker(new MarkerOptions().position(p.getGeometry().getCoordinate()).title(p.getName() + p.getGeometry().getCoordinate().toString()));
            }
        }
        //mMap.addMarker(new MarkerOptions().position(initialPosition()).title("Centre du monde"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
