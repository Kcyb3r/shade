package com.example.shade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.shade.widget.ShadeWidgetProvider;

public class AmoledBloomManager {

    public static final String ACTION_AMOLED_FRAME = "com.example.shade.ACTION_AMOLED_FRAME";
    private static final int FRAME_INTERVAL_MS = 5000;

    public static void scheduleNextFrame(Context context) {
        Intent intent = new Intent(context, AmoledBloomReceiver.class);
        intent.setAction(ACTION_AMOLED_FRAME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + FRAME_INTERVAL_MS, pendingIntent);
    }

    public static void cancelAnimation(Context context) {
        Intent intent = new Intent(context, AmoledBloomReceiver.class);
        intent.setAction(ACTION_AMOLED_FRAME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
