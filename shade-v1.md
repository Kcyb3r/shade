# Shade v1.0 — Initial Release

Minimal, dark-themed Android task manager with a feature-rich home screen widget.

## Features

### Task Management
- Add, edit, delete, and reorder tasks via drag-and-drop
- Swipe-to-delete with undo snackbar
- Completion checkbox per task
- Sequence numbers auto-update on reorder

### Priority System
- 4 levels: None, Low, Medium, High
- Color-coded indicators (red / amber / teal / gray)
- Sort by priority descending

### Due Dates
- Inline date picker on add/edit forms
- Smart labels ("Due today", "Due tomorrow")
- Overdue highlighting
- "Due today" quick filter

### Home Screen Widget
- Live task list synced with app data
- Customizable empty-state message
- Divider toggle
- 7 background presets:
  - **Auto** — time-aware (teal morning, navy afternoon, purple evening, dark night)
  - **Teal**, **Navy**, **Purple**, **Dark**, **Live** (gradient), **Transparent** (wallpaper)
- Toggle item background on/off

### Interface
- Draggable FAB — move anywhere, short-tap still works
- Full-screen Add Task and Settings pages
- Cool-toned dark theme (teal accent #00B4D8, Material 3)

## Tech
- Java, Material 3, SharedPreferences + Gson
- Min SDK 24, Target SDK 34

---

**Full changelog**: https://github.com/Kcyb3r/shade/commits/master
