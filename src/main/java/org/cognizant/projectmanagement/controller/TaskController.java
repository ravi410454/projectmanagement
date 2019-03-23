package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Task;
import org.cognizant.projectmanagement.api.Users;
import org.cognizant.projectmanagement.repo.TaskRepository;
import org.cognizant.projectmanagement.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/task/{id}")
    @ResponseBody
    public Task getTask(@PathVariable long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (!task.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        return task.get();
    }

    @GetMapping("/task")
    @ResponseBody
    public List<Task> getTasks() {
        List<Task> tasks = taskRepository.findAll();

        if (tasks.isEmpty())
            throw new RuntimeException("Not found");

        return tasks;
    }

    @PostMapping("/task")
    @ResponseBody
    public ResponseEntity<Object> addTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        if (savedTask == null)
            return ResponseEntity.noContent().build();
        Optional<Users> usersOptional = usersRepository.findById(task.getUserId());
        if (usersOptional.isPresent()) {
            Users user = usersOptional.get();
            user.setProjectId(savedTask.getProjectId());
            user.setTaskId(savedTask.getTaskId());
            usersRepository.save(user);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedTask.getTaskId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/task/{id}")
    @ResponseBody
    public ResponseEntity<Object> editTask(@RequestBody Task task, @PathVariable long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (!taskOptional.isPresent())
            return ResponseEntity.notFound().build();

        task.setTaskId(id);

        taskRepository.save(task);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/task/{id}")
    @ResponseBody
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }
}
