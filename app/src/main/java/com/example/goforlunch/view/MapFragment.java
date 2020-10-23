package com.example.goforlunch.view;

import android.content.Context;
import android.content.Intent;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.DetailActivity;
import com.example.goforlunch.MapActivity;
import com.example.goforlunch.R;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.example.goforlunch.viewmodel.PredictionViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final String TAG = "TAG";

    private static final String LATITUDE = "LAT";
    private static final String LONGITUDE = "LNG";
    private GoogleMap mMap;
    private NearByPlace mNearByPlace;
    private PredictionViewModel mPredictionViewModel;
    private PredictionAdapter adapter = new PredictionAdapter();
    private Handler handler = new Handler();
    private MapMarkerListener mMapMarkerListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mMapMarkerListener = (MapMarkerListener) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: mapfragment super");
        final NetworkViewModel networkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(getContext(), "")).get(NetworkViewModel.class);
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
                mMap.addMarker(new MarkerOptions()
                        .position(p.getGeometry().getCoordinate())
                        .title(p.getName() + p.getGeometry().getCoordinate().toString()));
                   //     .icon(getBitmap(R.drawable.ic_baseline_restaurant_24))).setTag(p);


                Log.d(TAG, "updateNearByPlace: mark mapfragment");
            }
            mMap.setOnMarkerClickListener(this);
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
            mMapMarkerListener.setSearchMarker(mMap);
            Log.d(TAG, "onCreateView: nonnll nonnull mapfragment");
        } else Log.d(TAG, "onCreateView: nonnull null mapfragment");
        // }

        Log.d(TAG, "onCreateView: aftercall mapfragment");
        //   else Log.d(TAG, "onCreateView: null");
        return mapView;
    }

    private LatLng initialPosition() {
        return new LatLng(47.390289, 0.688850);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: entre mapfragment");
        mMap = googleMap;
        Log.d(TAG, "onCreateView: retrocall mapfragment");
        if (mNearByPlace != null) {
            for (Place p : mNearByPlace.getResults()) {
                Log.d(TAG, "onMapReady: marker mapfragment");
                mMap.addMarker(new MarkerOptions()
                        .position(p.getGeometry().getCoordinate())
                        .title(p.getName() + p.getGeometry().getCoordinate().toString()));
                       // .icon(getBitmap(R.drawable.ic_baseline_restaurant_24))).setTag(p);
            }
        }
        //mMap.addMarker(new MarkerOptions().position(initialPosition()).title("Centre du monde"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        initSearchView(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initSearchView(SearchView searchView) {
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

                // Cancel any previous place prediction requests
                handler.removeCallbacksAndMessages(null);

                // Start a new place prediction request in 300 ms
                handler.postDelayed(() -> {
                    mPredictionViewModel.newQuery(newText);
                    // getPlacePredictions(newText);
                }, 300);
                return true;
            }
        });
    }

    public void updateUI(LatLng latLng) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(getBitmap(R.drawable.ic_baseline_restaurant_24)));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Place p = (Place) marker.getTag();
        if (p != null) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        String urlPhoto = p.getPhotos().get(0).getPhotoRef() + getContext().getString(R.string.google_maps_key);
        intent.putExtra(MapActivity.URL_IMAGE,urlPhoto);
        intent.putExtra(MapActivity.NAME_RESTAURANT,p.getName());
        intent.putExtra(MapActivity.UID_RESTAURANT,p.getId());
        intent.putExtra(MapActivity.ADDR_RESTAURANT,p.getAddress());
        getContext().startActivity(intent);
        return true; }
        return false;
    }

    public interface MapMarkerListener {
        void setSearchMarker(GoogleMap map);
    }

    private BitmapDescriptor getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}


