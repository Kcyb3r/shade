package com.example.shade;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class WidgetPreferences {
    private static final String PREF_NAME = "widget_preferences";
    private static final String KEY_EMPTY_MESSAGE = "empty_message";
    private static final String KEY_SHOW_DIVIDERS = "show_dividers";
    private static final String KEY_SHOW_ITEM_BG = "show_item_bg";
    private static final String KEY_WIDGET_BACKGROUND = "widget_background";
    private static final String KEY_AMOLED_FRAME = "amoled_frame";
    private static final int AMOLED_FRAME_COUNT = 6;
    private static final String DEFAULT_EMPTY_MESSAGE = "No tasks yet in the shade";
    private static final String DEFAULT_BACKGROUND = "teal";

    private final SharedPreferences preferences;

    public WidgetPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setEmptyMessage(String message) {
        preferences.edit().putString(KEY_EMPTY_MESSAGE, message).apply();
    }

    public String getEmptyMessage() {
        return preferences.getString(KEY_EMPTY_MESSAGE, DEFAULT_EMPTY_MESSAGE);
    }

    public void setShowDividers(boolean showDividers) {
        preferences.edit().putBoolean(KEY_SHOW_DIVIDERS, showDividers).apply();
    }

    public boolean getShowDividers() {
        return preferences.getBoolean(KEY_SHOW_DIVIDERS, true);
    }

    public void setShowItemBg(boolean show) {
        preferences.edit().putBoolean(KEY_SHOW_ITEM_BG, show).apply();
    }

    public boolean getShowItemBg() {
        return preferences.getBoolean(KEY_SHOW_ITEM_BG, true);
    }

    public void setWidgetBackground(String preset) {
        preferences.edit().putString(KEY_WIDGET_BACKGROUND, preset).apply();
    }

    public String getWidgetBackground() {
        return preferences.getString(KEY_WIDGET_BACKGROUND, DEFAULT_BACKGROUND);
    }

    public int getAmoledFrame() {
        return preferences.getInt(KEY_AMOLED_FRAME, 0);
    }

    public void setAmoledFrame(int frame) {
        preferences.edit().putInt(KEY_AMOLED_FRAME, frame % AMOLED_FRAME_COUNT).apply();
    }

    public void incrementAmoledFrame() {
        int next = (getAmoledFrame() + 1) % AMOLED_FRAME_COUNT;
        setAmoledFrame(next);
    }

    public static int getAmoledFrameDrawable(int frame) {
        switch (frame % AMOLED_FRAME_COUNT) {
            case 0: return R.drawable.widget_bg_amoled_01;
            case 1: return R.drawable.widget_bg_amoled_02;
            case 2: return R.drawable.widget_bg_amoled_03;
            case 3: return R.drawable.widget_bg_amoled_04;
            case 4: return R.drawable.widget_bg_amoled_05;
            case 5: return R.drawable.widget_bg_amoled_06;
            default: return R.drawable.widget_bg_amoled_01;
        }
    }

    public static Map<String, String> getBackgroundOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("auto", "Auto");
        options.put("teal", "Teal");
        options.put("navy", "Navy");
        options.put("purple", "Purple");
        options.put("dark", "Dark");
        options.put("live", "Live");
        options.put("amoled_bloom", "Amoled Bloom");
        options.put("transparent", "Transparent");
        return options;
    }

    public static int getBackgroundDrawableResId(String preset) {
        switch (preset) {
            case "auto":
            case "teal": return R.drawable.widget_bg_teal;
            case "navy": return R.drawable.widget_bg_navy;
            case "purple": return R.drawable.widget_bg_purple;
            case "dark": return R.drawable.widget_bg_dark;
            case "live": return R.drawable.widget_bg_live;
            case "amoled_bloom": return R.drawable.widget_bg_amoled_01;
            default: return 0;
        }
    }

    public static String resolveAutoPreset() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 12) return "teal";
        if (hour >= 12 && hour < 18) return "navy";
        if (hour >= 18 && hour < 22) return "purple";
        return "dark";
    }

    public static int getBackgroundColorRes(String preset) {
        switch (preset) {
            case "auto": return getBackgroundColorRes(resolveAutoPreset());
            case "teal": return R.color.accent_dark;
            case "navy": return R.color.dark_gray;
            case "purple": return R.color.gray;
            case "live": return R.color.accent;
            case "amoled_bloom": return R.color.dark_gray;
            case "dark": return R.color.dark_gray;
            default: return android.R.color.transparent;
        }
    }

    public void resetToDefaults() {
        preferences.edit()
                .putString(KEY_EMPTY_MESSAGE, DEFAULT_EMPTY_MESSAGE)
                .putBoolean(KEY_SHOW_DIVIDERS, true)
                .putBoolean(KEY_SHOW_ITEM_BG, true)
                .putString(KEY_WIDGET_BACKGROUND, DEFAULT_BACKGROUND)
                .apply();
    }
}
