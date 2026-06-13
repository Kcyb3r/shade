# Release v1.1 — Amoled Bloom & Widget Polish

## Priority Color Indicators in Widget
- Each task in the widget now shows a colored priority icon (cyan/low, orange/medium, red/high) matching the main app style
- Priority indicator uses the same triangle icon set as the task list for visual consistency

## Due-Date Notifications
- Tasks with a due date trigger a notification at 8:00 AM on the due date
- Notification includes the task text and opens the app on tap
- Permission request for Android 13+ (`POST_NOTIFICATIONS`)
- Notification auto-scheduled on task save; cancelled on delete or edit

## Fixes
- Widget no longer shows persistent "Loading…" — switched priority indicator from `View.setBackgroundColor` (incompatible with RemoteViews on some API levels) to `ImageView.setImageResource` with the priority drawable
- "Clear" button text size increased to `14sp` in Add Task, Edit, and quick-add dialogs — no longer clipped on lower-density screens

## Amoled Bloom Widget Background
- New animated widget background mode: 6 gradient frames cycling every 5 seconds via `AlarmManager`
- Deep AMOLED black base with shifting neon blooms (cyan, magenta, purple, blue, gold)
- Frame colors rotate through different emphasis patterns — creates a slow aurora/plasma effect
- Animation auto-starts when Amoled Bloom is selected; auto-stops when switching presets or removing all widgets

## Settings
- App version displayed at the bottom of Settings (`v1.1 (2)`)
- "Amoled Bloom" available as a selectable widget background radio option

## Internal
- Version bumped to 1.1 (versionCode 2)
- New: `AmoledBloomManager`, `AmoledBloomReceiver`, 6 gradient frame drawables
- Widget background selection uses `resizeMode="horizontal|vertical"` for custom sizing
