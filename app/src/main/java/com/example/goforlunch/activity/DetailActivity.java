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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

    private static final String TAG = "DETAILACTIVITYTAG";
    private ImageView mRestaurantPicture;
    private RecyclerView mLuncherList;

    private Button mLikeButton;
    private FloatingActionButton mFloatingActionButton;
    private RatingBar mRatingBar;

    private String mUrlImage;
    private String mNameRestaurant;
    private String mAddressRestaurant;
    private String mPhoneNumber;
    private Uri mWebsiteUri;
    private List<User> mUsers = new ArrayList<>();
    private List<String> mLikers = new ArrayList<>();
    private String uid;
    private DetailViewModel mDetailViewModel;
    private SharedPreferences mPreferences;
    private String mCurrentId;

    private ActivityDetailBinding mBinding;

    private boolean mLike;
    private boolean mLunch;

    private Restaurant mCurrentRestaurant;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Enter onCreate");
        bindingView();
        initCurrentId();
        setInfoRestaurant();
        setViewModel();
        Log.d(TAG, "onCreate: End onCreate");
    }

    /**
     * VIEW MODEL OBSRVERS
     */
    private void observeViewModel() {
        mDetailViewModel.getPlaceObservable().observe(this, this::updatePlace);
        mDetailViewModel.getPhotoObservable().observe(this, this::updateImage);
        mDetailViewModel.getIsLunch().observe(this, this::updateFloatingButton);
        mDetailViewModel.getUsersLunch().observe(this, this::setLuncherAdapter);
        mDetailViewModel.getIsLike().observe(this, this::updateLikeButton);
        mDetailViewModel.getCurrentRestaurantId().observe(this, this::updateRestaurant);
        mDetailViewModel.getRatioRestaurant().observe(this, this::updateRatio);
    }

    private void updateRatio(Integer integer) {
        if (integer == 0)
            mRatingBar.setVisibility(View.GONE);
        else {
            mRatingBar.setNumStars(integer);
            mRatingBar.setRating(integer);
        }
    }

    private void updatePlace(Place place) {
        mPhoneNumber = place.getPhoneNumber();
        mWebsiteUri = place.getWebsiteUri();
        mAddressRestaurant = place.getAddress();
        mNameRestaurant = place.getName();
    }

    private void updateImage(Bitmap bitmap) {
        mRestaurantPicture.setImageBitmap(bitmap);
    }

    private void updateFloatingButton(Boolean isLunch) {
        mLunch = isLunch;
    }

    private void setLuncherAdapter(List<User> users) {
        mUsers = users;
        mLuncherList.setLayoutManager(new LinearLayoutManager(this));
        mLuncherList.setAdapter(new WorkerAdapter(mUsers, true));
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

    private void updateRestaurant(String currentRestaurantId) {
        if ((currentRestaurantId != null) && !currentRestaurantId.isEmpty())
            RestaurantManager.getRestaurant(currentRestaurantId).addOnSuccessListener(documentSnapshot -> {
                mCurrentRestaurant = documentSnapshot.toObject(Restaurant.class);
                mCurrentRestaurant.setId(documentSnapshot.getId());
            });
    }

/********************************************************************************/
    /**
     * CLICKLISTENER
     */
    public void addOnclicklistener() {
        Log.d("TAG", "addOnclicklistener: ");
        createRestaurant();
        if ((mCurrentRestaurant != null) && !mCurrentRestaurant.getId().isEmpty())
            RestaurantManager.updateRestaurantLunchers(mCurrentRestaurant.getNumberOfLunchers() - 1, mCurrentRestaurant.getId());
        setAlarmManager();
        mDetailViewModel.changeUserRestaurant();
    }

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
        startActivity(intent);

    }
/*************************************************************************************/
    /**
     * annexe function
     */
    private void setAlarmManager() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("notificationId", 42);
        intent.putExtra("restaurantName", mNameRestaurant);
        intent.putExtra("address", mAddressRestaurant);
        intent.putExtra("userId", mCurrentId);
        intent.putExtra("restaurantId", uid);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (mLunch) {
            UserManager.updateUserRestaurant("", mCurrentId, "");
            alarmManager.cancel(alarmIntent);
        } else {
            UserManager.updateUserRestaurant(uid, mCurrentId, mNameRestaurant);
            RestaurantManager.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {
                Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
                RestaurantManager.updateRestaurantLunchers(currentRestaurant.getNumberOfLunchers() + 1, uid);
            });
            mDetailViewModel.updateCurrentRestaurant();

            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, 12);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            long notificationTime = currentTime.getTimeInMillis();

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, alarmIntent);
        }
    }

    private void createRestaurant() {
        RestaurantManager.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {
            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            if (currentRestaurant == null) {
                RestaurantManager.createRestaurant(uid);
                RestaurantManager.updateRestaurantName(mNameRestaurant, uid);
                RestaurantManager.updateRestaurantLunchers(0, uid);
            }

        });
    }


    private void setViewModel() {
        mDetailViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(DetailViewModel.class);
        mDetailViewModel.init(uid, mCurrentId);
        observeViewModel();
        mDetailViewModel.setId(uid);

        mBinding.setDetailViewModel(mDetailViewModel);
        mBinding.setLifecycleOwner(this);
    }

    private void setInfoRestaurant() {
        Intent intent = getIntent();
        uid = intent.getStringExtra(MapActivity.UID_RESTAURANT);
        mUrlImage = intent.getStringExtra(MapActivity.URL_IMAGE);
        mNameRestaurant = intent.getStringExtra(MapActivity.NAME_RESTAURANT);
        mAddressRestaurant = intent.getStringExtra(MapActivity.ADDR_RESTAURANT);

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

    }

    private void initCurrentId() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
    }

    private void bindingView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mBinding.setDetailActivity(this);

        mRestaurantPicture = mBinding.detailImageview;
        mLuncherList = mBinding.detailLuncherRecyclerview;
        mLikeButton = mBinding.detailLikeButton;
        mFloatingActionButton = mBinding.floatingButton;
        mRatingBar = mBinding.ratio;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();
    }
}
