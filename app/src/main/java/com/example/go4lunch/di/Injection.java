package com.example.go4lunch.di;

import android.content.Context;

import com.example.go4lunch.MapClient;
import com.example.go4lunch.repository.NetworkRepository;
import com.example.go4lunch.repository.PredictionRepository;
import com.example.go4lunch.viewmodel.ViewModelFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.net.PlacesClient;

public class Injection {

    public static NetworkRepository provideNetworkRepository() {
        return new NetworkRepository(MapClient.getInstance());
    }

    public static ViewModelFactory provideNetworkViewModelFactory(Context context) {
        return new ViewModelFactory(provideNetworkRepository(), providePredictionRepository(context));
    }

    public static PredictionRepository providePredictionRepository(Context context) {
        PlacesClient placesClient;
        placesClient = Places.createClient(context);
        return new PredictionRepository(placesClient, AutocompleteSessionToken.newInstance());
    }

}
