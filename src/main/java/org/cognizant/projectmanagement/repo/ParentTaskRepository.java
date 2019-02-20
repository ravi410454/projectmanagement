package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.ParentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentTaskRepository extends JpaRepository<ParentTask, Long> {
}
