package com.example.goforlunch.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.example.goforlunch.R;
import com.example.goforlunch.repository.UserManager;
import com.example.goforlunch.activity.DetailActivity;
import com.example.goforlunch.activity.SettingsActivity;
import com.example.goforlunch.model.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "CHANNEL";
    private NotificationManager mNotificationManager;
    private String mRestaurantName;
    private String mAddress;
    private int mNotificationId;
    private List<User> mUsers = new ArrayList<>();

    /**
     * We send a notification only if the notification is enable in the settings activity.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean notificationEnable = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.NOTIFICATION_ENABLE, true);
        if (notificationEnable) {
            mNotificationId = intent.getIntExtra(DetailActivity.NOTIFICATION_ID, 42);
            mRestaurantName = intent.getStringExtra(DetailActivity.RESTAURANT_NAME);
            mAddress = intent.getStringExtra(DetailActivity.ADDRESS);
            String userId = intent.getStringExtra(DetailActivity.USER_ID);
            String restaurantId = intent.getStringExtra(DetailActivity.RESTAURANT_ID);

            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel();
            UserManager.getUsersInRestaurant(restaurantId).addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                    if (!documentSnapshot.getId().equals(userId))
                        mUsers.add(documentSnapshot.toObject(User.class));
                updateLunchers(mUsers, context);
            });
        }
    }

    /**
     * Create the message that will be send in the notification.
     *
     * @param users   the list of workmates who lunch with th user.
     * @param context need to get string resources.
     */
    private void updateLunchers(List<User> users, Context context) {
        String message;

        if (users.size() == 0)
            message = context.getResources().getString(R.string.notification_solo, mRestaurantName, mAddress);
        else
            message = context.getResources().getString(R.string.notification_message, luncherList(users), mRestaurantName, mAddress);
        buildNotification(context, mNotificationId, message);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CHANNEL_DESCRIPTION");
            Objects.requireNonNull(mNotificationManager).createNotificationChannel(channel);
        }
    }

    private void buildNotification(Context context, int id, String message) {
        NotificationCompat.Builder notificationBuild =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_check_24)
                        .setContentTitle(context.getResources().getString(R.string.notification_title))
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mNotificationManager.notify(id, notificationBuild.build());
    }

    /**
     * @param lunchers the list of workmates who lunch with th user.
     * @return the string composed of the names in the list lunchers separate
     * by a coma ","
     */
    public static String luncherList(List<User> lunchers) {
        StringBuilder result = new StringBuilder();
        for (User user : lunchers) {
            result.append(user.getUserName()).append(", ");
        }
        return result.toString();
    }
}