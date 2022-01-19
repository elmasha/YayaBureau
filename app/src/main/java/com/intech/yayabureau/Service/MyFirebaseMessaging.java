package com.intech.yayabureau.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.intech.yayabureau.Activities.MyCandidatesActivity;
import com.intech.yayabureau.R;
import com.intech.yayabureau.notification.OreoAndAboveNotification;


import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getTitle();
        String  title2 = remoteMessage.getData().get("title");
        String  message = remoteMessage.getData().get("message");
        String  ord_id = remoteMessage.getData().get("user_id");


        String click_ation = remoteMessage.getNotification().getClickAction();



        if (remoteMessage.getData().isEmpty()){


            ShowNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),click_ation,ord_id);

        }else {

           ShowNotification(remoteMessage.getData(),click_ation,ord_id);

        }

    }


    private void ShowNotification(Map<String, String> data, String click_ation, String ord_id) {

        String title = data.get("title").toString();
        String message = data.get("message").toString();


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_notifications_38)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(message)
                .setPriority(NotificationCompat.DEFAULT_SOUND)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.popglass))
                .setColor(getResources().getColor(R.color.main));
        int notificationId = (int) System.currentTimeMillis();



        Intent resultintent = new Intent(click_ation);
        resultintent.putExtra("user_id",ord_id);
        PendingIntent  resultPendingintent =  PendingIntent.getActivity(
                this,
                0,
                resultintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingintent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,mBuilder.build());




        if (notificationManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                        "El_Tech_CH_01",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(true);
                channel.setShowBadge(true);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0,100});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(channel);

            }

            notificationManager.notify(notificationId, mBuilder.build());
        }



    }

    private void ShowNotification(String title, String body,String click_ation,String ord_id) {


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_notifications_38)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setPriority(NotificationCompat.DEFAULT_SOUND)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.popglass))
                .setColor(getResources().getColor(R.color.main));
        int notificationId = (int) System.currentTimeMillis();



        Intent resultintent = new Intent(click_ation);
        resultintent.putExtra("user_id",ord_id);
        PendingIntent  resultPendingintent =  PendingIntent.getActivity(
                this,
                0,
                resultintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingintent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId,mBuilder.build());



    }





    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replace("[\\D]",""));

        Intent intent = new Intent(this, MyCandidatesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUID",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent Pintent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(Pintent)
                .setSound(defSound);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
         int j = 0;
         if (i>0){
             j=i;
         }
         notificationManager.notify(j,builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {


        String OrdID = remoteMessage.getData().get("user_id");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("message");


        int notificationId = (int) System.currentTimeMillis();

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent intent = new Intent(this, MyCandidatesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_id",OrdID);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent Pintent = PendingIntent.getActivity(this,notificationId,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        OreoAndAboveNotification oreoAndAboveNotification = new OreoAndAboveNotification(this);
        Notification.Builder builder = oreoAndAboveNotification.getNotification(title,body,Pintent,defSound,icon);


        oreoAndAboveNotification.getManger().notify(notificationId,builder.build());

    }
}
