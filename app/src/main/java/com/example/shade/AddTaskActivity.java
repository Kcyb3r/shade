package com.example.shade;

import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.shade.widget.ShadeWidgetProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {

    private TaskRepository repository;
    private TextInputEditText taskInput;
    private RadioGroup priorityGroup;
    private MaterialButton dueDateButton;
    private MaterialButton clearDueDateButton;
    private Button saveButton;
    private TextView taskCountHeader;
    private long selectedDueDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        repository = new TaskRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Task");
        }

        taskCountHeader = findViewById(R.id.taskCountHeader);
        taskInput = findViewById(R.id.taskInput);
        priorityGroup = findViewById(R.id.priorityGroup);
        dueDateButton = findViewById(R.id.dueDateButton);
        clearDueDateButton = findViewById(R.id.clearDueDateButton);
        saveButton = findViewById(R.id.saveButton);

        List<Task> tasks = repository.getTasks();
        int count = tasks.size();
        taskCountHeader.setText("Total tasks: " + count + "  |  New task #" + (count + 1));

        dueDateButton.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (selectedDueDate > 0) cal.setTimeInMillis(selectedDueDate);
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (dv, year, month, day) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day, 23, 59, 0);
                        selectedDueDate = c.getTimeInMillis();
                        dueDateButton.setText(new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date(selectedDueDate)));
                        clearDueDateButton.setVisibility(View.VISIBLE);
                    },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        clearDueDateButton.setOnClickListener(v -> {
            selectedDueDate = 0;
            dueDateButton.setText("Set date");
            clearDueDateButton.setVisibility(View.GONE);
        });

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String taskText = taskInput.getText().toString().trim();
        if (taskText.isEmpty()) {
            Snackbar.make(taskInput, "Please enter a task", Snackbar.LENGTH_SHORT)
                    .setTextColor(getColor(R.color.white))
                    .show();
            return;
        }

        int selectedPriority;
        int id = priorityGroup.getCheckedRadioButtonId();
        if (id == R.id.priorityHigh) selectedPriority = Task.PRIORITY_HIGH;
        else if (id == R.id.priorityMedium) selectedPriority = Task.PRIORITY_MEDIUM;
        else if (id == R.id.priorityLow) selectedPriority = Task.PRIORITY_LOW;
        else selectedPriority = Task.PRIORITY_NONE;

        Task newTask = new Task(UUID.randomUUID().toString(), taskText, System.currentTimeMillis());
        newTask.setPriority(selectedPriority);
        newTask.setDueDate(selectedDueDate);
        repository.addTask(newTask);

        updateWidget();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
