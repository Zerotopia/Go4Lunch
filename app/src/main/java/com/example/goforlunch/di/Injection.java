package com.example.goforlunch.di;

import android.content.Context;
import android.util.Log;

import com.example.goforlunch.MapClient;
import com.example.goforlunch.R;
import com.example.goforlunch.repository.NetworkRepository;
import com.example.goforlunch.repository.PredictionRepository;
import com.example.goforlunch.viewmodel.ViewModelFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

public class Injection {

    public static final String TAG = "TAG";

    public static NetworkRepository provideNetworkRepository() {
        Log.d(TAG, "provideNetworkRepository: Injection ");
        return new NetworkRepository(MapClient.getInstance());
    }

    public static ViewModelFactory provideNetworkViewModelFactory(Context context) {
        Log.d(TAG, "provideNetworkViewModelFactory: Injection");
        return new ViewModelFactory(provideNetworkRepository(), providePredictionRepository(context));
    }

    public static PredictionRepository providePredictionRepository(Context context) {
        PlacesClient placesClient;
        if (!Places.isInitialized()) {
            Places.initialize(context, String.valueOf(R.string.google_api_key));
            Log.d("TAG", "onCreateView: initiliaze");
        }
        placesClient = Places.createClient(context);
        return new PredictionRepository(placesClient, AutocompleteSessionToken.newInstance());
    }

}
