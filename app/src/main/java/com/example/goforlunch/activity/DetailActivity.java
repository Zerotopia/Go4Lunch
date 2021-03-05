package com.example.goforlunch.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.AlarmReceiver;
import com.example.goforlunch.R;
import com.example.goforlunch.RestaurantManager;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.WorkerAdapter;
import com.example.goforlunch.databinding.ActivityDetailBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.DetailViewModel;
import com.example.goforlunch.viewmodel.LikeViewModel;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    // private PredictionViewModel mPredictionViewModel;
    private DetailViewModel mDetailViewModel;
    private SharedPreferences mPreferences;
    private String mCurrentId;
    private Bitmap mBitmap;

    //private LikeViewModel mLikeViewModel;
    private ActivityDetailBinding mBinding;

    private boolean mLike;
    private boolean mLunch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mBinding.setDetailActivity(this);

        mRestaurantPicture = mBinding.detailImageview;
        //mRestaurantName = findViewById(R.id.detail_restaurant_name);
        //mRestaurantAddress = findViewById(R.id.detail_restaurant_address);
        mLuncherList = mBinding.detailLuncherRecyclerview;
        // mCallButton = findViewById(R.id.detail_call_button);
        mLikeButton = mBinding.detailLikeButton;
        // mWebsiteButton = findViewById(R.id.detail_website_button);
        mFloatingActionButton = mBinding.floatingButton;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
        Log.d("TAG", "onCreate: ID = " + mCurrentId);

        Intent intent = getIntent();
        uid = intent.getStringExtra(MapActivity.UID_RESTAURANT);
        mUrlImage = intent.getStringExtra(MapActivity.URL_IMAGE);
        mNameRestaurant = intent.getStringExtra(MapActivity.NAME_RESTAURANT);
        mAddressRestaurant = intent.getStringExtra(MapActivity.ADDR_RESTAURANT);
        mLikers = intent.getStringArrayListExtra(MapActivity.LIST_LIKERS);
        if (mLikers == null) {
            mLikers = new ArrayList<>();
            Log.d("TAG", "onCreate: null");
        }

        Log.d("TAG", ": In detailactivity : url :" + mUrlImage + ", namer : " + mNameRestaurant + ", id: " + uid + ", addr: " + mAddressRestaurant);

        mDetailViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(DetailViewModel.class);
        mDetailViewModel.init(uid, mCurrentId);
        observeViewModel();
        mDetailViewModel.setId(uid);

        mBinding.setDetailViewModel(mDetailViewModel);
        mBinding.setLifecycleOwner(this);

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

        // mRestaurantName.setText(mNameRestaurant);
        // mRestaurantAddress.setText(mAddressRestaurant);

//        mCallButton.setOnClickListener(view -> {
//            callOnClickListener();
//        });
//
//        mLikeButton.setOnClickListener(view -> {
//            likeOnClickListener();
//        });
//
//        mWebsiteButton.setOnClickListener(view -> {
//            webOnClickListener();
//        });
//
//        mFloatingActionButton.setOnClickListener(view -> {
//            addOnclicklistener();
//        });

    }

    private void setLuncherAdapter(List<User> users) {
        mUsers = users;
        mLuncherList.setLayoutManager(new LinearLayoutManager(this));
        mLuncherList.setAdapter(new WorkerAdapter(mUsers, true));
    }

    private void updateFloatingButton(Boolean isLunch) {
        mLunch = isLunch;

//        if (isLunch) {
//            mFloatingActionButton.setImageResource(R.drawable.ic_baseline_check_24);
//            mLunch = true;
//        } else {
//            mFloatingActionButton.setImageResource(R.drawable.ic_baseline_add_circle_24);
//            mLunch = false;
//        }
    }

    public void addOnclicklistener() {
        Log.d("TAG", "addOnclicklistener: ");
        createRestaurant();

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("notificationId", 42);
        intent.putExtra("restaurantName", mNameRestaurant);
        intent.putExtra("address", mAddressRestaurant);
        String lunchers = "";
        for (User user : mUsers) {
            lunchers += user.getUserName() + " ";
        }
        intent.putExtra("lunchers", lunchers);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (mLunch) {
            UserManager.updateUserRestaurant("", mCurrentId, "");
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.cancel(42);
            alarmManager.cancel(alarmIntent);
        } else {
            UserManager.updateUserRestaurant(uid, mCurrentId, mNameRestaurant);

            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, 12);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            long notificationTime = currentTime.getTimeInMillis();

            alarmManager.set(AlarmManager.RTC_WAKEUP,notificationTime,alarmIntent);


        }
       // mDetailViewModel.setIsLunch(mLunch);
        mDetailViewModel.changeUserRestaurant();

        //  mLikeViewModel.init(uid,mCurrentId);

    }

    private void createRestaurant() {
        RestaurantManager.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {
            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            if (currentRestaurant == null) {
                RestaurantManager.createRestaurant(uid);
                RestaurantManager.updateRestaurantName(mNameRestaurant, uid);
            }
        });
    }

    private void observeViewModel() {
        mDetailViewModel.getPlaceObservable().observe(this, this::updatePlace);
        mDetailViewModel.getPhotoObservable().observe(this, this::updateImage);
        mDetailViewModel.getIsLunch().observe(this, this::updateFloatingButton);
        mDetailViewModel.getUsersLunch().observe(this, this::setLuncherAdapter);
        mDetailViewModel.getIsLike().observe(this, this::updateLikeButton);
    }

    private void updateImage(Bitmap bitmap) {
        mRestaurantPicture.setImageBitmap(bitmap);
    }

    private void updatePlace(Place place) {
        mPhoneNumber = place.getPhoneNumber();
        mWebsiteUri = place.getWebsiteUri();
        mAddressRestaurant = place.getAddress();
        mNameRestaurant = place.getName();
    }


    private void updateLikeButton(Boolean isLike) {
        Drawable drawableTop;
        if (isLike)
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null);
        else
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_grey_24, null);
        mLike = isLike;
        mLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableTop, null, null);
    }

//    private void updatePhone(Place place) {
//        mPhoneNumber = place.getPhoneNumber();
//        mWebsiteUri = place.getWebsiteUri();
//    }

    public void webOnClickListener() {
        Intent intent = new Intent(Intent.ACTION_VIEW, mWebsiteUri);
        startActivity(intent);

    }

    public void likeOnClickListener() {
        createRestaurant();
        if (mLike) mLikers.remove(mCurrentId);
        else mLikers.add(mCurrentId);
        RestaurantManager.updateRestaurantLikers(mLikers, uid);
        mDetailViewModel.changeLike();
    }

    public void callOnClickListener() {
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

//    private void changeLikeButtonIcon(boolean like) {
//        if (like) {
//
//        }
//    }
}
