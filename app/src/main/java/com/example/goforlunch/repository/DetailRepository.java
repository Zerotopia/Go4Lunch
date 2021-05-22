package com.example.goforlunch.repository;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.R;
import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DetailRepository {

    private PlacesClient mPlacesClient;

    public DetailRepository(PlacesClient placesClient) {
        mPlacesClient = placesClient;
    }

    public DetailRepository() {
    }

    public MutableLiveData<Place> getPlace(String placeId) {
        final MutableLiveData<Place> data = new MutableLiveData<>();
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.ADDRESS, Place.Field.NAME);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        mPlacesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            Log.d("TAG", "getPlaceLocation: notNPE1");
            if (fetchPlaceResponse != null) {
                //Log.d("TAG", "getPlaceLocation: fetchnonnull : " + fetchPlaceResponse.toString());
                //if (fetchPlaceResponse.getPlace() != null) {
                //String[] dataArray = {fetchPlaceResponse.getPlace().getPhoneNumber(); //,fetchPlaceResponse.getPlace().getWebsiteUri()};
                //  Log.d("TAG", "getPlaceLocation: before datasetvalue : " + fetchPlaceResponse.getPlace().toString());
                // Log.d("TAG", "getPlaceLocation: value " + fetchPlaceResponse.getPlace().getLatLng());
                Place placeResponse = fetchPlaceResponse.getPlace();
                Place place = new Place() {
                    @Nullable
                    @Override
                    public String getAddress() {
                        String address = placeResponse.getAddress();
                        if (address != null)
                            return placeResponse.getAddress().split(",")[0];
                        else return "";
                    }

                    @Nullable
                    @Override
                    public AddressComponents getAddressComponents() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public BusinessStatus getBusinessStatus() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public List<String> getAttributions() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getId() {
                        return placeResponse.getId();
                    }

                    @Nullable
                    @Override
                    public LatLng getLatLng() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getName() {
                        return placeResponse.getName();
                    }

                    @Nullable
                    @Override
                    public OpeningHours getOpeningHours() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getPhoneNumber() {
                        return placeResponse.getPhoneNumber();
                    }

                    @Nullable
                    @Override
                    public List<PhotoMetadata> getPhotoMetadatas() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public PlusCode getPlusCode() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Integer getPriceLevel() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Double getRating() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public List<Type> getTypes() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Integer getUserRatingsTotal() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Integer getUtcOffsetMinutes() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public LatLngBounds getViewport() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Uri getWebsiteUri() {
                        return placeResponse.getWebsiteUri();
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel parcel, int i) {

                    }
                };
                data.setValue(place);
                //} else
                //  Log.d("TAG", "getPlaceLocation: notNPE2");
                //Log.d("TAG", "getPlaceLocation: setvalueOK");
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
        final List<Place.Field> placeFields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
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
            mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener(
                    (fetchPhotoResponse) -> data.setValue(fetchPhotoResponse.getBitmap()));
        });
        return data;
    }

    public MutableLiveData<Boolean> isLike(String restaurantId, String userId) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            if (restaurant != null) {
                data.setValue((restaurant.getLikers() != null) && (restaurant.getLikers().contains(userId)));
            } else data.setValue(false);
        });
        return data;
    }

    public MutableLiveData<Boolean> isLunch(String restaurantId, String userId) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        UserManager.getUser(userId).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            data.setValue((user != null) && (user.getRestaurantId() != null) && user.getRestaurantId().equals(restaurantId));
        });
        return data;
    }

    public MutableLiveData<List<User>> getLunchers(String restaurantId, String userId) {
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        UserManager.getUsersInRestaurant(restaurantId).addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> users = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                if (!documentSnapshot.getId().equals(userId)) {
                    User user = documentSnapshot.toObject(User.class);
                    user.initName();
                    users.add(user);
                }
            data.setValue(users);
        });
        return data;
    }

    public MutableLiveData<String> getCurrentRestaurantId(String userId) {
        MutableLiveData<String> data = new MutableLiveData<>();
        UserManager.getUser(userId).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            data.setValue((user == null) ? "" : user.getRestaurantId());
        });
        return data;
    }

    public MutableLiveData<Integer> getRatio(String restaurantId) {
        MutableLiveData<Integer> data = new MutableLiveData<>();
        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            data.setValue((restaurant == null) ? 0 : restaurant.getRatio());
        });
        return data;
    }
}
