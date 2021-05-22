package com.example.goforlunch.view;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.ListItemClickListener;
import com.example.goforlunch.R;
import com.example.goforlunch.activity.MapActivity;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.LocationBias;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
    , MapActivity.InterfaceListener
        //      , FragmentViewModelListener
{
    public static final String TAG = "TAG";

    private static final String LATITUDE = "LAT";
    private static final String LONGITUDE = "LNG";
    private GoogleMap mMap;
    private NearByPlace mNearByPlace;
    private Handler handler = new Handler();
    private List<String> mReservedRestaurants;
    private NetworkViewModel mNetworkViewModel;
    private LatLng mInitialPosition;
    private LocationBias mBias;
    private SupportMapFragment mMapFragment;


    @NonNull
    public static MapFragment newInstance(LatLng coordinate) {
        MapFragment mapFragment = new MapFragment();
        Bundle arg = new Bundle();
        arg.putDouble(LATITUDE, coordinate.latitude);
        arg.putDouble(LONGITUDE, coordinate.longitude);
        mapFragment.setArguments(arg);
        return mapFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMapFragment();
       // mNetworkViewModel.initReservedRestaurant();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mapView = inflater.inflate(R.layout.map, container, false);
        setInitialPosition();
        initMapFragment();
        return mapView;
    }

    /**
     * VIEW MODEL OBSERVERS
     */
    public void observeViewModel(NetworkViewModel networkViewModel) {
        networkViewModel.getNetworkObservable().observe(getViewLifecycleOwner(), this::updateNearByPlace);
        networkViewModel.getRestaurantObservable().observe(getViewLifecycleOwner(), this::updateRestaurants);
    }

    private void updateNearByPlace(NearByPlace nearByPlace) {
        mNearByPlace = nearByPlace;
        if ((mMap != null)) {
            for (Place p : nearByPlace.getResults()) {
                mMap.addMarker(setMarkerOptions(p)).setTag(p.getId());
            }
            mMap.setOnMarkerClickListener(this);
        }
    }

    private void updateRestaurants(List<String> reservedRestaurants) {
        mReservedRestaurants = reservedRestaurants;
    }

    /********************************************************************/
    /**
     * INTERFACE
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mNetworkViewModel.initReservedRestaurant();
        if (mNearByPlace != null) {
            for (Place p : mNearByPlace.getResults()) {
                mMap.addMarker(setMarkerOptions(p)).setTag(p.getId());
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mInitialPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String id = (String) marker.getTag();
        if (id != null) {
            ((ListItemClickListener) getContext()).itemClick(id);
            return true;
        }
        return false;
    }
    /********************************************************************************/
    /**
     * annexe functions
     */

    public void updateUIAutocomplete(LatLng latLng, String placeId) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(getBitmap(R.drawable.google_map_restaurant_icon))).setTag(placeId);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setOnMarkerClickListener(this);
        }
    }

    private MarkerOptions setMarkerOptions(Place p) {
        return new MarkerOptions()
                .position(p.getGeometry().getCoordinate())
                .title(p.getName() + p.getGeometry().getCoordinate().toString())
                .icon(getIcon(p.getId()));
    }

    private BitmapDescriptor getIcon(String placeId) {
        if (mReservedRestaurants != null) {
            if (mReservedRestaurants.contains(placeId))
                return getBitmap(R.drawable.google_map_restaurant_green);
            else return getBitmap(R.drawable.google_map_restaurant_icon);
        }
        return getBitmap(R.drawable.google_map_restaurant_icon);
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

    private void setViewModel() {
        mNetworkViewModel = //((MapActivity) getActivity()).getNetworkViewModel();
                ViewModelProviders.of(requireActivity(), Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        Log.d(TAG, "setViewModel: utilisation mBias dans mapfragment");
        mBias = ((MapActivity) requireActivity()).getBias();
        mNetworkViewModel.init("", mBias, 1);
        observeViewModel(mNetworkViewModel);
    }

    private void setInitialPosition() {
        if (getArguments() != null)
            mInitialPosition = new LatLng(getArguments().getDouble(LATITUDE), getArguments().getDouble(LONGITUDE));
    }

    private void initMapFragment() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);
    }


    @Override
    public void updateUI(NearByPlace nearbyPlace, List<String> reservedRestaurant) {

    }
}


