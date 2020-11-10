package com.kukdudelivery.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kukdudelivery.MainActivity;
import com.kukdudelivery.R;
import com.kukdudelivery.util.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;


/**
 * Created by nisarg on 3/08/19.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    int fcid=0;
    AppPreferences appPreferences;
    Context context;

    String emoji;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        appPreferences=new AppPreferences(this);

        Log.i("MESSAGE DETAILS==== " , remoteMessage.getData().toString());



        Date date=new Date();
        fcid= (int) date.getTime();

        String from_uid="",to_uid="",title="",message="",name="",type="";
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Message data payload: " , remoteMessage.getData().toString());

            JSONObject object = null;
            try {
                object = new JSONObject(remoteMessage.getData().toString());
               // type=object.getJSONObject("payload").getString("type");
//                if(type.equalsIgnoreCase("new.message")) {
//                    from_uid = object.getJSONObject("payload").getString("from_uid");
//                    to_uid=object.getJSONObject("payload").getString("to_uid");
//                    name=object.getJSONObject("payload").getString("sender_name");
                    message=object.getString("message");
//
//
//                    Log.i("MESSAGE FROM CHAT-->",message+"TESTING");
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendNotification(from_uid,to_uid,title,message,type);

        }
        if (remoteMessage.getNotification() != null) {
            Log.d("Message Notification: " ,remoteMessage.getData().toString());
        }

    }

    private void sendNotification(String fromuid, String toid, String title, String message, String type) {

            if (!isAppIsInBackground(this)) {
                //Dashboard_.getInstance().SetImage();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String CHANNEL_ID = "my_channel_01";// The id of the channel.
                    CharSequence name = "Kukdu App";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setLightColor(Color.GREEN);
                    mChannel.enableLights(true);
                    mChannel.enableVibration(true);
                    mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                        Intent intent = new Intent(this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notificationBuilder = null;
                        try {
                            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Kukdu")
                                    .setContentText(URLDecoder.decode(message,"utf-8"))
                                    .setContentIntent(pendingIntent)
                                    .setColor(255)
                                    .setAutoCancel(true);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(mChannel);
                        notificationManager.notify(fcid, notificationBuilder.build());


                } else {
                    //Intent intent = new Intent(this, AnnouncementDetails_.class);

                        Intent intent = new Intent(this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder = null;
                        try {
                            notificationBuilder = new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Kukdu")
                                    .setContentText(URLDecoder.decode(message,"utf-8"))
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(fcid, notificationBuilder.build());

                }
            }
    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                            Log.i("isInBackground","No");
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
                Log.i("isInBackground","No");
            }
        }

        return isInBackground;
    }



}
