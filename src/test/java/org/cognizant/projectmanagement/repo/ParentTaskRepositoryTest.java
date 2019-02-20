package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.ParentTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ParentTaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParentTaskRepository parentTaskRepository;

    @Test
    public void testSaveParentTask() {
        ParentTask savedParentTask = parentTaskRepository.save(new ParentTask());
        assertNotNull(savedParentTask);
        assertTrue(savedParentTask.getParentId() > 0);
    }

    @Test
    public void testUpdateParentTask() {
        ParentTask parentTask = createParentTask();
        parentTask.setParentTask("Test Parent Task");

        ParentTask savedParentTask = parentTaskRepository.save(parentTask);
        assertNotNull(savedParentTask);
        assertEquals("Test Parent Task", savedParentTask.getParentTask());
    }

    @Test
    public void testGetParentTask() {
        ParentTask project = createParentTask();
        Optional<ParentTask> savedParentTask = parentTaskRepository.findById(project.getParentId());
        assertTrue(savedParentTask.isPresent());
        assertEquals("Test", savedParentTask.get().getParentTask());
    }

    @Test
    public void testDeleteParentTask() {
        ParentTask parentTask = createParentTask();
        parentTaskRepository.delete(parentTask);
        assertFalse(parentTaskRepository.findById(parentTask.getParentId()).isPresent());
    }

    private ParentTask createParentTask() {
        ParentTask parentTask = new ParentTask();
        parentTask.setParentTask("Test");
        entityManager.persist(parentTask);
        entityManager.flush();
        return parentTask;
    }
}
