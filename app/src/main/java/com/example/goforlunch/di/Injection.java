package com.example.goforlunch.di;

import android.content.Context;
import android.util.Log;

import com.example.goforlunch.MapClient;
import com.example.goforlunch.R;
import com.example.goforlunch.repository.DetailRepository;
import com.example.goforlunch.repository.NetworkRepository;
import com.example.goforlunch.viewmodel.ViewModelFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

public class Injection {

    public static final String TAG = "TAG";

    public static NetworkRepository provideNetworkRepository(Context context) {
        Log.d(TAG, "provideNetworkRepository: Injection ");
        PlacesClient placesClient;
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_key));
            Log.d("TAG", "onCreateView: initiliaze");
        }
        placesClient = Places.createClient(context);
        return new NetworkRepository(MapClient.getInstance(),placesClient,AutocompleteSessionToken.newInstance());
    }

    public static ViewModelFactory provideNetworkViewModelFactory(Context context) {
        Log.d(TAG, "provideNetworkViewModelFactory: Injection");
        return new ViewModelFactory(provideNetworkRepository(context), provideDetailRepository(context));
    }

//    public static PredictionRepository providePredictionRepository(Context context) {
//        PlacesClient placesClient;
//        if (!Places.isInitialized()) {
//            Places.initialize(context, context.getString(R.string.google_maps_key));
//            Log.d("TAG", "onCreateView: initiliaze");
//        }
//        placesClient = Places.createClient(context);
//        return new PredictionRepository(placesClient, AutocompleteSessionToken.newInstance());
//    }

  //  public static LikeRepository provideLikeRepository() {
     //   return new LikeRepository();
    //}

    public static DetailRepository provideDetailRepository(Context context) {
        PlacesClient placesClient;
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.google_maps_key));
            Log.d("TAG", "onCreateView: initiliaze");
        }
        placesClient = Places.createClient(context);
        return new DetailRepository(placesClient);
    }
}

