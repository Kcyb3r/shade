package com.example.shade;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener;
    private OnCheckboxClickListener checkboxClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnToggleCheckboxVisibilityListener toggleCheckboxVisibilityListener;
    private OnTaskMovedListener taskMovedListener;
    private OnSequenceClickListener sequenceClickListener;

    public interface OnItemLongClickListener { void onItemLongClick(Task task); }
    public interface OnItemClickListener { void onItemClick(Task task); }
    public interface OnCheckboxClickListener { void onCheckboxClick(Task task); }
    public interface OnDeleteClickListener { void onDeleteClick(Task task); }
    public interface OnToggleCheckboxVisibilityListener { void onToggleCheckboxVisibility(Task task); }
    public interface OnTaskMovedListener { void onTaskMoved(int fromPosition, int toPosition); }
    public interface OnSequenceClickListener { void onSequenceClick(Task task, int currentPosition); }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) { this.longClickListener = listener; }
    public void setOnItemClickListener(OnItemClickListener listener) { this.clickListener = listener; }
    public void setOnCheckboxClickListener(OnCheckboxClickListener listener) { this.checkboxClickListener = listener; }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) { this.deleteClickListener = listener; }
    public void setOnToggleCheckboxVisibilityListener(OnToggleCheckboxVisibilityListener listener) { this.toggleCheckboxVisibilityListener = listener; }
    public void setOnTaskMovedListener(OnTaskMovedListener listener) { this.taskMovedListener = listener; }
    public void setOnSequenceClickListener(OnSequenceClickListener listener) { this.sequenceClickListener = listener; }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.textViewTask.setText(task.getText());

        holder.sequenceNumber.setText(String.valueOf(task.getSequenceNumber()));

        holder.sequenceNumber.setOnClickListener(v -> {
            if (sequenceClickListener != null) {
                sequenceClickListener.onSequenceClick(task, position);
            }
        });

        int checkboxDrawable = task.isCompleted()
            ? R.drawable.ic_checkbox_checked
            : R.drawable.ic_checkbox_unchecked;
        holder.checkboxImage.setImageResource(checkboxDrawable);

        if (task.isCompleted()) {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTask.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_completed));
        } else {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTask.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        holder.checkboxImage.setOnClickListener(v -> {
            animateCheckbox(holder.checkboxImage);
            if (checkboxClickListener != null) {
                checkboxClickListener.onCheckboxClick(task);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(task);
            }
        });

        holder.toggleCheckboxButton.setOnClickListener(v -> {
            if (toggleCheckboxVisibilityListener != null) {
                toggleCheckboxVisibilityListener.onToggleCheckboxVisibility(task);
            }
        });

        holder.toggleCheckboxButton.setAlpha(task.isShowCheckboxInWidget() ? 1.0f : 0.4f);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(task);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(task);
                return true;
            }
            return false;
        });

        // Priority indicator
        int priorityColor;
        int priorityIcon;
        switch (task.getPriority()) {
            case Task.PRIORITY_HIGH:
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_high);
                priorityIcon = R.drawable.ic_priority_high;
                break;
            case Task.PRIORITY_MEDIUM:
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_medium);
                priorityIcon = R.drawable.ic_priority_medium;
                break;
            case Task.PRIORITY_LOW:
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_low);
                priorityIcon = R.drawable.ic_priority_low;
                break;
            default:
                priorityColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.priority_none);
                priorityIcon = R.drawable.ic_priority_none;
                break;
        }
        holder.priorityStrip.setBackgroundColor(priorityColor);
        holder.priorityIndicator.setImageResource(priorityIcon);

        // Due date display
        if (task.hasDueDate()) {
            holder.dueDateText.setVisibility(View.VISIBLE);
            String dateStr = formatDueDate(holder, task);
            holder.dueDateText.setText(dateStr);

            if (task.isOverdue()) {
                holder.dueDateText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.overdue));
                holder.dueDateText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_priority_high, 0, 0, 0);
            } else if (isToday(task.getDueDate())) {
                holder.dueDateText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.due_today));
                holder.dueDateText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                holder.dueDateText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.due_future));
                holder.dueDateText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        } else {
            holder.dueDateText.setVisibility(View.GONE);
        }
    }

    private String formatDueDate(TaskViewHolder holder, Task task) {
        Date date = new Date(task.getDueDate());
        SimpleDateFormat sdf;
        Calendar cal = Calendar.getInstance();
        Calendar dueCal = Calendar.getInstance();
        dueCal.setTime(date);

        if (isToday(task.getDueDate())) {
            return "Due today";
        }

        cal.add(Calendar.DAY_OF_YEAR, 1);
        if (dueCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            dueCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
            return "Due tomorrow";
        }

        if (dueCal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            sdf = new SimpleDateFormat("MMM d", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        }
        return "Due " + sdf.format(date);
    }

    private boolean isToday(long millis) {
        Calendar today = Calendar.getInstance();
        Calendar other = Calendar.getInstance();
        other.setTimeInMillis(millis);
        return today.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR);
    }

    private void animateCheckbox(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
            1.0f, 0.8f, 1.0f, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(scaleAnimation);
    }

    public void moveTask(int fromPosition, int toPosition) {
        Task task = tasks.remove(fromPosition);
        tasks.add(toPosition, task);
        notifyItemMoved(fromPosition, toPosition);
        if (taskMovedListener != null) {
            taskMovedListener.onTaskMoved(fromPosition, toPosition);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        TextView dueDateText;
        TextView sequenceNumber;
        ImageView checkboxImage;
        ImageView deleteButton;
        ImageView toggleCheckboxButton;
        ImageView priorityIndicator;
        View priorityStrip;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            sequenceNumber = itemView.findViewById(R.id.sequenceNumber);
            checkboxImage = itemView.findViewById(R.id.taskCheckbox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            toggleCheckboxButton = itemView.findViewById(R.id.toggleCheckboxButton);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            priorityStrip = itemView.findViewById(R.id.priorityStrip);
        }
    }
}