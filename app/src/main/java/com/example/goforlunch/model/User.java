package com.example.goforlunch.model;

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


    public  User () {}

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

    public static User parseString (String userString) {
        String[] userInfo = userString.split("/",3);
        return new User(userInfo[0],userInfo[1],userInfo[2]);

    }
}
