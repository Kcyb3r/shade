package com.example.shade.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.shade.R;
import com.example.shade.Task;
import com.example.shade.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class ShadeWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        String bgPreset = intent.getStringExtra("bgPreset");
        boolean showItemBg = intent.getBooleanExtra("showItemBg", true);
        return new ShadeRemoteViewsFactory(this.getApplicationContext(), bgPreset, showItemBg);
    }
}

class ShadeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private List<Task> tasks = new ArrayList<>();
    private TaskRepository repository;
    private String bgPreset;
    private boolean showItemBg;

    public ShadeRemoteViewsFactory(Context context, String bgPreset, boolean showItemBg) {
        this.context = context;
        this.repository = new TaskRepository(context);
        this.bgPreset = bgPreset != null ? bgPreset : "teal";
        this.showItemBg = showItemBg;
    }

    @Override
    public void onCreate() {
        tasks = repository.getTasks();
    }

    @Override
    public void onDataSetChanged() {
        tasks = repository.getTasks();
    }

    @Override
    public void onDestroy() {
        tasks.clear();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= tasks.size())
            return null;

        Task task = tasks.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_widget_task);

        views.setTextViewText(R.id.text1, task.getText());

        int priorityIcon;
        switch (task.getPriority()) {
            case Task.PRIORITY_HIGH:
                priorityIcon = R.drawable.ic_priority_high;
                break;
            case Task.PRIORITY_MEDIUM:
                priorityIcon = R.drawable.ic_priority_medium;
                break;
            case Task.PRIORITY_LOW:
                priorityIcon = R.drawable.ic_priority_low;
                break;
            default:
                priorityIcon = R.drawable.ic_priority_none;
                break;
        }
        views.setImageViewResource(R.id.priority_indicator, priorityIcon);

        if (!showItemBg) {
            views.setInt(R.id.item_widget_root, "setBackgroundColor", Color.TRANSPARENT);
        } else {
            views.setInt(R.id.item_widget_root, "setBackgroundColor", 0xCC121218);
        }

        if (task.isShowCheckboxInWidget()) {
            int checkboxDrawable = task.isCompleted()
                ? R.drawable.ic_checkbox_checked
                : R.drawable.ic_checkbox_unchecked;
            views.setImageViewResource(R.id.task_checkbox, checkboxDrawable);
            views.setViewVisibility(R.id.task_checkbox, android.view.View.VISIBLE);

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("taskId", task.getId());
            fillInIntent.putExtra("taskPosition", position);
            views.setOnClickFillInIntent(R.id.task_checkbox, fillInIntent);
        } else {
            views.setViewVisibility(R.id.task_checkbox, android.view.View.GONE);
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
