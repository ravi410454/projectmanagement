package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.ParentTask;
import org.cognizant.projectmanagement.api.Project;
import org.cognizant.projectmanagement.api.Task;
import org.cognizant.projectmanagement.api.Users;
import org.cognizant.projectmanagement.repo.ParentTaskRepository;
import org.cognizant.projectmanagement.repo.ProjectRepository;
import org.cognizant.projectmanagement.repo.TaskRepository;
import org.cognizant.projectmanagement.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ParentTaskRepository parentTaskRepository;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/project/{id}")
    @ResponseBody
    public Project getProject(@PathVariable long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);

        if (!optionalProject.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        Project project = optionalProject.get();
        project.setTasks(taskRepository.findByProjectId(id));
        if (project.getTasks() != null) {
            project.getTasks().forEach(t -> {
                if (t.getParentId() > 0) {
                    Optional<ParentTask> parent = parentTaskRepository.findById(t.getParentId());
                    if (parent.isPresent()) {
                        t.setParent(parent.get().getParentTask());
                    }
                }
            });
        }
        return project;
    }

    @GetMapping("/project")
    @ResponseBody
    public List<Project> getProjects() {
        List<Project> projects = projectRepository.findAll();

        if (projects.isEmpty())
            throw new RuntimeException("Not found");

        projects.forEach(p -> {
            List<Task> tasks = taskRepository.findByProjectId(p.getProjectId());
            p.setTasks(tasks);
            p.setTaskCount(tasks.size());

            Integer completed = tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).collect(Collectors.toList()).size();
            p.setCompleted(completed);

            List<Users> users = usersRepository.findByProjectId(p.getProjectId());
            if (users != null && !users.isEmpty()) {
                p.setManagerId(users.get(0).getUserId());
            }
        });
        return projects;
    }

    @PostMapping("/project")
    @ResponseBody
    public ResponseEntity<Object> addProject(@RequestBody Project project) {
        Project savedProject = projectRepository.save(project);
        if (savedProject == null)
            return ResponseEntity.noContent().build();
        Optional<Users> users = usersRepository.findById(project.getManagerId());
        if (users.isPresent()) {
            users.get().setProjectId(savedProject.getProjectId());
            usersRepository.save(users.get());
        }

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
