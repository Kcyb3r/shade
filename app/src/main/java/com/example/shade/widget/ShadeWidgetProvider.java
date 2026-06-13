package com.example.shade.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.example.shade.AmoledBloomManager;
import com.example.shade.MainActivity;
import com.example.shade.R;
import com.example.shade.TaskRepository;
import com.example.shade.WidgetPreferences;

public class ShadeWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_TOGGLE_TASK = "com.example.shade.ACTION_TOGGLE_TASK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        WidgetPreferences widgetPreferences = new WidgetPreferences(context);

        boolean showDividers = widgetPreferences.getShowDividers();
        int layoutId = showDividers ? R.layout.widget_shade : R.layout.widget_shade_no_divider;

        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        String bgPreset = widgetPreferences.getWidgetBackground();
        String resolvedPreset = bgPreset.equals("auto") ? WidgetPreferences.resolveAutoPreset() : bgPreset;

        if (resolvedPreset.equals("amoled_bloom")) {
            int frame = widgetPreferences.getAmoledFrame();
            int drawableRes = WidgetPreferences.getAmoledFrameDrawable(frame);
            views.setInt(R.id.widget_background, "setBackgroundResource", drawableRes);
            AmoledBloomManager.scheduleNextFrame(context);
        } else if (resolvedPreset.equals("transparent")) {
            views.setInt(R.id.widget_background, "setBackgroundColor", Color.TRANSPARENT);
            AmoledBloomManager.cancelAnimation(context);
        } else {
            int drawableRes = WidgetPreferences.getBackgroundDrawableResId(resolvedPreset);
            if (drawableRes != 0) {
                views.setInt(R.id.widget_background, "setBackgroundResource", drawableRes);
            }
            AmoledBloomManager.cancelAnimation(context);
        }

        views.setTextViewText(R.id.empty_view, widgetPreferences.getEmptyMessage());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.empty_view, pendingIntent);

        Intent serviceIntent = new Intent(context, ShadeWidgetService.class);
        serviceIntent.putExtra("bgPreset", bgPreset);
        serviceIntent.putExtra("showItemBg", widgetPreferences.getShowItemBg());
        views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        Intent clickIntent = new Intent(context, ShadeWidgetProvider.class);
        clickIntent.setAction(ACTION_TOGGLE_TASK);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0,
                clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigActivity.deleteWidgetPrefs(context, appWidgetId);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] remainingIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, ShadeWidgetProvider.class));
        if (remainingIds == null || remainingIds.length == 0) {
            AmoledBloomManager.cancelAnimation(context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_TOGGLE_TASK.equals(intent.getAction())) {
            String taskId = intent.getStringExtra("taskId");
            if (taskId != null) {
                TaskRepository repository = new TaskRepository(context);
                repository.toggleTaskCompleted(taskId);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] ids = appWidgetManager
                        .getAppWidgetIds(new android.content.ComponentName(context, ShadeWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager
                    .getAppWidgetIds(new android.content.ComponentName(context, ShadeWidgetProvider.class));
            onUpdate(context, appWidgetManager, ids);
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);
        }
    }
}
