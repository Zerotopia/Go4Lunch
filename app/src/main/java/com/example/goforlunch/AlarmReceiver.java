package com.example.goforlunch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.example.goforlunch.model.User;
import com.example.goforlunch.repository.DetailRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private String mRestaurantName;
    private String mAdress;
    private int mNotificationId;
    private List<User> mUsers = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
     //   DetailRepository detailRepository = new DetailRepository();



        mNotificationId = intent.getIntExtra("notificationId",42);
        mRestaurantName = intent.getStringExtra("restaurantName");
        mAdress = intent.getStringExtra("address");
        //String lunchers = intent.getStringExtra("lunchers");
        String userId = intent.getStringExtra("userId");
        String restaurantId = intent.getStringExtra("restaurantId");

        mNotificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        Log.d("TAG", "onReceive: notification");
        UserManager.getUsersInRestaurant(restaurantId).addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                if (!documentSnapshot.getId().equals(userId))
                    mUsers.add(documentSnapshot.toObject(User.class));
            updateLunchers(mUsers, context);
            //data.setValue(users);
        });
        //detailRepository.getLunchers(restaurantId,userId).observe(context.ge, users -> {

        //});



     //   throw new UnsupportedOperationException("Not yet implemented");
    }

    private void updateLunchers(List<User> users, Context context) {
        String message = "Vous déjeunez ce midi avec " + luncherList(users) + "dans le restaurant " + mRestaurantName +
                " qui se situe au " + mAdress +". Bon Apétit !!";


        buildNotification(context,mNotificationId, message);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "CHANNEL",
                    "NOTIFICATION_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CHANNEL_DESCRIPTION");
            Objects.requireNonNull(mNotificationManager).createNotificationChannel(channel);
        }
    }

    private void buildNotification(Context context, int id, String message) {
        // if (numberOfArticles != 0) {
        //  Resources resources = getApplicationContext().getResources();
       // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notificationBuild =
                new NotificationCompat.Builder(context, "CHANNEL")
                        .setSmallIcon(R.drawable.ic_baseline_check_24)
                        .setContentTitle("C'est l'heure de déjeuner !")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mNotificationManager.notify(id, notificationBuild.build());
    }

    private String luncherList (List<User> lunchers) {
        String result = "";
        for (User user : lunchers) {
            result += user.getUserName() + " ";
        }
        return result;
    }


}