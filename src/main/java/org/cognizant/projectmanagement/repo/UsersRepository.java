package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    List<Users> findByProjectId(Long projectId);

    List<Users> findByTaskId(Long taskId);
}
