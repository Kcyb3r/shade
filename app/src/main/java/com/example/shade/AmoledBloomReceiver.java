package com.example.shade;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.shade.widget.ShadeWidgetProvider;

public class AmoledBloomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AmoledBloomManager.ACTION_AMOLED_FRAME.equals(intent.getAction())) {
            WidgetPreferences prefs = new WidgetPreferences(context);
            prefs.incrementAmoledFrame();

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, ShadeWidgetProvider.class));
            if (ids.length == 0) return;

            Intent updateIntent = new Intent(context, ShadeWidgetProvider.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(updateIntent);
        }
    }
}
