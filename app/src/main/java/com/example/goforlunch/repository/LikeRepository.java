package com.example.goforlunch.repository;

import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.R;
import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;

public class LikeRepository {

    private String mUserId;

    public LikeRepository(String mUserId) {
        this.mUserId = mUserId;
    }

    public MutableLiveData<Boolean> isLike(String restaurantId) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        RestaurantManager.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            if ((restaurant.getLikers() != null) && (restaurant.getLikers().contains(mUserId)))
                data.setValue(true);
            else
                data.setValue(false);
        });
        return data;
    }

    public MutableLiveData<Boolean> isLunch(String restaurantId) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        UserManager.getUser(mUserId).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user.getRestaurantId().equals(restaurantId))
                data.setValue(true);
            else
                data.setValue(false);
        });
        return data;
    }

}
