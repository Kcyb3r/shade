package com.example.shade;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shade.widget.ShadeWidgetProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private TaskRepository repository;
    private TaskAdapter adapter;
    private TextView taskCountText;
    private View emptyState;
    private RecyclerView recyclerView;
    private WidgetPreferences widgetPreferences;
    private boolean sortByPriority = false;
    private boolean filterDueToday = false;
    private FloatingActionButton fab;
    private float dX, dY;
    private float downRawX, downRawY;
    private boolean fabDragged;
    private static final float CLICK_DRAG_TOLERANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new TaskRepository(this);
        widgetPreferences = new WidgetPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shade");
        }

        recyclerView = findViewById(R.id.recyclerView);
        taskCountText = findViewById(R.id.taskCountText);
        emptyState = findViewById(R.id.emptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper dragDropHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.moveTask(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.setAlpha(0.8f);
                    viewHolder.itemView.setScaleX(1.05f);
                    viewHolder.itemView.setScaleY(1.05f);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);
                viewHolder.itemView.setScaleX(1.0f);
                viewHolder.itemView.setScaleY(1.0f);
                repository.updateTaskSequences(repository.getTasks());
                loadTasks();
                updateWidget();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }
        });
        dragDropHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private ColorDrawable background = new ColorDrawable(Color.parseColor("#FF5E7D"));
            private Drawable deleteIcon = ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_delete);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<Task> tasks = repository.getTasks();
                if (position >= 0 && position < tasks.size()) {
                    Task deletedTask = tasks.get(position);
                    repository.removeTask(deletedTask.getId());
                    loadTasks();
                    updateWidget();

                    Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                repository.addTask(deletedTask);
                                loadTasks();
                                updateWidget();
                            })
                            .setActionTextColor(getColor(R.color.white))
                            .setTextColor(getColor(R.color.white))
                            .show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                if (dX < 0) {
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(
                            itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(c);
                    deleteIcon.draw(c);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(task -> showEditTaskDialog(task));
        adapter.setOnItemLongClickListener(task -> showDeleteConfirmDialog(task));

        adapter.setOnCheckboxClickListener(task -> {
            repository.toggleTaskCompleted(task.getId());
            loadTasks();
            updateWidget();
        });

        adapter.setOnDeleteClickListener(task -> showDeleteConfirmDialog(task));

        adapter.setOnToggleCheckboxVisibilityListener(task -> {
            repository.toggleShowCheckboxInWidget(task.getId());
            loadTasks();
            updateWidget();
            String message = task.isShowCheckboxInWidget()
                ? "Checkbox hidden in widget"
                : "Checkbox shown in widget";
            Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                    .setTextColor(getColor(R.color.white))
                    .show();
        });

        adapter.setOnTaskMovedListener((fromPosition, toPosition) -> {
            repository.moveTask(fromPosition, toPosition);
            loadTasks();
            updateWidget();
        });

        adapter.setOnSequenceClickListener((task, position) -> showEditSequenceDialog(task));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
        setupDraggableFab();

        loadTasks();
    }

    private void setupDraggableFab() {
        fab.setOnTouchListener((view, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    downRawX = event.getRawX();
                    downRawY = event.getRawY();
                    fabDragged = false;
                    return true;

                case MotionEvent.ACTION_MOVE: {
                    float dx = event.getRawX() - downRawX;
                    float dy = event.getRawY() - downRawY;
                    if (Math.abs(dx) + Math.abs(dy) > CLICK_DRAG_TOLERANCE) {
                        fabDragged = true;
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        int viewWidth = view.getWidth();
                        int viewHeight = view.getHeight();
                        int parentWidth = ((View) view.getParent()).getWidth();
                        int parentHeight = ((View) view.getParent()).getHeight();

                        newX = Math.max(0, Math.min(newX, parentWidth - viewWidth));
                        newY = Math.max(0, Math.min(newY, parentHeight - viewHeight));

                        view.setX(newX);
                        view.setY(newY);
                        view.setPressed(false);
                    }
                    return true;
                }

                case MotionEvent.ACTION_UP:
                    if (!fabDragged) {
                        view.performClick();
                    }
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
        updateWidget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_filter_due_today) {
            filterDueToday = !filterDueToday;
            item.setChecked(filterDueToday);
            if (filterDueToday) sortByPriority = false;
            loadTasks();
            String msg = filterDueToday ? "Showing tasks due today" : "Showing all tasks";
            Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT)
                    .setTextColor(getColor(R.color.white))
                    .show();
            return true;
        } else if (item.getItemId() == R.id.action_sort_priority) {
            sortByPriority = !sortByPriority;
            item.setChecked(sortByPriority);
            if (sortByPriority) filterDueToday = false;
            loadTasks();
            String msg = sortByPriority ? "Sorting by priority" : "Sorting by sequence";
            Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT)
                    .setTextColor(getColor(R.color.white))
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTasks() {
        List<Task> tasks;
        if (sortByPriority) {
            tasks = repository.getTasksSortedByPriority();
        } else {
            tasks = repository.getTasks();
        }
        if (filterDueToday) {
            List<Task> filtered = new java.util.ArrayList<>();
            Calendar today = Calendar.getInstance();
            for (Task t : tasks) {
                if (t.hasDueDate()) {
                    Calendar due = Calendar.getInstance();
                    due.setTimeInMillis(t.getDueDate());
                    if (today.get(Calendar.YEAR) == due.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR)) {
                        filtered.add(t);
                    }
                }
            }
            tasks = filtered;
        }
        adapter.setTasks(tasks);
        updateTaskCount(tasks);
        updateEmptyState(tasks.isEmpty());
    }

    private void updateTaskCount(List<Task> tasks) {
        int totalTasks = tasks.size();
        int completedTasks = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }
        String countText;
        if (totalTasks == 0) {
            countText = "No tasks";
        } else if (completedTasks == totalTasks) {
            countText = "All tasks completed!";
        } else {
            countText = completedTasks + " of " + totalTasks + " completed";
        }
        taskCountText.setText(countText);
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmDialog(Task task) {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.removeTask(task.getId());
                    loadTasks();
                    updateWidget();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditSequenceDialog(Task task) {
        int totalTasks = repository.getTasks().size();
        int padding = (int) (20 * getResources().getDisplayMetrics().density);

        com.google.android.material.textfield.TextInputLayout til = new com.google.android.material.textfield.TextInputLayout(this);
        til.setHint("Position (1-" + totalTasks + ")");
        til.setBoxBackgroundMode(com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE);
        til.setBoxStrokeColor(getColor(R.color.accent));
        til.setBoxStrokeWidth(1);
        til.setHintTextColor(android.content.res.ColorStateList.valueOf(getColor(R.color.accent_light)));

        final com.google.android.material.textfield.TextInputEditText input = new com.google.android.material.textfield.TextInputEditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(task.getSequenceNumber()));
        input.setSelection(input.getText().length());
        input.setTextColor(getColor(R.color.white));
        input.setTextSize(18f);
        input.setGravity(android.view.Gravity.CENTER);
        til.addView(input);

        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Edit Sequence Number")
                .setMessage("Current position: " + task.getSequenceNumber())
                .setView(til)
                .setPositiveButton("Update", (dialog, which) -> {
                    String sequenceText = input.getText().toString().trim();
                    if (!sequenceText.isEmpty()) {
                        try {
                            int newSequence = Integer.parseInt(sequenceText);
                            if (newSequence >= 1 && newSequence <= totalTasks) {
                                repository.updateTaskSequence(task.getId(), newSequence);
                                loadTasks();
                                updateWidget();
                                Snackbar.make(recyclerView, "Task moved to position " + newSequence, Snackbar.LENGTH_SHORT)
                                        .setTextColor(getColor(R.color.white)).show();
                            } else {
                                Snackbar.make(recyclerView, "Invalid position! Enter 1-" + totalTasks, Snackbar.LENGTH_SHORT)
                                        .setTextColor(getColor(R.color.white))
                                        .setBackgroundTint(getColor(R.color.gray))
                                        .show();
                            }
                        } catch (NumberFormatException e) {
                            Snackbar.make(recyclerView, "Enter a valid number", Snackbar.LENGTH_SHORT)
                                    .setTextColor(getColor(R.color.white))
                                    .setBackgroundTint(getColor(R.color.gray))
                                    .show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditTaskDialog(Task task) {
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);

        com.google.android.material.textfield.TextInputEditText input = view.findViewById(R.id.taskInput);
        RadioGroup priorityGroup = view.findViewById(R.id.priorityGroup);
        com.google.android.material.button.MaterialButton dueDateButton = view.findViewById(R.id.dueDateButton);
        com.google.android.material.button.MaterialButton clearDueDateButton = view.findViewById(R.id.clearDueDateButton);

        input.setText(task.getText());
        input.setSelection(task.getText().length());

        int priorityId;
        switch (task.getPriority()) {
            case Task.PRIORITY_HIGH: priorityId = R.id.priorityHigh; break;
            case Task.PRIORITY_MEDIUM: priorityId = R.id.priorityMedium; break;
            case Task.PRIORITY_LOW: priorityId = R.id.priorityLow; break;
            default: priorityId = R.id.priorityNone;
        }
        priorityGroup.check(priorityId);

        final long[] selectedDueDate = {task.getDueDate()};

        if (task.hasDueDate()) {
            dueDateButton.setText(new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(new java.util.Date(task.getDueDate())));
            clearDueDateButton.setVisibility(View.VISIBLE);
        }

        dueDateButton.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            if (selectedDueDate[0] > 0) cal.setTimeInMillis(selectedDueDate[0]);
            android.app.DatePickerDialog dpd = new android.app.DatePickerDialog(this,
                    (dv, year, month, day) -> {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.set(year, month, day, 23, 59, 0);
                        selectedDueDate[0] = c.getTimeInMillis();
                        dueDateButton.setText(new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(new java.util.Date(selectedDueDate[0])));
                        clearDueDateButton.setVisibility(View.VISIBLE);
                    },
                    cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        clearDueDateButton.setOnClickListener(v -> {
            selectedDueDate[0] = 0;
            dueDateButton.setText("Set date");
            clearDueDateButton.setVisibility(View.GONE);
        });

        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Edit Task")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String taskText = input.getText().toString().trim();
                    if (!taskText.isEmpty()) {
                        int selectedPriority;
                        int id = priorityGroup.getCheckedRadioButtonId();
                        if (id == R.id.priorityHigh) selectedPriority = Task.PRIORITY_HIGH;
                        else if (id == R.id.priorityMedium) selectedPriority = Task.PRIORITY_MEDIUM;
                        else if (id == R.id.priorityLow) selectedPriority = Task.PRIORITY_LOW;
                        else selectedPriority = Task.PRIORITY_NONE;

                        repository.updateTask(task.getId(), taskText);
                        repository.updateTaskPriority(task.getId(), selectedPriority);
                        repository.updateTaskDueDate(task.getId(), selectedDueDate[0]);
                        loadTasks();
                        updateWidget();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateWidget() {
        Intent intent = new Intent(this, ShadeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(
                new ComponentName(getApplication(), ShadeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
