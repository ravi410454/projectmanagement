package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Task;
import org.cognizant.projectmanagement.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/task/{id}")
    @ResponseBody
    public Task getTask(@PathVariable long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (!task.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        return task.get();
    }

    @PostMapping("/task")
    @ResponseBody
    public ResponseEntity<Object> addTask(@RequestBody Task task) {
        Task savedTask = taskRepository.save(task);
        if (savedTask == null)
            return ResponseEntity.noContent().build();
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
