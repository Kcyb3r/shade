package com.example.shade;

import org.junit.Test;
import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void testTaskCreation() {
        Task task = new Task("123", "Test task", 1000L);

        assertEquals("123", task.getId());
        assertEquals("Test task", task.getText());
        assertEquals(1000L, task.getTimestamp());
    }

    @Test
    public void testSetText() {
        Task task = new Task("123", "Original text", 1000L);

        task.setText("Updated text");

        assertEquals("Updated text", task.getText());
        assertEquals("123", task.getId());
        assertEquals(1000L, task.getTimestamp());
    }

    @Test
    public void testEmptyText() {
        Task task = new Task("id1", "", 0L);

        assertEquals("", task.getText());
    }
}
