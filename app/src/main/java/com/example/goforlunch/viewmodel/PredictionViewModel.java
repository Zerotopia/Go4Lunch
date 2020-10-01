package com.example.goforlunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.repository.PredictionRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.List;

public class PredictionViewModel extends ViewModel {


    private PredictionRepository mPredictionRepository;
    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private MutableLiveData<String> mPlaceId = new MutableLiveData<>();
    private LocationBias BIAS = RectangularBounds.newInstance(
            new LatLng(47.38545, 0.67909), // SW lat, lng
            new LatLng(47.39585, 0.69519) );// NE lat, lng
    private final LiveData<List<AutocompletePrediction>> mPredictionObservable
            = Transformations.switchMap(mQuery, (query) -> mPredictionRepository.getPlacePredictions(query, BIAS));
    private final LiveData<LatLng> mLocationObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mPredictionRepository.getPlaceLocation(placeId));

    public PredictionViewModel(PredictionRepository predictionRepository) {
        mPredictionRepository = predictionRepository;
    }


    public void init() {
        //  mQuery = mPredictionRepository.getQuery("");
    }

    public void newQuery(String query) {
       mQuery.setValue(query);
    }
    public void newPos (String placeId) {mPlaceId.setValue(placeId);};

    public final LiveData<List<AutocompletePrediction>> getPredictionObservable() {
         return mPredictionObservable;
    }

    public final LiveData<LatLng> getLocationObservable() { return mLocationObservable; }
}