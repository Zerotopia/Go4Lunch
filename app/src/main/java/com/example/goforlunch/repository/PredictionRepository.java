package com.example.goforlunch.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.model.Restaurant;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionRepository {

//    private PlacesClient mPlacesClient;
//    private AutocompleteSessionToken mSessionToken;
//    //private MutableLiveData<List<AutocompletePrediction>> data = new MutableLiveData<>();
//
//    public PredictionRepository(PlacesClient placesClient, AutocompleteSessionToken sessionToken) {
//        mPlacesClient = placesClient;
//        mSessionToken = sessionToken;
//    }
//
////    public MutableLiveData<List<AutocompletePrediction>> getData() {
////        return data;
////    }
//
////    public MutableLiveData<String> getQuery(String query) {
////        final MutableLiveData<String> data = new MutableLiveData<>();
////        data.setValue(query);
////        return data;
////    }
//
//    public MutableLiveData<List<AutocompletePrediction>> getPlacePredictions
//            (String query, LocationBias bias) {
//        final MutableLiveData<List<AutocompletePrediction>> data = new MutableLiveData<>();
//        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
//                .builder()
//                .setSessionToken(mSessionToken)
//                .setLocationBias(bias)
//                .setTypeFilter(TypeFilter.ESTABLISHMENT)
//                .setQuery(query)
//                .setCountries("FR")
//                .build();
//        mPlacesClient.findAutocompletePredictions(newRequest).addOnSuccessListener((response) -> {
//            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
//            Log.d("TAG", "getPlacePredictions PredictionRepository: non filtrer " + predictions.size());
//            List<AutocompletePrediction> pred = new ArrayList<>();
//            for (AutocompletePrediction p : predictions) {
//                Log.d("TAG", "getPlacePredictions predictionrepository: ");
//                //if (p.getPlaceTypes().contains(Place.Type.FOOD)) {
//                pred.add(p);
//                //  }
//            }
//            Log.d("TAG", "getPlacePredictions prediction repository:" + pred.size());
//            data.setValue(pred);
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                Log.e("TAG", "Place not found fail predictionrepository getPlacePrediction: " + apiException.getStatusCode());
//            }
//        });
//        return data;
//    }
//
//    public MutableLiveData<LatLng> getPlaceLocation(String placeId) {
//        final MutableLiveData<LatLng> data = new MutableLiveData<>();
//        final List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
//        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
//            Log.d("TAG", "getPlaceLocation: notNPE1");
//            if (fetchPlaceResponse != null) {
//                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
//                if (fetchPlaceResponse.getPlace() != null) {
//
//                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
//                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
//                    data.setValue(fetchPlaceResponse.getPlace().getLatLng());
//                } else
//                    Log.d("TAG", "getPlaceLocation: notNPE2");
//                Log.d("TAG", "getPlaceLocation: setvalueOK");
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                final ApiException apiException = (ApiException) exception;
//                Log.e("TAG", "Place not found: " + exception.getMessage());
//                final int statusCode = apiException.getStatusCode();
//                // TODO: Handle error with given status code.
//            }
//        });
//
//        return data;
//
//    }
//
//    public MutableLiveData<Place> getPlacePhone(String placeId) {
//        final MutableLiveData<Place> data = new MutableLiveData<>();
//        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER,Place.Field.WEBSITE_URI);
//        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
//            Log.d("TAG", "getPlaceLocation: notNPE1");
//            if (fetchPlaceResponse != null) {
//                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
//                if (fetchPlaceResponse.getPlace() != null) {
//                    //String[] dataArray = {fetchPlaceResponse.getPlace().getPhoneNumber(); //,fetchPlaceResponse.getPlace().getWebsiteUri()};
//                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
//                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
//                    data.setValue(fetchPlaceResponse.getPlace());
//                } else
//                    Log.d("TAG", "getPlaceLocation: notNPE2");
//                Log.d("TAG", "getPlaceLocation: setvalueOK");
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                final ApiException apiException = (ApiException) exception;
//                Log.e("TAG", "Place not found: " + exception.getMessage());
//                final int statusCode = apiException.getStatusCode();
//                // TODO: Handle error with given status code.
//            }
//        });
//
//        return data;
//
//    }
//
//    public MutableLiveData<Bitmap> getPhotos(String placeId) {
//        final MutableLiveData<Bitmap> data = new MutableLiveData();
//        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
//        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
//            final List<PhotoMetadata> metadata = fetchPlaceResponse.getPlace().getPhotoMetadatas();
//            if (metadata == null || metadata.isEmpty()) {
//                Log.w("TAG", "No photo metadata.");
//                return;
//            }
//            final PhotoMetadata photoMetadata = metadata.get(0);
//
//            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                    .setMaxWidth(500) // Optional.
//                    .setMaxHeight(300) // Optional.
//                    .build();
//            mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
//                data.setValue(fetchPhotoResponse.getBitmap());
//            });
//        });
//        return data;
//    }
//
//    public MutableLiveData<com.example.goforlunch.model.Place> getPlace(String placeId) {
//        final MutableLiveData<com.example.goforlunch.model.Place> data = new MutableLiveData<>();
//        final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.ADDRESS);
//        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
//            Log.d("TAG", "getPlaceLocation: notNPE1");
//            if (fetchPlaceResponse != null) {
//                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
//                if (fetchPlaceResponse.getPlace() != null) {
//                    //String[] dataArray = {fetchPlaceResponse.getPlace().getPhoneNumber(); //,fetchPlaceResponse.getPlace().getWebsiteUri()};
//                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
//                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
//                    com.example.goforlunch.model.Place place = new com.example.goforlunch.model.Place(placeId,
//                            fetchPlaceResponse.getPlace().getName(),
//                            fetchPlaceResponse.getPlace().getAddress());
//                    data.setValue(place);
//                } else
//                    Log.d("TAG", "getPlaceLocation: notNPE2");
//                Log.d("TAG", "getPlaceLocation: setvalueOK");
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                final ApiException apiException = (ApiException) exception;
//                Log.e("TAG", "Place not found: " + exception.getMessage());
//                final int statusCode = apiException.getStatusCode();
//                // TODO: Handle error with given status code.
//            }
//        });
//
//        return data;
//
//    }
//
//
//    public MutableLiveData<List<String>> getLikers(String restaurantId) {
//        final MutableLiveData<List<String>> data = new MutableLiveData<>();
//        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
//            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
//            if (currentRestaurant == null) {
//               // RestaurantManager.createRestaurant(restaurantId);
//               // RestaurantManager.updateRestaurantName(restaurantName,restaurantId);
//                data.setValue(new ArrayList<>());
//            } else {
//                data.setValue(currentRestaurant.getLikers());
//            }
//        });
//        return data;
//    }
}
