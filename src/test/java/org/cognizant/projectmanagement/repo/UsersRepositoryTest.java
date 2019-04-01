package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Users;
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
public class UsersRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void testSaveUsers() {
        entityManager.clear();
        Users savedUsers = usersRepository.save(new Users());
        assertNotNull(savedUsers);
        assertTrue(savedUsers.getUserId() > 0);
    }

    @Test
    public void testUpdateUsers() {
        Users users = createUsers();
        users.setFirstName("First");
        users.setLastName("Last");

        Users savedUsers = usersRepository.save(users);
        assertNotNull(savedUsers);
        assertEquals("First", savedUsers.getFirstName());
        assertEquals("Last", savedUsers.getLastName());
    }

    @Test
    public void testGetUsers() {
        Users users = createUsers();
        Optional<Users> savedUsers = usersRepository.findById(users.getEmployeeId());
        assertTrue(savedUsers.isPresent());
        assertEquals(Long.valueOf(1), savedUsers.get().getUserId());
    }

    @Test
    public void testDeleteUsers() {
        Users users = createUsers();
        usersRepository.delete(users);
        assertFalse(usersRepository.findById(users.getEmployeeId()).isPresent());
    }

    private Users createUsers() {
        entityManager.clear();
        Users users = new Users();
        users.setEmployeeId(1l);
        users.setFirstName("TestF");
        users.setLastName("TestL");
        users.setProjectId(1l);
        users.setTaskId(1l);
        entityManager.persist(users);
        entityManager.flush();
        return users;
    }
}
