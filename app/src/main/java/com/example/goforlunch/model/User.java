package com.example.goforlunch.model;

public class User {
    //@SerializedName("FirstName")
    private String FirstName;
    //@SerializedName("LastName")
    private String LastName;
    ///@SerializedName("Email")
    private String Email;
   // private boolean mIsActive;

    //private int mUserId;
   // @SerializedName("Photo")
    private String Photo;
   // @SerializedName("RestaurantId")
    private int RestaurantId;


    public  User () {}

    public User(String firstName, String lastName, String email) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
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

    public int getRestaurantId() {
        return RestaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.RestaurantId = restaurantId;
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
}
