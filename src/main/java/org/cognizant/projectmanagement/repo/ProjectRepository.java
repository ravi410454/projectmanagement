package org.cognizant.projectmanagement.repo;

import org.cognizant.projectmanagement.api.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
