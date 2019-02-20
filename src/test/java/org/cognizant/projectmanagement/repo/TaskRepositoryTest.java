package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testSaveTask() {
        Task savedTask = taskRepository.save(new Task());
        assertNotNull(savedTask);
        assertTrue(savedTask.getTaskId() > 0);
    }

    @Test
    public void testUpdateTask() {
        Task task = createTask();

        task.setPriority(2);
        task.setStatus("Complete");

        Task savedTask = taskRepository.save(task);
        assertNotNull(savedTask);
        assertEquals(2, savedTask.getPriority());
        assertEquals("Complete", savedTask.getStatus());
    }

    @Test
    public void testGetTask() {
        Task task = createTask();
        Optional<Task> savedTask = taskRepository.findById(task.getTaskId());
        assertTrue(savedTask.isPresent());
        assertEquals("Test", savedTask.get().getTask());
    }

    @Test
    public void testDeleteTask() {
        Task task = createTask();
        taskRepository.delete(task);
        assertFalse(taskRepository.findById(task.getTaskId()).isPresent());
    }

    private Task createTask() {
        Task task = new Task();
        task.setTask("Test");
        task.setParentId(1);
        task.setPriority(1);
        task.setStatus("In Progress");
        task.setStartDate(LocalDate.now());
        task.setEndDate(LocalDate.now());
        entityManager.persist(task);
        entityManager.flush();
        return task;
    }
}
