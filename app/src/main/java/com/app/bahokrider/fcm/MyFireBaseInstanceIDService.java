package com.app.bahokrider.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.app.bahokrider.activities.NotificationsActivity;
import com.app.bahokrider.activities.OrderDetailsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.app.bahokrider.R;
import com.app.bahokrider.activities.MainScreenActivity;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.utils.AppConstants;

public class MyFireBaseInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendMyNotification(remoteMessage);
    }

    private void sendMyNotification(RemoteMessage remoteMessage) {

        try {
            String userId = SharedPreferencesManager.getInstance().getSomeStringValue(getApplicationContext(), AppConstants.USER_ID);

            if (TextUtils.isEmpty(userId))
                return;

            String title = "";
            String notiType = "";
            String orderId = "";

            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getBody();
                notiType = remoteMessage.getData().get("type");
                orderId = remoteMessage.getData().get("order_id");
            }

            if (remoteMessage.getData() != null) {
                title = remoteMessage.getData().get("title");
                notiType = remoteMessage.getData().get("type");
                orderId = remoteMessage.getData().get("order_id");
            }

            if (!TextUtils.isEmpty(notiType) && notiType.equalsIgnoreCase("new_order")) {
                Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("type", notiType);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            Intent intent = null;
            if (TextUtils.isEmpty(orderId))
                intent = new Intent(this, NotificationsActivity.class);
            else {
                intent = new Intent(this, OrderDetailsActivity.class);
                intent.putExtra("orderId", orderId);
            }

            stackBuilder.addNextIntentWithParentStack(intent);

            PendingIntent pendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = getString(R.string.default_notification_channel_id);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                NotificationChannel channel = new NotificationChannel(channelId,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(soundUri, audioAttributes);
                channel.enableVibration(true);
                channel.enableLights(true);
                if (notificationManager != null)
                    notificationManager.createNotificationChannel(channel);
            }

            if (notificationManager != null)
                notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
