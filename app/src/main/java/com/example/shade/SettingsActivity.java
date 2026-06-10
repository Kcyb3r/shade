package com.example.shade;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.checkbox.MaterialCheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.shade.widget.ShadeWidgetProvider;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private WidgetPreferences widgetPreferences;
    private TextInputEditText emptyMessageInput;
    private MaterialCheckBox dividersCheckbox;
    private MaterialCheckBox itemBgCheckbox;
    private RadioGroup backgroundGroup;
    private String selectedBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        widgetPreferences = new WidgetPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        emptyMessageInput = findViewById(R.id.emptyMessageInput);
        dividersCheckbox = findViewById(R.id.dividersCheckbox);
        itemBgCheckbox = findViewById(R.id.itemBgCheckbox);
        backgroundGroup = findViewById(R.id.backgroundGroup);

        emptyMessageInput.setText(widgetPreferences.getEmptyMessage());
        dividersCheckbox.setChecked(widgetPreferences.getShowDividers());
        itemBgCheckbox.setChecked(widgetPreferences.getShowItemBg());
        selectedBackground = widgetPreferences.getWidgetBackground();

        setupBackgroundRadioButtons();

        findViewById(R.id.saveButton).setOnClickListener(v -> saveSettings());
        findViewById(R.id.resetButton).setOnClickListener(v -> resetSettings());
    }

    private void setupBackgroundRadioButtons() {
        Map<String, String> options = WidgetPreferences.getBackgroundOptions();

        for (Map.Entry<String, String> entry : options.entrySet()) {
            String key = entry.getKey();
            String label = entry.getValue();

            MaterialRadioButton rb = new MaterialRadioButton(this);
            rb.setId(View.generateViewId());
            rb.setText(label);
            rb.setTextColor(ContextCompat.getColor(this, R.color.white));
            rb.setTextSize(15f);
            rb.setButtonTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.accent)));

            rb.setTag(key);
            backgroundGroup.addView(rb);

            if (key.equals(selectedBackground)) {
                rb.setChecked(true);
            }
        }
    }

    private void saveSettings() {
        String message = emptyMessageInput.getText().toString().trim();
        boolean showDividers = dividersCheckbox.isChecked();
        boolean showItemBg = itemBgCheckbox.isChecked();
        if (!message.isEmpty()) widgetPreferences.setEmptyMessage(message);
        widgetPreferences.setShowDividers(showDividers);
        widgetPreferences.setShowItemBg(showItemBg);

        int checkedId = backgroundGroup.getCheckedRadioButtonId();
        if (checkedId != -1) {
            MaterialRadioButton checked = findViewById(checkedId);
            selectedBackground = (String) checked.getTag();
            widgetPreferences.setWidgetBackground(selectedBackground);
        }

        updateWidget();
        Snackbar.make(findViewById(R.id.saveButton), "Settings saved", Snackbar.LENGTH_SHORT)
                .setTextColor(getColor(R.color.white))
                .show();
        finish();
    }

    private void resetSettings() {
        widgetPreferences.resetToDefaults();
        emptyMessageInput.setText(widgetPreferences.getEmptyMessage());
        dividersCheckbox.setChecked(true);
        itemBgCheckbox.setChecked(true);
        selectedBackground = widgetPreferences.getWidgetBackground();
        for (int i = 0; i < backgroundGroup.getChildCount(); i++) {
            MaterialRadioButton rb = (MaterialRadioButton) backgroundGroup.getChildAt(i);
            rb.setChecked(rb.getTag().equals(selectedBackground));
        }
        updateWidget();
        Snackbar.make(findViewById(R.id.saveButton), "Reset to default", Snackbar.LENGTH_SHORT)
                .setTextColor(getColor(R.color.white))
                .show();
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
