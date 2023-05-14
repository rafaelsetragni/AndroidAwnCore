package me.carda.awesome_notifications.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import me.carda.awesome_notifications.core.AwesomeNotifications;
import me.carda.awesome_notifications.core.AwesomeNotificationsExtension;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.NotificationChannelModel;
import me.carda.awesome_notifications.core.models.NotificationContentModel;
import me.carda.awesome_notifications.core.models.NotificationModel;

public class MainActivity
        extends AppCompatActivity {

    static private final String TAG = "MainActivity";
    private AwesomeNotifications awesomeNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupAwesomeNotifications();
        createNewExampleNotification();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void setupAwesomeNotifications() {
        try {
            AwesomeNotifications.awesomeExtensions = new AwesomeNotificationsExtension() {
                @Override
                public void loadExternalExtensions(Context context) {

                }
            };
            awesomeNotifications = new AwesomeNotifications(this);

            NotificationChannelModel channelModel =
                    new NotificationChannelModel().fromMap(new HashMap<String, Object>() {{
                        put("channelKey", "test");
                        put("channelName", "Test Channel");
                        put("channelDescription", "Channel for test purposes");
                        put("playSound", true);
                        put("importance", "High");
                    }});

            awesomeNotifications.initialize(
                    "resource://drawable/res_app_icon",
                    Arrays.asList(channelModel),
                    null,
                    0L,
                    true
            );

        } catch (AwesomeNotificationsException e) {
            e.printStackTrace();
        }
    }

    private void createNewExampleNotification() {
        try {
            NotificationModel notificationModel = new NotificationModel();
            notificationModel.content =
                    new NotificationContentModel()
                            .fromMap(new HashMap<String, Object>() {{
                                put("id", -1);
                                put("channelKey", "test");
                                put("title", "Notification Test");
                                put("body", "Notification test for AwN core");
                                put("notificationLayout", "BigPicture");
                                put("bigPicture", "https://cdn.pixabay.com/photo/2020/04/23/03/53/astronaut-5080937_960_720.jpg");
                                put("hideLargeIconOnExpand", true);
                                put("importance", "High");
                            }});

            awesomeNotifications.createNotification(
                    notificationModel,
                    (success, exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        Log.d(TAG,
                                success
                                    ? "Notification created successfully"
                                    : "Notification creation failed");
                    });

        } catch (AwesomeNotificationsException e) {
            e.printStackTrace();
        }
    }
}