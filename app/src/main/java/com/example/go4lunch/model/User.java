package com.example.go4lunch.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("FirstName")
    private String mFirstName;
    @SerializedName("LastName")
    private String mLastName;
    @SerializedName("Email")
    private String mEmail;
   // private boolean mIsActive;

    private int mUserId;
    @SerializedName("Photo")
    private String mPhoto;
    @SerializedName("RestaurantId")
    private int mRestaurantId;



    public User(String firstName, String lastName, String email) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

   // public boolean isActive() {
   //     return mIsActive;
   // }

   // public void setActive(boolean active) {
   //     mIsActive = active;
   // }

    public int getRestaurantId() {
        return mRestaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.mRestaurantId = restaurantId;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }
}
