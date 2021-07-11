package com.example.goforlunch.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.interfaces.MapService;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for map activity
 */
public class NetworkRepository {

    private MapService mMapService;
    private PlacesClient mPlacesClient;
    private AutocompleteSessionToken mSessionToken;

    public NetworkRepository(MapService mapService, PlacesClient placesClient, AutocompleteSessionToken sessionToken) {
        mMapService = mapService;
        mPlacesClient = placesClient;
        mSessionToken = sessionToken;
    }

    /**
     * Get Restaurants around the user in pos initPos in a radius of 1500m
     */
    public MutableLiveData<NearByPlace> getNearByPlace(LatLng initPos) {
        final MutableLiveData<NearByPlace> data = new MutableLiveData<>();
        mMapService.aroundRestaurants(latlngToString(initPos)).enqueue(new Callback<NearByPlace>() {
            @Override
            public void onResponse(@NotNull Call<NearByPlace> call, @NotNull Response<NearByPlace> response) {
                if (response.isSuccessful())
                    data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<NearByPlace> call, Throwable t) {

            }
        });
        return data;
    }

    public String latlngToString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    /**
     * Get all reserved restaurant to mark them on the map.
     */
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

    /**
     * Get all workmates of the user "userId".
     */
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

    /**
     * Get place prediction for search autocomplete places.
     */
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
            List<AutocompletePrediction> pred = new ArrayList<>();
            for (AutocompletePrediction p : predictions) {
                if (p.getPlaceTypes().contains(Place.Type.FOOD) ||
                        p.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                    pred.add(p);
                }
            }
            data.setValue(pred);
        });
        return data;
    }

    /**
     * Get a location of a place for center the map on the place if hte place is selected in the
     * search view.
     */
    public MutableLiveData<LatLng> getPlaceLocation(String placeId) {
        final MutableLiveData<LatLng> data = new MutableLiveData<>();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            if (fetchPlaceResponse != null)
                data.setValue(fetchPlaceResponse.getPlace().getLatLng());
        });
        return data;
    }

    /**
     * Get the list of the users who like the restaurant "restaurantId"
     * Need to be update when a user like a restaurant.
     */
    public MutableLiveData<List<String>> getLikers(String restaurantId) {
        final MutableLiveData<List<String>> data = new MutableLiveData<>();
        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            if (currentRestaurant == null) {
                data.setValue(new ArrayList<>());
            } else {
                data.setValue(currentRestaurant.getLikers());
            }
        });
        return data;
    }

    /**
     * Get the ratio of all restaurant in nearbyplaces.
     */
    public MutableLiveData<List<Integer>> getRatio(NearByPlace nearByPlace, int totalUsers) {
        final MutableLiveData<List<Integer>> data = new MutableLiveData<>();
        final List<Integer> dataList = new ArrayList<>();
        RestaurantManager.getAllRestaurant().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Restaurant> restaurants = queryDocumentSnapshots.toObjects(Restaurant.class);
            for (int i = 0; i < restaurants.size(); i++)
                restaurants.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());

            for (com.example.goforlunch.model.Place place : nearByPlace.getResults()) {
                double ratioLike = ((double) (ratioRestaurant(place.getId(), restaurants)) / totalUsers);
                int ratio = computeRatio(ratioLike, place.getRatio());
                dataList.add(ratio);
                RestaurantManager.updateRestaurantRatio(ratio, place.getId());
            }
            data.setValue(dataList);
        });
        return data;
    }

    /**
     * The like of the user represent 70% of the final ratio and
     * the google ratio represent 30% of the final ratio.
     */
    public static int computeRatio(double ratioPlace, Double ratio) {
        double result = 3 * (0.7 * ratioPlace + 0.3 * (ratio / 5.0));
        if (result <= 0.5) return 0;
        if (result <= 1.5) return 1;
        if (result <= 2.5) return 2;
        return 3;
    }

    public static int ratioRestaurant(String id, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants)
            if (id.equals(restaurant.getId())) return restaurant.getLikers().size();
        return 0;
    }

    /**
     * Get the number of all users that use the application.
     * use to calculate the ratio numberOfLikers / numberOfTotalUsers.
     */
    public MutableLiveData<Integer> getTotalUsers() {
        final MutableLiveData<Integer> data = new MutableLiveData<>();
        UserManager.getAllUser().addOnSuccessListener(queryDocumentSnapshots -> {
            data.setValue(queryDocumentSnapshots.size());
        });
        return data;
    }

    /**
     * Get the number of luncher for each restaurant in nearbyplace request.
     */
    public MutableLiveData<List<Integer>> getNumberOfLunchers(NearByPlace nearByPlace) {
        final MutableLiveData<List<Integer>> data = new MutableLiveData<>();
        final List<Integer> dataList = new ArrayList<>();
        RestaurantManager.getAllRestaurant().addOnSuccessListener(queryDocumentSnapshots -> {
            for (com.example.goforlunch.model.Place place : nearByPlace.getResults())
                dataList.add(numberOfLuncher(place.getId(), queryDocumentSnapshots));
            data.setValue(dataList);
        });
        return data;
    }

    private int numberOfLuncher(String id, QuerySnapshot querySnapshot) {
        for (DocumentSnapshot documentSnapshot : querySnapshot)
            if (id.equals(documentSnapshot.getId()))
                return documentSnapshot.toObject(Restaurant.class).getNumberOfLunchers();
        return 0;
    }

    /**
     * Use to simplify the gestion of the rotation of the screen.
     */
    public MutableLiveData<Integer> getFragmentId(int id) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        data.setValue(id);
        return data;
    }

}
