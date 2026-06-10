package com.example.shade.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shade.R;

public class WidgetConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "shade_widget_prefs";
    private static final String PREF_PREFIX_COLOR = "widget_color_";
    private static final String PREF_PREFIX_TRANSPARENT = "widget_transparent_";

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SeekBar seekBarRed, seekBarGreen, seekBarBlue, seekBarAlpha;
    private CheckBox checkBoxTransparent;
    private View colorPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget_config);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        seekBarRed = findViewById(R.id.seekbar_red);
        seekBarGreen = findViewById(R.id.seekbar_green);
        seekBarBlue = findViewById(R.id.seekbar_blue);
        seekBarAlpha = findViewById(R.id.seekbar_alpha);
        checkBoxTransparent = findViewById(R.id.checkbox_transparent);
        colorPreview = findViewById(R.id.color_preview);
        Button btnSave = findViewById(R.id.btn_save);

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    checkBoxTransparent.setChecked(false);
                }
                updateColorPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekBarRed.setOnSeekBarChangeListener(seekBarListener);
        seekBarGreen.setOnSeekBarChangeListener(seekBarListener);
        seekBarBlue.setOnSeekBarChangeListener(seekBarListener);
        seekBarAlpha.setOnSeekBarChangeListener(seekBarListener);

        checkBoxTransparent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            seekBarRed.setEnabled(!isChecked);
            seekBarGreen.setEnabled(!isChecked);
            seekBarBlue.setEnabled(!isChecked);
            seekBarAlpha.setEnabled(!isChecked);
            updateColorPreview();
        });

        btnSave.setOnClickListener(v -> saveAndFinish());

        updateColorPreview();
        seekBarRed.setEnabled(false);
        seekBarGreen.setEnabled(false);
        seekBarBlue.setEnabled(false);
        seekBarAlpha.setEnabled(false);
    }

    private void updateColorPreview() {
        if (checkBoxTransparent.isChecked()) {
            colorPreview.setBackground(new ColorDrawable(Color.TRANSPARENT));
        } else {
            int color = Color.argb(
                    seekBarAlpha.getProgress(),
                    seekBarRed.getProgress(),
                    seekBarGreen.getProgress(),
                    seekBarBlue.getProgress()
            );
            colorPreview.setBackground(new ColorDrawable(color));
        }
    }

    private void saveAndFinish() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean isTransparent = checkBoxTransparent.isChecked();
        editor.putBoolean(PREF_PREFIX_TRANSPARENT + appWidgetId, isTransparent);

        if (!isTransparent) {
            int color = Color.argb(
                    seekBarAlpha.getProgress(),
                    seekBarRed.getProgress(),
                    seekBarGreen.getProgress(),
                    seekBarBlue.getProgress()
            );
            editor.putInt(PREF_PREFIX_COLOR + appWidgetId, color);
        }
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ShadeWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public static int getWidgetColor(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isTransparent = prefs.getBoolean(PREF_PREFIX_TRANSPARENT + appWidgetId, true);
        if (isTransparent) {
            return Color.TRANSPARENT;
        }
        return prefs.getInt(PREF_PREFIX_COLOR + appWidgetId, Color.TRANSPARENT);
    }

    public static void deleteWidgetPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .remove(PREF_PREFIX_COLOR + appWidgetId)
                .remove(PREF_PREFIX_TRANSPARENT + appWidgetId)
                .apply();
    }
}
