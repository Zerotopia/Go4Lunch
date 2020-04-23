package com.example.go4lunch.model;

public class User {
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private boolean mIsActive;

    private int mUserId;
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

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    public int getRestaurantId() {
        return mRestaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.mRestaurantId = restaurantId;
    }



}
