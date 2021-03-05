package com.example.goforlunch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        int notificationId = intent.getIntExtra("notificationId",42);
        String restaurantName = intent.getStringExtra("restaurantName");
        String adresse = intent.getStringExtra("address");
        String lunchers = intent.getStringExtra("lunchers");

        String message = "Vous déjeunez ce midi avec " + lunchers + "dans le restaurant " + restaurantName +
        " qui se situe au " + adresse +". Bon Apétit !!";

        mNotificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        buildNotification(context,notificationId, message);

     //   throw new UnsupportedOperationException("Not yet implemented");
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
                        .setContentTitle("notif titre")
                        .setContentText("Lunc time")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mNotificationManager.notify(id, notificationBuild.build());
    }
}