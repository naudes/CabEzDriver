package com.overthere.express;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.overthere.express.utills.AndyConstants;
import com.overthere.express.utills.PreferenceHelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;

import java.util.Map;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by Jack on 18/03/2017.
 */

public class MyFcmListenerService extends FirebaseMessagingService {
    public MyFcmListenerService() {
        super();
    }
    private static final String CHANNEL_ID = "FCM";
    private static final String CHANNEL_DESCRIPTION = "CabEz Driver";
    //private NotificationManager notificationManager;
    private Uri sound = null;


    //@Override
    public void onCreate() {
        super.onCreate();

        String ChnId ="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChnId = "new_pickup" ;
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_pickup);
            createNotificationChn(ChnId,sound);
            ChnId = "new_booking";
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_booking);
            createNotificationChn(ChnId,sound);
            ChnId = "job_canceled";
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.job_canceled);
            createNotificationChn(ChnId,sound);
            ChnId = "set_destination";
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.set_destination);
            createNotificationChn(ChnId,sound);

        }
    }


    @Override
    public void onMessageReceived(RemoteMessage message){
        //String from = message.getFrom();
        Map data = message.getData();
        String mbody = data.get("message").toString();
        if(mbody==null|mbody=="") {
            mbody="You got a message";
        }
        String Channel_ID = "";
        Uri sound = null;
        if(mbody.contains("Please collect cash")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.payment_failed_driver);
        }
        switch(mbody) {
            case "New Request":
                Channel_ID = "new_pickup";
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_pickup);
                break;
            case "New Booking":
                Channel_ID = "new_booking";
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_booking);
                break;
            case "Payment Type Change":
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.payment_changed);
                break;
            case "Request Cancelled":
                Channel_ID = "job_canceled";
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.job_canceled);
                break;
            case "Set Destination":
                Channel_ID = "set_destination";
                sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.set_destination);
                break;

        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder status = new NotificationCompat.Builder(this, Channel_ID)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher)
                    //.setOnlyAlertOnce(true)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText((String)mbody)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS )
                    .setSound(sound);
            // .setContentIntent(pendingIntent)
            // .setContent(views);
            notificationManager.notify(1000, status.build());
        } else {
            Notification noti = new Notification.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(mbody)
                    .setSmallIcon(R.drawable.ic_launcher).setSound(sound)
                    .build();

            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            if (sound == null) {
                noti.defaults |= Notification.DEFAULT_SOUND;
            }
            notificationManager.notify(1000, noti);
        }


        /*Notification noti = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(mbody)
                .setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
                .setSound(sound)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        if(sound == null) { noti.defaults |= Notification.DEFAULT_SOUND;}
        notificationManager.notify(1000, noti);*/

    }

    private void createNotificationChn(String ChnId, Uri voice) {
        NotificationChannel mChannel;
        mChannel = new NotificationChannel(ChnId, AndyConstants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        mChannel.setLightColor(Color.GRAY);
        mChannel.enableLights(true);
        mChannel.setDescription(AndyConstants.CHANNEL_SIREN_DESCRIPTION);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        mChannel.setSound(voice, audioAttributes);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel( mChannel );
        }

    }


}