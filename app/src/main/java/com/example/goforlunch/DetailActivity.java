package com.example.goforlunch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.LikeViewModel;
import com.example.goforlunch.viewmodel.PredictionViewModel;
import com.example.goforlunch.viewmodel.ViewModelFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private ImageView mRestaurantPicture;
    private TextView mRestaurantName;
    private TextView mRestaurantAddress;
    private RecyclerView mLuncherList;

    private Button mCallButton;
    private Button mLikeButton;
    private Button mWebsiteButton;
    private FloatingActionButton mFloatingActionButton;

    private String mUrlImage;
    private String mNameRestaurant;
    private String mAddressRestaurant;
    private String mPhoneNumber;
    private Uri mWebsiteUri;
    private List<User> mUsers = new ArrayList<>();
    private List<String> mLikers = new ArrayList<>();
    private String uid;
    private PredictionViewModel mPredictionViewModel;
    private SharedPreferences mPreferences;
    private String mCurrentId;

    private LikeViewModel mLikeViewModel;

    private boolean mLike;
    private boolean mLunch;

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
        mFloatingActionButton = findViewById(R.id.floating_button);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
        Log.d("TAG", "onCreate: ID = " + mCurrentId);

        Intent intent = getIntent();
        mUrlImage = intent.getStringExtra(MapActivity.URL_IMAGE);
        mNameRestaurant = intent.getStringExtra(MapActivity.NAME_RESTAURANT);
        mAddressRestaurant = intent.getStringExtra(MapActivity.ADDR_RESTAURANT);
        uid = intent.getStringExtra(MapActivity.UID_RESTAURANT);
        mLikers = intent.getStringArrayListExtra(MapActivity.LIST_LIKERS);
        if (mLikers == null) {
            mLikers = new ArrayList<>();
            Log.d("TAG", "onCreate: null");
        }

        mPredictionViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(PredictionViewModel.class);
        observeViewModel();
        mPredictionViewModel.newPos(uid);

        mLikeViewModel = ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(LikeViewModel.class);
        if (mLikeViewModel == null) Log.d("TAGM", "onCreate: Likemodel NULL");
        else Log.d("TAGM", "onCreate: NONNULL");
        mLikeViewModel.init(uid,mCurrentId);
        observeLike();
        observeLunch();
        observeLuncher();


        //if (restaurant exist)
        // RestaurantManager.createRestaurant(uid);

//        UserManager.getUsersInRestaurant(uid).addOnSuccessListener(queryDocumentSnapshots -> {
//            mUsers = queryDocumentSnapshots.toObjects(User.class);
//            mLuncherList.setLayoutManager(new LinearLayoutManager(this));
//            mLuncherList.setAdapter(new WorkerAdapter(mUsers, true));
//        });
        // List<String> usersString = intent.getStringArrayListExtra(MapActivity.LIST_USER_STRING);
        //for (String userString : usersString) mUsers.add(User.parseString(userString));


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(this)
                .setDefaultRequestOptions(options)
                .load(mUrlImage)
                //  .apply
                .into(mRestaurantPicture);

        mRestaurantName.setText(mNameRestaurant);
        mRestaurantAddress.setText(mAddressRestaurant);

//        RestaurantManager.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {
//            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
//             Drawable drawableTop;
//            if ((restaurant.getLikers() != null) && (restaurant.getLikers().contains(mCurrentId))) {
//                drawableTop = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_star_24,null);
//                mLike = true;
//            } else {
//                drawableTop = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_star_grey_24,null);
//                mLike = false;
//            }
//            mLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableTop,null,null);
//        });


        mCallButton.setOnClickListener(view -> {
            callOnClickListener();
        });

        mLikeButton.setOnClickListener(view -> {
            likeOnClickListener();
        });

        mWebsiteButton.setOnClickListener(view -> {
            webOnClickListener();
        });

        mFloatingActionButton.setOnClickListener(view -> {
            addOnclicklistener();
        });

    }

    private void observeLuncher() {
        mLikeViewModel.getUsersLunch().observe(this, this::setLuncherAdapter);
    }

    private void setLuncherAdapter(List<User> users) {
        mUsers = users;
        mLuncherList.setLayoutManager(new LinearLayoutManager(this));
        mLuncherList.setAdapter(new WorkerAdapter(mUsers, true));
    }

    private void observeLunch() {
        mLikeViewModel.getIsLunch().observe(this, this::updateFloatingButton);
    }

    private void updateFloatingButton(Boolean isLunch) {
        if (isLunch) {
            mFloatingActionButton.setImageResource(R.drawable.ic_baseline_check_24);
            mLunch = true;
        } else {
            mFloatingActionButton.setImageResource(R.drawable.ic_baseline_add_circle_24);
            mLunch = false;
        }
    }

    private void addOnclicklistener() {
        if (mLunch) UserManager.updateUserRestaurant("", mCurrentId);
        else UserManager.updateUserRestaurant(uid, mCurrentId);
        mLikeViewModel.changeUserRestaurant();
      //  mLikeViewModel.init(uid,mCurrentId);

    }

    private void observeViewModel() {
        mPredictionViewModel.getmPhoneObservable().observe(this, this::updatePhone);
    }

    private void observeLike() {
        mLikeViewModel.getIsLike().observe(this, this::updateLikeButton);
    }

    private void updateLikeButton(Boolean isLike) {
        Drawable drawableTop;
        if (isLike) {
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null);
            mLike = true;
        } else {
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_grey_24, null);
            mLike = false;
        }
        mLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableTop, null, null);
    }

    private void updatePhone(Place place) {
        mPhoneNumber = place.getPhoneNumber();
        mWebsiteUri = place.getWebsiteUri();
    }

    private void webOnClickListener() {
        Intent intent = new Intent(Intent.ACTION_VIEW, mWebsiteUri);
        startActivity(intent);

    }

    private void likeOnClickListener() {
        if (mLike) mLikers.remove(mCurrentId);
        else mLikers.add(mCurrentId);
        RestaurantManager.updateRestaurantLikers(mLikers, uid);
        mLikeViewModel.changeLike();
    }

    private void callOnClickListener() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhoneNumber));
        Log.d("TAG", "callOnClickListener: " + mPhoneNumber);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
//            Log.d("TAG", "onClick: denied");
//        else {
//            Log.d("TAG", "onClick: garanted");
        startActivity(intent);
        //}
    }

    private void changeLikeButtonIcon(boolean like) {
        if (like) {

        }
    }
}
