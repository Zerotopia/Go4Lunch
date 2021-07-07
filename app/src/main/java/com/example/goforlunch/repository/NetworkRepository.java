package com.example.goforlunch.repository;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkRepository {

    private MapService mMapService;

    private PlacesClient mPlacesClient;
    private AutocompleteSessionToken mSessionToken;
    // private int mTotalUsers;

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
                    user.initName();
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
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
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
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
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

    public MutableLiveData<List<Double>> getRatio(NearByPlace nearByPlace, int totalUsers) {
        final MutableLiveData<List<Double>> data = new MutableLiveData<>();
        final List<Double> dataList = new ArrayList<>();
//        UserManager.getAllUser().addOnSuccessListener(queryDocumentSnapshots -> {
//           mTotalUsers = queryDocumentSnapshots.size();
//        });
        RestaurantManager.getAllRestaurant().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Restaurant> restaurants = queryDocumentSnapshots.toObjects(Restaurant.class);
            for (com.example.goforlunch.model.Place place : nearByPlace.getResults()) {
                double ratioLike = (double) ((ratioRestaurant(place.getId(), restaurants)) / totalUsers);
                dataList.add(ratioLike);
                RestaurantManager.updateRestaurantRatio(computeRatio(ratioLike,place.getRatio()),place.getId());
            }
            data.setValue(dataList);
        });
        return data;
    }

    public static int computeRatio(double ratioPlace, Double ratio) {
        double result = 3 * (0.7 * ratioPlace + 0.3 * (ratio / 5.0));
        if (result <= 0.5) return 0;
        if (result <= 1.5) return 1;
        if (result <= 2.5) return 2;
        return 3;
    }
//        Log.d("TAG", "getRatio: REPOSITORY ::: nerabyplace : " + nearByPlace.getResults().size());
//
//        //for (com.example.goforlunch.model.Place place : nearByPlace.getResults()) {
//        Log.d("TAG", "getRati in for boucle");
//            RestaurantManager.getRestaurant(place.getId()).addOnSuccessListener(documentSnapshot -> {
//                Log.d("TAG", "getRatio: successlistenr");
//                Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
//                if (currentRestaurant == null) {
//                    dataList.add(0.0);
//                    Log.d("TAG", "getRatio: REPOSITORY :: null :" + dataList.size());
//                } else {
//
//                    dataList.add((double) ((currentRestaurant.getLikers().size()) / totalUsers));
//                    Log.d("TAG", "getRatio: REPOSITORY :: nonnul :"+ dataList.size() );
//                }
//                data.setValue(dataList);
//            }).addOnFailureListener(e -> {
//                Log.d("TAG", "getRatio: error" + e.getMessage());
//            }).addOnCanceledListener(() -> {
//                Log.d("TAG", "getRatio: cancel");
//            }).addOnCompleteListener(task -> {
//                Log.d("TAG", "getRatio: onComplete ");
//            });
//        }
//        Log.d("TAG", "getRatio: REPOSITORY" + dataList.size());
//
//        return data;
//    }

    public static int ratioRestaurant(String id, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants)
            if (id.equals(restaurant.getId())) return restaurant.getLikers().size();
        return 0;
    }

    public MutableLiveData<List<Integer>> getNumberOfLuncher(NearByPlace nearByPlace) {
        final MutableLiveData<List<Integer>> data = new MutableLiveData<>();
        final List<Integer> dataList = new ArrayList<>();
//        UserManager.getAllUser().addOnSuccessListener(queryDocumentSnapshots -> {
//           mTotalUsers = queryDocumentSnapshots.size();
//        });
        RestaurantManager.getAllRestaurant().addOnSuccessListener(queryDocumentSnapshots -> {
            for (com.example.goforlunch.model.Place place : nearByPlace.getResults())
                dataList.add(numberOfLuncher (place.getId(), queryDocumentSnapshots))
                        ;
            data.setValue(dataList);
        });
        return data;
    }

    private int numberOfLuncher(String id, QuerySnapshot querySnapshot) {
        for (DocumentSnapshot documentSnapshot : querySnapshot)
            if (id.equals(documentSnapshot.getId())) return documentSnapshot.toObject(Restaurant.class).getNumberOfLunchers();
        return 0;
    }

    public MutableLiveData<Integer> getTotalUsers() {
        final MutableLiveData<Integer> data = new MutableLiveData<>();
        UserManager.getAllUser().addOnSuccessListener(queryDocumentSnapshots -> {
            data.setValue(queryDocumentSnapshots.size());
        }).addOnCanceledListener(() -> {
            Log.d("VIEWMODELTAG", "getTotalUsers: cancel");
        }).addOnFailureListener(e -> {
            Log.d("VIEWMODELTAG", "getTotalUsers: fail :::  " + e.getMessage());

        });
        return data;
    }

    public MutableLiveData<Integer>getFragmentId(int id) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        data.setValue(id);
        return data;
    }

 }
