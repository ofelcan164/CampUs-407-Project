package com.example.campus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    public static final String CHANNEL_4_ID = "channel4";
    public static final String CHANNEL_5_ID = "channel5";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {

        // Check Device Build Number
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Urgency Rating: !",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is Urgency Rating 1 Channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Urgency Rating: !!",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is Urgency Rating 2 Channel");
            manager.createNotificationChannel(channel2);

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Urgency Rating: !!!",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is Urgency Rating 3 Channel");
            manager.createNotificationChannel(channel3);

            NotificationChannel channel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "Urgency Rating: !!!!",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is Urgency Rating 4 Channel");
            manager.createNotificationChannel(channel4);

            NotificationChannel channel5 = new NotificationChannel(
                    CHANNEL_5_ID,
                    "Urgency Rating: !!!!!",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is Urgency Rating 5 Channel");
            manager.createNotificationChannel(channel5);
        }
    }
}
