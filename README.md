<div align="center">
  <img src="app_logo.jpeg" alt="Shade logo" width="90" style="border-radius: 18px;">
  <h1>Shade</h1>
  <p>
    <strong>Minimal · Dark · Cool-toned task management for Android</strong>
  </p>
  <p>
    <img src="https://img.shields.io/badge/API-24%2B-00B4D8?style=flat-square">
    <img src="https://img.shields.io/badge/License-MIT-00B4D8?style=flat-square">
    <img src="https://img.shields.io/badge/Material-3-00B4D8?style=flat-square">
  </p>
</div>

---

## Overview

Shade is a lightweight Android task manager built with **Material Design 3** and a cool-toned dark aesthetic. It combines a clean local-first task list with a feature-rich home screen widget that supports **7 background presets** — including Auto (time-aware), Live (gradient), and Transparent (wallpaper).

Built with zero backend dependency. No accounts, no servers, no syncing. Your tasks live in SharedPreferences as JSON.

---

## Features

### Task Management
| | |
|---|---|
| **Add tasks** | Full-screen activity with task text, priority, and optional due date. Shows total count + new task number. |
| **Edit tasks** | Dialog-based editing of any field. |
| **Delete tasks** | Swipe-to-dismiss with undo snackbar. Long-press for confirmation dialog. |
| **Reorder tasks** | Long-press drag-and-drop. Sequence numbers update automatically. |
| **Complete tasks** | Inline checkbox per task. |

### Priority System
- **Four levels** — None, Low, Medium, High
- **Color strip** — Red (High), amber (Medium), teal (Low), gray (None)
- **Triangle indicator** — Visual cue beside each task
- **Sort** — Sort by priority descending

### Due Dates
- **Date picker** — Inline in add/edit forms
- **Smart labels** — "Due today", "Due tomorrow", formatted date
- **Overdue highlighting** — Red text for past-due tasks
- **Filter** — "Due today" quick-filter in overflow menu

### Home Screen Widget
- **Live task list** — Mirrors your app data with checkbox toggle per task
- **Empty state** — Customizable message
- **Dividers** — Toggle on/off
- **7 background presets:**

| Preset | Description |
|--------|-------------|
| **Auto** | Time-aware: teal (morning), navy (afternoon), purple (evening), dark (night) |
| **Teal** | Cool-teal gradient |
| **Navy** | Deep navy gradient |
| **Purple** | Muted purple gradient |
| **Dark** | Near-black gradient |
| **Live** | Teal-to-purple diagonal sweep |
| **Transparent** | Fully transparent — shows device wallpaper |

  All configurable from **Settings** (overflow menu → Settings).

### Interface
- **Draggable FAB** — The + button can be dragged anywhere; short-tap still works
- **Dark theme** — Teal accent (`#00B4D8`), deep navy surfaces, Material 3
- **MaterialAlertDialog** — Themed dialogs throughout

---

## Screenshots

| Tasks | Add Task | Widget Variants |
|------|----------|----------------|
| *(screenshot)* | *(screenshot)* | *(screenshot)* |

---

## Tech Stack

| Component | Choice |
|-----------|--------|
| **Language** | Java |
| **UI** | Material Components (Material 3), RecyclerView, RemoteViews |
| **Persistence** | SharedPreferences + Gson serialization |
| **Architecture** | Activity-based with SharedPreferences repository |
| **Min / Target SDK** | 24 (Android 7.0) / 34 (Android 14) |
| **Build** | Gradle (Groovy) |

### Dependencies

```groovy
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0
com.google.code.gson:gson:2.10.1
```

---

## Building

```bash
git clone https://github.com/yourusername/shade.git
cd shade
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Project Structure

```
app/src/main/
├── java/com/example/shade/
│   ├── MainActivity.java           # Task list, drag FAB, widget updates
│   ├── AddTaskActivity.java        # Full-screen add-task page
│   ├── SettingsActivity.java       # Widget settings page
│   ├── Task.java                   # Data model
│   ├── TaskAdapter.java            # RecyclerView adapter
│   ├── TaskRepository.java         # SharedPreferences persistence
│   ├── WidgetPreferences.java      # Widget config storage
│   └── widget/
│       ├── ShadeWidgetProvider.java
│       ├── ShadeWidgetService.java
│       └── WidgetConfigActivity.java
├── res/
│   ├── layout/                     # XML layouts
│   ├── drawable/                   # Gradients, icons, priority indicators
│   ├── values/                     # Colors, themes, strings
│   ├── menu/                       # Overflow menu
│   └── xml/                        # Widget info
└── AndroidManifest.xml
```

---

## License

```
MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```
