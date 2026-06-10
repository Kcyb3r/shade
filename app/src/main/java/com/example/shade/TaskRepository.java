package com.example.shade;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskRepository {

    private static final String PREF_NAME = "shade_tasks";
    private static final String KEY_TASKS = "tasks_list";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public TaskRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Task> getTasks() {
        String json = sharedPreferences.getString(KEY_TASKS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(json, type);
        
        // Sort tasks by sequence number
        Collections.sort(tasks, (t1, t2) -> Integer.compare(t1.getSequenceNumber(), t2.getSequenceNumber()));
        
        return tasks;
    }

    public void addTask(Task task) {
        List<Task> tasks = getTasks();
        // Assign sequence number as the next available number
        int maxSequence = 0;
        for (Task t : tasks) {
            if (t.getSequenceNumber() > maxSequence) {
                maxSequence = t.getSequenceNumber();
            }
        }
        task.setSequenceNumber(maxSequence + 1);
        tasks.add(task);
        saveTasks(tasks);
    }

    public void removeTask(String taskId) {
        List<Task> tasks = getTasks();
        tasks.removeIf(t -> t.getId().equals(taskId));
        saveTasks(tasks);
    }

    public void updateTask(String taskId, String newText) {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setText(newText);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void updateTaskPriority(String taskId, int priority) {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setPriority(priority);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void updateTaskDueDate(String taskId, long dueDate) {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setDueDate(dueDate);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void toggleTaskCompleted(String taskId) {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setCompleted(!task.isCompleted());
                break;
            }
        }
        saveTasks(tasks);
    }

    public void toggleShowCheckboxInWidget(String taskId) {
        List<Task> tasks = getTasks();
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setShowCheckboxInWidget(!task.isShowCheckboxInWidget());
                break;
            }
        }
        saveTasks(tasks);
    }

    public void moveTask(int fromPosition, int toPosition) {
        List<Task> tasks = getTasks();
        if (fromPosition < 0 || fromPosition >= tasks.size() || 
            toPosition < 0 || toPosition >= tasks.size()) {
            return;
        }
        
        Task task = tasks.remove(fromPosition);
        tasks.add(toPosition, task);
        
        // Reassign sequence numbers
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setSequenceNumber(i + 1);
        }
        
        saveTasks(tasks);
    }

    public void updateTaskSequences(List<Task> reorderedTasks) {
        for (int i = 0; i < reorderedTasks.size(); i++) {
            reorderedTasks.get(i).setSequenceNumber(i + 1);
        }
        saveTasks(reorderedTasks);
    }

    public void updateTaskSequence(String taskId, int newSequence) {
        List<Task> tasks = getTasks();
        
        // Find the task to update
        Task targetTask = null;
        int oldPosition = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(taskId)) {
                targetTask = tasks.get(i);
                oldPosition = i;
                break;
            }
        }
        
        if (targetTask == null || oldPosition == -1) {
            return;
        }
        
        // Validate new sequence number
        if (newSequence < 1 || newSequence > tasks.size()) {
            return;
        }
        
        // Remove task from old position
        tasks.remove(oldPosition);
        
        // Insert at new position (newSequence - 1 because sequence is 1-based)
        tasks.add(newSequence - 1, targetTask);
        
        // Reassign all sequence numbers
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setSequenceNumber(i + 1);
        }
        
        saveTasks(tasks);
    }

    public List<Task> getTasksSortedByPriority() {
        List<Task> tasks = getTasks();
        Collections.sort(tasks, (t1, t2) -> {
            // Sort by priority descending (high first), then by sequence
            if (t1.getPriority() != t2.getPriority()) {
                return Integer.compare(t2.getPriority(), t1.getPriority());
            }
            return Integer.compare(t1.getSequenceNumber(), t2.getSequenceNumber());
        });
        return tasks;
    }

    private void saveTasks(List<Task> tasks) {
        String json = gson.toJson(tasks);
        sharedPreferences.edit().putString(KEY_TASKS, json).apply();
    }
}
