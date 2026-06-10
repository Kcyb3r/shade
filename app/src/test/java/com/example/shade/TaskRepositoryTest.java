package com.example.shade;

import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class TaskRepositoryTest {

    private TaskRepository repository;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        repository = new TaskRepository(context);
        // Clear any existing tasks
        for (Task task : repository.getTasks()) {
            repository.removeTask(task.getId());
        }
    }

    @Test
    public void testGetTasks_emptyInitially() {
        List<Task> tasks = repository.getTasks();

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testAddTask() {
        Task task = new Task("1", "Test task", System.currentTimeMillis());

        repository.addTask(task);
        List<Task> tasks = repository.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("Test task", tasks.get(0).getText());
        assertEquals("1", tasks.get(0).getId());
    }

    @Test
    public void testAddMultipleTasks() {
        repository.addTask(new Task("1", "Task 1", 1000L));
        repository.addTask(new Task("2", "Task 2", 2000L));
        repository.addTask(new Task("3", "Task 3", 3000L));

        List<Task> tasks = repository.getTasks();

        assertEquals(3, tasks.size());
    }

    @Test
    public void testRemoveTask() {
        repository.addTask(new Task("1", "Task 1", 1000L));
        repository.addTask(new Task("2", "Task 2", 2000L));

        repository.removeTask("1");
        List<Task> tasks = repository.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("2", tasks.get(0).getId());
    }

    @Test
    public void testRemoveNonexistentTask() {
        repository.addTask(new Task("1", "Task 1", 1000L));

        repository.removeTask("nonexistent");
        List<Task> tasks = repository.getTasks();

        assertEquals(1, tasks.size());
    }

    @Test
    public void testUpdateTask() {
        repository.addTask(new Task("1", "Original text", 1000L));

        repository.updateTask("1", "Updated text");
        List<Task> tasks = repository.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("Updated text", tasks.get(0).getText());
    }

    @Test
    public void testUpdateNonexistentTask() {
        repository.addTask(new Task("1", "Task 1", 1000L));

        repository.updateTask("nonexistent", "New text");
        List<Task> tasks = repository.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getText());
    }

    @Test
    public void testPersistence() {
        repository.addTask(new Task("1", "Persistent task", 1000L));

        // Create new repository instance to test persistence
        TaskRepository newRepository = new TaskRepository(context);
        List<Task> tasks = newRepository.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("Persistent task", tasks.get(0).getText());
    }
}
