package com.example.goforlunch.di;

import android.content.Context;

import com.example.goforlunch.repository.MapClient;
import com.example.goforlunch.R;
import com.example.goforlunch.repository.DetailRepository;
import com.example.goforlunch.repository.NetworkRepository;
import com.example.goforlunch.viewmodel.ViewModelFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

/**
 * Class used to "inject" viewModel in Activity.
 */
public class Injection {

    public static NetworkRepository provideNetworkRepository(Context context) {
        PlacesClient placesClient;
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(context);
        return new NetworkRepository(MapClient.getInstance(), placesClient, AutocompleteSessionToken.newInstance());
    }

    public static ViewModelFactory provideNetworkViewModelFactory(Context context) {
        return new ViewModelFactory(provideNetworkRepository(context), provideDetailRepository(context));
    }

    public static DetailRepository provideDetailRepository(Context context) {
        PlacesClient placesClient;
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(context);
        return new DetailRepository(placesClient);
    }
}

