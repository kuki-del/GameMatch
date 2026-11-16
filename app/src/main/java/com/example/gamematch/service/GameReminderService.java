package com.example.gamematch.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.gamematch.MainActivity;
import com.example.gamematch.R;

/**
 * Background Service that schedules a gentle reminder notification
 * when the user backgrounds the app with saved favorite games.
 */
public class GameReminderService extends Service {

    private static final String CHANNEL_ID = "gamematch_reminders";
    private static final int NOTIFICATION_ID = 1001;
    private Handler handler;
    private Runnable reminderRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int favCount = intent != null ? intent.getIntExtra("fav_count", 0) : 0;

        // Cancel any previously pending reminder before scheduling a new one
        if (reminderRunnable != null) {
            handler.removeCallbacks(reminderRunnable);
        }

        if (favCount > 0) {
            reminderRunnable = () -> {
                showReminderNotification(favCount);
                stopSelf(); // Cleanly terminate service once work is complete
            };
            // Triggers 5 seconds after backgrounding so you can test it easily during your demo
            handler.postDelayed(reminderRunnable, 5000);
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void showReminderNotification(int favCount) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_heart_filled)
                .setContentTitle("GameMatch! Ready to play?")
                .setContentText("You have " + favCount + " saved game(s) waiting in your collection.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Saved Game Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (handler != null && reminderRunnable != null) {
            handler.removeCallbacks(reminderRunnable);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}