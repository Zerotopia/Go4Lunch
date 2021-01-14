package com.example.goforlunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkRepository {

    private MapService mMapService;

    public NetworkRepository(MapService mapService) {
        mMapService = mapService;
    }


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
}
