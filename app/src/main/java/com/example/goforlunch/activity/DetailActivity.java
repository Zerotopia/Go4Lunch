package com.example.goforlunch.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.receiver.AlarmReceiver;
import com.example.goforlunch.R;
import com.example.goforlunch.repository.RestaurantManager;
import com.example.goforlunch.repository.UserManager;
import com.example.goforlunch.view.WorkerAdapter;
import com.example.goforlunch.databinding.ActivityDetailBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.DetailViewModel;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Display detailed informations about a restaurant.
 */
public class DetailActivity extends AppCompatActivity {

    /**
     * TAGs for shared preferences
     */
    public static final String CURRENT_RESTAURANT = "Current restaurant Id";

    /**
     * TAGs for build notification
     */
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String ADDRESS = "address";
    public static final String USER_ID = "userId";
    public static final String RESTAURANT_ID = "restaurantId";

    private ImageView mRestaurantPicture;
    private RecyclerView mLuncherList;
    private Button mLikeButton;
    private RatingBar mRatingBar;

    private String mNameRestaurant;
    private String mAddressRestaurant;
    private String mPhoneNumber;
    private Uri mWebsiteUri;
    private List<String> mLikers;

    private DetailViewModel mDetailViewModel;
    private ActivityDetailBinding mBinding;

    private SharedPreferences mPreferences;

