package com.example.shade;

public class Task {
    public static final int PRIORITY_NONE = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;

    private String id;
    private String text;
    private long timestamp;
    private boolean completed = false;
    private boolean showCheckboxInWidget = true;
    private int sequenceNumber = 0;
    private int priority = PRIORITY_NONE;
    private long dueDate = 0;

    public Task(String id, String text, long timestamp) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Task(String id, String text, long timestamp, boolean completed) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.completed = completed;
    }

    public Task(String id, String text, long timestamp, boolean completed, boolean showCheckboxInWidget) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.completed = completed;
        this.showCheckboxInWidget = showCheckboxInWidget;
    }

    public Task(String id, String text, long timestamp, boolean completed, boolean showCheckboxInWidget, int sequenceNumber) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.completed = completed;
        this.showCheckboxInWidget = showCheckboxInWidget;
        this.sequenceNumber = sequenceNumber;
    }

    public Task(String id, String text, long timestamp, boolean completed, boolean showCheckboxInWidget, int sequenceNumber, int priority) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.completed = completed;
        this.showCheckboxInWidget = showCheckboxInWidget;
        this.sequenceNumber = sequenceNumber;
        this.priority = priority;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public void setText(String text) { this.text = text; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isShowCheckboxInWidget() { return showCheckboxInWidget; }
    public void setShowCheckboxInWidget(boolean showCheckboxInWidget) { this.showCheckboxInWidget = showCheckboxInWidget; }
    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    public boolean hasDueDate() { return dueDate > 0; }
    public boolean isOverdue() { return dueDate > 0 && !completed && dueDate < System.currentTimeMillis(); }

    public static String priorityName(int priority) {
        switch (priority) {
            case PRIORITY_HIGH: return "High";
            case PRIORITY_MEDIUM: return "Medium";
            case PRIORITY_LOW: return "Low";
            default: return "None";
        }
    }
}
