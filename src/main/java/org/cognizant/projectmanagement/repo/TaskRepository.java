package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