    /**
     * The Id of the current user.
     */
    private String mCurrentId;
    /**
     * The Id of the restaurant that we display the detail information.
     */
    private String mDetailRestaurantId;
    /**
     * The current restaurant where the user has choice to lunch (if exists)
     */
    private Restaurant mCurrentLunchRestaurant;
    /**
     * mLike is true if the user like the restaurant, else false.
     */
    private boolean mLike;
    /**
     * mLunch is true if the user decided to lunch in the restaurant, else false.
     */
    private boolean mLunch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView();
        setInfo();
        setViewModel();
    }

    /**
     * Set binding for databinding.
     */
    private void bindingView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mBinding.setDetailActivity(this);
        mBinding.setLifecycleOwner(this);

        mRestaurantPicture = mBinding.detailImageview;
        mLuncherList = mBinding.detailLuncherRecyclerview;
        mLikeButton = mBinding.detailLikeButton;
        mRatingBar = mBinding.ratio;
    }

    /**
     * Set Information need to detail activity.
     */
    private void setInfo() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");

        Intent intent = getIntent();
        mDetailRestaurantId = intent.getStringExtra(MapActivity.UID_RESTAURANT);
        mLikers = intent.getStringArrayListExtra(MapActivity.LIST_LIKERS);
        if (mLikers == null) mLikers = new ArrayList<>();
    }

    /**
     * VIEW MODEL
     * Set and observe ViewModel
     */

    /**
     * Set ViewModel and data binding.
     */
    private void setViewModel() {
        mDetailViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(this)).get(DetailViewModel.class);
        mDetailViewModel.init(mDetailRestaurantId, mCurrentId);
        observeViewModel();
        mDetailViewModel.setId(mDetailRestaurantId);

        mBinding.setDetailViewModel(mDetailViewModel);
    }

    /**
     * observe View Model
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

    /**
     * Observe the ratio and display it with a number of star
     * that depends of the ratio.
     *
     * @param integer ratio of the restaurant.
     */
    private void updateRatio(Integer integer) {
        if (integer == 0)
            mRatingBar.setVisibility(View.GONE);
        else {
            mRatingBar.setNumStars(integer);
            mRatingBar.setRating(integer);
        }
    }

    /**
     * Observe place return by the view model and set all info
     * in appropriate variables.
     *
     * @param place place of the restaurant
     */
    private void updatePlace(Place place) {
        mPhoneNumber = place.getPhoneNumber();
        mWebsiteUri = place.getWebsiteUri();
        mAddressRestaurant = place.getAddress();
        mNameRestaurant = place.getName();
    }

    /**
     * Observe the bitmap return by the view model and
     * set the picture of the restaurant.
     *
     * @param bitmap Image of  the restaurant.
     */
    private void updateImage(Bitmap bitmap) {
        mRestaurantPicture.setImageBitmap(bitmap);
    }

    /**
     * Observe if the restaurant is selected for lunch or not and
     * update the boolean mLucnh with the observed value.
     *
     * @param isLunch boolean tha is true if the user has selected the
     *                restaurant for lunch, else false.
     */
    private void updateFloatingButton(Boolean isLunch) {
        mLunch = isLunch;
    }

    /**
     * Observe the users that lunch in the restaurants and update the
     * recyclerview that display the list of co-lunchers.
     *
     * @param users all other users that lunch in this restaurant.
     */
    private void setLuncherAdapter(List<User> users) {
        mLuncherList.setLayoutManager(new LinearLayoutManager(this));
        mLuncherList.setAdapter(new WorkerAdapter(users, true));
    }

    /**
     * Observe if the user like or unlike the restaurant and
     * update the star icon and the boolean mLike.
     *
     * @param isLike true if the user like the restaurant else false.
     */
    private void updateLikeButton(Boolean isLike) {
        Drawable drawableTop;
        if (isLike)
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_24, null);
        else
            drawableTop = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_star_grey_24, null);
        mLike = isLike;
        mLikeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableTop, null, null);
    }

    /**
     * Observe the current restaurant selected by the user for lunch.
     * if there exist a restaurant selected for lunch then we save this restaurant in
     * mCurrentLunchRestaurant.
     *
     * @param currentRestaurantId Id of the current restaurant that the user has selected to lunch.
     */
    private void updateRestaurant(String currentRestaurantId) {
        if ((currentRestaurantId != null) && !currentRestaurantId.isEmpty())
            RestaurantManager.getRestaurant(currentRestaurantId).addOnSuccessListener(documentSnapshot -> {
                mCurrentLunchRestaurant = documentSnapshot.toObject(Restaurant.class);
                mCurrentLunchRestaurant.setId(documentSnapshot.getId());
            });
    }

    /**
     * CLICKLISTENER
     */

    /**
     * Call when the user clicked on the Floating button to choice
     * the restaurant where the user want to lunch.
     * 1 - We create the restaurant in Firebase if he doesn't exists.
     * 2 - We update all information on Firebase relative to this click.
     * 3 - We update Shared Preferences about the current restaurant choice.
     * 4 - We set AlarmManager to manage the notification.
     */
    public void addOnclicklistener() {
        createRestaurant();
        updateFireBaseDataBase();
        updateCurrentRestaurantInSharedPreferences();
        setAlarmManager();
        mDetailViewModel.changeUserRestaurant();
    }

    /**
     * Call when the user clicked on the website button.
     * Just start an activity with a browser that open the web page.
     */
    public void webOnClickListener() {
        Intent intent = new Intent(Intent.ACTION_VIEW, mWebsiteUri);
        startActivity(intent);
    }

    /**
     * Call when the user clicked on the like button.
     * 1 - We create the restaurant in firebase if he doesn't exists.
     * 2 - Depends of the user like/unlike, we add/remove the current user
     * of the list of the "Likers" of the restaurant.
     */
    public void likeOnClickListener() {
        createRestaurant();
        if (mLike) mLikers.remove(mCurrentId);
        else mLikers.add(mCurrentId);
        RestaurantManager.updateRestaurantLikers(mLikers, mDetailRestaurantId);
        mDetailViewModel.changeLike();
    }

    /**
     * Call when the user clicked on the Call button.
     * Just open the phone application with the phone number of the restaurant.
     */
    public void callOnClickListener() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhoneNumber));
        startActivity(intent);
    }

    /**
     * ANNEXE FUNCTION
     */

    /**
     * Create the restaurant in Firebase if he doesn't exists.
     */
    private void createRestaurant() {
        RestaurantManager.getRestaurant(mDetailRestaurantId).addOnSuccessListener(documentSnapshot -> {
            Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
            if (currentRestaurant == null) {
                RestaurantManager.createRestaurant(mDetailRestaurantId);
                RestaurantManager.updateRestaurantName(mNameRestaurant, mDetailRestaurantId);
                RestaurantManager.updateRestaurantLunchers(0, mDetailRestaurantId);
                RestaurantManager.updateRestaurantLikers(mLikers, mDetailRestaurantId);
            }
        });
    }

    /**
     * This methode is call whe the user choices or "unchoices" the restaurant.
     * There is several case :
     * Case 1 : The user has already choice another restaurant and make a new choice.
     * Then the number of lunchers of the previous restaurant should be decrease by 1.
     * Case 2 : The user has already choice this restaurant and he has changed her mind.
     * Then the restaurant we erase the name of the restaurant where the user should be go.
     * Case 3 : The user choice this restaurant.
     * Then the restaurant where the user has decided to lunch should be save in the database and
     * the number of lunchers of this restaurant should be increase by 1.
     */
    private void updateFireBaseDataBase() {
        // Case 1
        if ((mCurrentLunchRestaurant != null) && !mCurrentLunchRestaurant.getId().isEmpty())
            RestaurantManager.updateRestaurantLunchers(
                    mCurrentLunchRestaurant.getNumberOfLunchers() - 1,
                    mCurrentLunchRestaurant.getId());
        // Case 2
        if (mLunch) {
            UserManager.updateUserRestaurant("", mCurrentId, "");
        }
        // Case 3
        else {
            UserManager.updateUserRestaurant(mDetailRestaurantId, mCurrentId, mNameRestaurant);
            RestaurantManager.getRestaurant(mDetailRestaurantId).addOnSuccessListener(documentSnapshot -> {
                Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
                RestaurantManager.updateRestaurantLunchers(currentRestaurant.getNumberOfLunchers() + 1, mDetailRestaurantId);
            });
            mDetailViewModel.updateCurrentRestaurant();
        }

    }

    /**
     * When the user choice a restaurant, we saves her choice in Shared Preferences
     * to open detail activity with the good restaurant when the user click on "Your lunch"
     * in the NavigationDrawer.
     */
    private void updateCurrentRestaurantInSharedPreferences() {
        SharedPreferences.Editor edit = mPreferences.edit();
        if (mLunch) edit.putString(CURRENT_RESTAURANT, "");
        else edit.putString(CURRENT_RESTAURANT, mDetailRestaurantId);
        edit.apply();
    }

    /**
     * Set AlarmManager to send a notification to the user at 12:00 PM
     */
    private void setAlarmManager() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(NOTIFICATION_ID, 42);
        intent.putExtra(RESTAURANT_NAME, mNameRestaurant);
        intent.putExtra(ADDRESS, mAddressRestaurant);
        intent.putExtra(USER_ID, mCurrentId);
        intent.putExtra(RESTAURANT_ID, mDetailRestaurantId);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (mLunch)
            alarmManager.cancel(alarmIntent);
        else {
            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, 12);
            currentTime.set(Calendar.MINUTE, 0);
            currentTime.set(Calendar.SECOND, 0);
            long notificationTime = currentTime.getTimeInMillis();

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, alarmIntent);
        }
    }
}
