package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Integer countDistinctTaskIdByProjectId(Long projectId);
}
