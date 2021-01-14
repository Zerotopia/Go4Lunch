package com.example.goforlunch.repository;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.R;
import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikeRepository {

    public LikeRepository() {

    }

//    public MutableLiveData<Boolean> isLike(String restaurantId, String userId) {
//        MutableLiveData<Boolean> data = new MutableLiveData<>();
//        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
//            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
//            if (restaurant != null) {
//                data.setValue((restaurant.getLikers() != null) && (restaurant.getLikers().contains(userId)));
//            } else data.setValue(false);
//        });
//        return data;
//    }
//
//    public MutableLiveData<Boolean> isLunch(String restaurantId, String userId) {
//        MutableLiveData<Boolean> data = new MutableLiveData<>();
//        UserManager.getUser(userId).addOnSuccessListener(documentSnapshot -> {
//            User user = documentSnapshot.toObject(User.class);
//            if (user.getRestaurantId().equals(restaurantId))
//                data.setValue(true);
//            else
//                data.setValue(false);
//        });
//        return data;
//    }
//
//    public MutableLiveData<List<User>> getUsers(String restaurantId, String userId) {
//        MutableLiveData<List<User>> data = new MutableLiveData<>();
//        UserManager.getUsersInRestaurant(restaurantId).addOnSuccessListener(queryDocumentSnapshots -> {
//            List<User> users = new ArrayList<>();
//            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
//                if (!documentSnapshot.getId().equals(userId))
//                    users.add(documentSnapshot.toObject(User.class));
//           data.setValue(users);
//        });
//        return data;
//    }

}
