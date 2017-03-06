package com.example.seanreddy.filescanner.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.seanreddy.filescanner.MainActivity;
import com.example.seanreddy.filescanner.R;

import java.util.HashMap;

/*
* Class to handle Notifications
* */
public class NotificationHandler {
    static NotificationHandler mNotificationHandler = new NotificationHandler();
    private HashMap<Integer,String> notificattions;
    NotificationManager notificationManager;
    RemoteViews remoteViews;

    //return of notifications instance
    public static NotificationHandler getInstance(){
        return mNotificationHandler;
    }


    NotificationHandler (){
        notificattions = new HashMap<>();
    }

    //Notification initialization
    public void initialize(int id,String message,Context context){
        notificattions.put(id, message);

        Intent intent = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.file_scanner)
                .setAutoCancel(false)
                .setProgress(0,0,false)
                .build();

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layoout);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setProgressBar(R.id.progressBar,0,0,true);
        notification.contentView = remoteViews;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,notification);

    }

    //update notification
    public void updateMessage(int id, String message, Context context){
        notificattions.put(id,message);
        initialize(id,message,context);
    }
    public void setProgress(){
        remoteViews.setProgressBar(R.id.progressBar,0,0,false);
    }
}
