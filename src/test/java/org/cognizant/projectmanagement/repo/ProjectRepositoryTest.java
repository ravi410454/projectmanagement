package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Project;
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
public class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testSaveProject() {
        Project savedProject = projectRepository.save(new Project());
        assertNotNull(savedProject);
        assertEquals(1, savedProject.getProjectId());
    }

    @Test
    public void testUpdateProject() {
        Project project = createProject();
        project.setPriority(2);
        project.setProject("Test Project");

        Project savedProject = projectRepository.save(project);
        assertNotNull(savedProject);
        assertEquals(2, savedProject.getPriority());
        assertEquals("Test Project", savedProject.getProject());
    }

    @Test
    public void testGetProject() {
        Project project = createProject();
        Optional<Project> savedProject = projectRepository.findById(project.getProjectId());
        assertTrue(savedProject.isPresent());
        assertEquals("Test", savedProject.get().getProject());
    }

    @Test
    public void testDeleteProject() {
        Project project = createProject();
        projectRepository.delete(project);
        assertFalse(projectRepository.findById(project.getProjectId()).isPresent());
    }

    private Project createProject() {
        Project project = new Project();
        project.setProject("Test");
        project.setPriority(1);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now());
        entityManager.persist(project);
        entityManager.flush();
        return project;
    }
}
