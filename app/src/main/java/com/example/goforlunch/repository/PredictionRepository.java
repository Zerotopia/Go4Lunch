package com.example.goforlunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class PredictionRepository {

    private PlacesClient mPlacesClient;
    private AutocompleteSessionToken mSessionToken;
    //private MutableLiveData<List<AutocompletePrediction>> data = new MutableLiveData<>();

    public PredictionRepository(PlacesClient placesClient, AutocompleteSessionToken sessionToken) {
        mPlacesClient = placesClient;
        mSessionToken = sessionToken;
    }

//    public MutableLiveData<List<AutocompletePrediction>> getData() {
//        return data;
//    }

//    public MutableLiveData<String> getQuery(String query) {
//        final MutableLiveData<String> data = new MutableLiveData<>();
//        data.setValue(query);
//        return data;
//    }

    public MutableLiveData<List<AutocompletePrediction>> getPlacePredictions
            (String query, LocationBias bias) {
        final MutableLiveData<List<AutocompletePrediction>> data = new MutableLiveData<>();
        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
                .builder()
                .setSessionToken(mSessionToken)
                .setLocationBias(bias)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(query)
                .setCountries("FR")
                .build();
        mPlacesClient.findAutocompletePredictions(newRequest).addOnSuccessListener((response) -> {
            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
            Log.d("TAG", "getPlacePredictions PredictionRepository: non filtrer " + predictions.size());
            List<AutocompletePrediction> pred = new ArrayList<>();
            for (AutocompletePrediction p : predictions) {
                Log.d("TAG", "getPlacePredictions predictionrepository: " );
                //if (p.getPlaceTypes().contains(Place.Type.FOOD)) {
                    pred.add(p);
              //  }
            }
            Log.d("TAG", "getPlacePredictions prediction repository:" + pred.size());
            data.setValue(pred);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found fail predictionrepository getPlacePrediction: " + apiException.getStatusCode());
            }
        });
        return data;
    }
}
