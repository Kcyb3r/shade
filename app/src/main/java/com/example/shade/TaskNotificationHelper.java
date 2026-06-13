package com.example.shade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class TaskNotificationHelper {

    private static final String ACTION_DUE_DATE = "com.example.shade.ACTION_TASK_DUE";

    public static void scheduleTaskNotification(Context context, String taskId, String taskText, long dueDateMillis) {
        if (dueDateMillis <= 0) return;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dueDateMillis);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long triggerAt = cal.getTimeInMillis();
        if (triggerAt <= System.currentTimeMillis()) return;

        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.setAction(ACTION_DUE_DATE);
        intent.putExtra("taskId", taskId);
        intent.putExtra("taskText", taskText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }

    public static void cancelTaskNotification(Context context, String taskId) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.setAction(ACTION_DUE_DATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
