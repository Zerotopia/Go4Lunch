package com.example.goforlunch.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.R;

/**
 * User Model
 */
public class User {

    private String UserName;
    private String Email;
    private String Photo;
    private String RestaurantId;
    private String RestaurantName;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(String userName, String email, String photo) {
        UserName = userName;
        Email = email;
        Photo = photo;
        firstName = UserName.split(" ")[0];
        lastName = UserName.split(" ")[1];
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

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * The separation of the first name and the last name
     * is to allow the research of workmate either by first name or last name.
     */
    public void initName() {
        firstName = UserName.split(" ")[0];
        lastName = UserName.split(" ")[1];
    }

    /**
     * Adapter used to databinding the user's image.
     *
     * @param imageView View that should be bind.
     * @param urlImage  url of the user's Image
     */
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
