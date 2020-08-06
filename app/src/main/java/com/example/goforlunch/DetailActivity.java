package com.example.goforlunch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailActivity  extends AppCompatActivity {

    private ImageView mRestaurantPicture;
    private TextView mRestaurantName;
    private TextView mRestaurantAddress;
    private RecyclerView mLuncherList;

    private Button mCallButton;
    private Button mLikeButton;
    private Button mWebsiteButton;

    private String mUrlImage;
    private String mNameRestaurant;
    private String mAddressRestaurant;
    private List<User> mUsers = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mRestaurantPicture = findViewById(R.id.detail_imageview);
        mRestaurantName = findViewById(R.id.detail_restaurant_name);
        mRestaurantAddress = findViewById(R.id.detail_restaurant_address);
        mLuncherList = findViewById(R.id.detail_luncher_recyclerview);
        mCallButton = findViewById(R.id.detail_call_button);
        mLikeButton = findViewById(R.id.detail_like_button);
        mWebsiteButton = findViewById(R.id.detail_website_button);

        Intent intent = getIntent();
        mUrlImage = intent.getStringExtra(MapActivity.URL_IMAGE);
        mNameRestaurant = intent.getStringExtra(MapActivity.NAME_RESTAURANT);
        mAddressRestaurant = intent.getStringExtra(MapActivity.ADDR_RESTAURANT);
        List<String> usersString = intent.getStringArrayListExtra(MapActivity.LIST_USER_STRING);
        for (String userString : usersString) mUsers.add(User.parseString(userString));


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(mUrlImage)
                .apply(RequestOptions.circleCropTransform())
                .into(mRestaurantPicture);

        mRestaurantAddress.setText(mNameRestaurant);
        mRestaurantAddress.setText(mAddressRestaurant);
        mLuncherList.setLayoutManager(new LinearLayoutManager(this));
        mLuncherList.setAdapter(new WorkerAdapter(mUsers, true));

    }
}
