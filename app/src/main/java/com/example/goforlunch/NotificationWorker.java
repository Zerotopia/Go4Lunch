package com.example.goforlunch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Calendar currentDate = Calendar.getInstance();
        Calendar notificationDate = Calendar.getInstance();

        notificationDate.set(Calendar.HOUR_OF_DAY, 12);
        notificationDate.set(Calendar.SECOND, 0);
        notificationDate.set(Calendar.MINUTE, 0);

        if (notificationDate.before(currentDate))
            notificationDate.add(Calendar.HOUR, 24);

        long delay = notificationDate.getTimeInMillis() - currentDate.getTimeInMillis();
        buildNotification();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(oneTimeWorkRequest);

        return Result.success();
    }

    private void buildNotification() {
        // if (numberOfArticles != 0) {
        //  Resources resources = getApplicationContext().getResources();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        NotificationCompat.Builder notificationBuild =
                new NotificationCompat.Builder(getApplicationContext(), "CHANNEL")
                        .setSmallIcon(R.drawable.ic_baseline_check_24)
                        .setContentTitle("notif titre")
                        .setContentText("Une notification")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(42, notificationBuild.build());
    }
}

