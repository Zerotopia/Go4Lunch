package com.example.goforlunch.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkRepository {

    private MapService mMapService;

    private PlacesClient mPlacesClient;
    private AutocompleteSessionToken mSessionToken;

    public NetworkRepository(MapService mapService, PlacesClient placesClient, AutocompleteSessionToken sessionToken) {
        mMapService = mapService;
        mPlacesClient = placesClient;
        mSessionToken = sessionToken;
    }

   // public NetworkRepository(MapService mapService) {
      //  mMapService = mapService;
  //  }


    public MutableLiveData<NearByPlace> getNearByPlace() {
        final MutableLiveData<NearByPlace> data = new MutableLiveData<>();
        mMapService.test().enqueue(new Callback<NearByPlace>() {
            @Override
            public void onResponse(Call<NearByPlace> call, Response<NearByPlace> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                    Log.d("TAG", "onResponse in getNearbyplace Network repository: data setValue OK" + response.body().getResults().size());
                } else Log.d("TAG", "onResponse in getNearbyplace Network repository: FAILLLLLLL");
            }

            @Override
            public void onFailure(Call<NearByPlace> call, Throwable t) {
                Log.d("TAG", "onFailure: getNearbyplace networkRepository null ");
            }
        });
        return data;
    }

    public MutableLiveData<List<String>> getReservedRestaurant() {
        final MutableLiveData<List<String>> data = new MutableLiveData<>();
        UserManager.getAllUser().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> restaurantsId = new ArrayList<>();
            for (User user : queryDocumentSnapshots.toObjects(User.class)) {
                String restaurantId = user.getRestaurantId();
                if ((restaurantId != null) && (!restaurantId.isEmpty()))
                    restaurantsId.add(restaurantId);
            }
            data.setValue(restaurantsId);
        });
        return data;
    }

    public MutableLiveData<List<User>> getWorkers(String userId) {
        final MutableLiveData<List<User>> data = new MutableLiveData<>();
        UserManager.getAllUser().addOnSuccessListener(documentSnapshots -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                if (!document.getId().equals(userId)) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
            }
            data.setValue(users);
        });
        return data;
    }

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
                Log.d("TAG", "getPlacePredictions predictionrepository: ");
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

    public MutableLiveData<LatLng> getPlaceLocation(String placeId) {
        final MutableLiveData<LatLng> data = new MutableLiveData<>();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            Log.d("TAG", "getPlaceLocation: notNPE1");
            if (fetchPlaceResponse != null) {
                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
                if (fetchPlaceResponse.getPlace() != null) {

                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
                    data.setValue(fetchPlaceResponse.getPlace().getLatLng());
                } else
                    Log.d("TAG", "getPlaceLocation: notNPE2");
                Log.d("TAG", "getPlaceLocation: setvalueOK");
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        return data;

    }

    public MutableLiveData<Place> getPlacePhone(String placeId) {
        final MutableLiveData<Place> data = new MutableLiveData<>();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER,Place.Field.WEBSITE_URI);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            Log.d("TAG", "getPlaceLocation: notNPE1");
            if (fetchPlaceResponse != null) {
                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
                if (fetchPlaceResponse.getPlace() != null) {
                    //String[] dataArray = {fetchPlaceResponse.getPlace().getPhoneNumber(); //,fetchPlaceResponse.getPlace().getWebsiteUri()};
                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
                    data.setValue(fetchPlaceResponse.getPlace());
                } else
                    Log.d("TAG", "getPlaceLocation: notNPE2");
                Log.d("TAG", "getPlaceLocation: setvalueOK");
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        return data;

    }

    public MutableLiveData<Bitmap> getPhotos(String placeId) {
        final MutableLiveData<Bitmap> data = new MutableLiveData();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            final List<PhotoMetadata> metadata = fetchPlaceResponse.getPlace().getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("TAG", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                data.setValue(fetchPhotoResponse.getBitmap());
            });
        });
        return data;
    }

    public MutableLiveData<com.example.goforlunch.model.Place> getPlace(String placeId) {
        final MutableLiveData<com.example.goforlunch.model.Place> data = new MutableLiveData<>();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.ADDRESS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            Log.d("TAG", "getPlaceLocation: notNPE1");
            if (fetchPlaceResponse != null) {
                Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
                if (fetchPlaceResponse.getPlace() != null) {
                    //String[] dataArray = {fetchPlaceResponse.getPlace().getPhoneNumber(); //,fetchPlaceResponse.getPlace().getWebsiteUri()};
                    Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
                    Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
                    com.example.goforlunch.model.Place place = new com.example.goforlunch.model.Place(placeId,
                            fetchPlaceResponse.getPlace().getName(),
                            fetchPlaceResponse.getPlace().getAddress());
                    data.setValue(place);
                } else
                    Log.d("TAG", "getPlaceLocation: notNPE2");
                Log.d("TAG", "getPlaceLocation: setvalueOK");
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        return data;

    }


    public MutableLiveData<List<String>> getLikers(String restaurantId) {
        final MutableLiveData<List<String>> data = new MutableLiveData<>();
        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            if (currentRestaurant == null) {
                // RestaurantManager.createRestaurant(restaurantId);
                // RestaurantManager.updateRestaurantName(restaurantName,restaurantId);
                data.setValue(new ArrayList<>());
            } else {
                data.setValue(currentRestaurant.getLikers());
            }
        });
        return data;
    }
}
