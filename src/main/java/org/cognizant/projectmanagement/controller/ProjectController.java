package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Project;
import org.cognizant.projectmanagement.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/project/{id}")
    @ResponseBody
    public Project getProject(@PathVariable long id) {
        Optional<Project> project = projectRepository.findById(id);

        if (!project.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        return project.get();
    }

    @PostMapping("/project/add")
    @ResponseBody
    public ResponseEntity<Object> addProject(@RequestBody Project project) {
        Project savedProject = projectRepository.save(project);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProject.getProjectId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/project/{id}")
    @ResponseBody
    public ResponseEntity<Object> editProject(@RequestBody Project project, @PathVariable long id) {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (!projectOptional.isPresent())
            return ResponseEntity.notFound().build();

        project.setProjectId(id);

        projectRepository.save(project);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/project/{id}")
    @ResponseBody
    public void deleteProject(@PathVariable long id) {
        projectRepository.deleteById(id);
    }
}
