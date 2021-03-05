package com.example.goforlunch.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.model.value.ReferenceValue;

import java.nio.file.attribute.UserPrincipalLookupService;

public class User {
    //@SerializedName("FirstName")
    private String UserName;
    //@SerializedName("LastName")
    //private String LastName;
    ///@SerializedName("Email")
    private String Email;
    // private boolean mIsActive;

    //private int mUserId;
    // @SerializedName("Photo")
    private String Photo;
    // @SerializedName("RestaurantId")
    private String RestaurantId;

    private String RestaurantName;


    public User() {
    }

    public User(String userName, String email, String photo) {
        UserName = userName;
        Email = email;
        Photo = photo;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    // public boolean isActive() {
    //     return mIsActive;
    // }

    // public void setActive(boolean active) {
    //     mIsActive = active;
    // }

    public String getRestaurantName() {
        return RestaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        RestaurantName = restaurantName;
    }

    public String getRestaurantId() {
        return RestaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        RestaurantId = restaurantId;
    }

//    public int getUserId() {
//        return mUserId;
//    }
//
//    public void setUserId(int userId) {
//        mUserId = userId;
//    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    @Override
    public String toString() {
        return UserName + "/" +
                Email + "/" +
                Photo;
    }

    public static User parseString(String userString) {
        String[] userInfo = userString.split("/", 3);
        return new User(userInfo[0], userInfo[1], userInfo[2]);

    }

    @BindingAdapter("userImage")
    public static void loadImage(ImageView imageView, String urlImage) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(options)
                .load(urlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
