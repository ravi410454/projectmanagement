package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.ParentTask;
import org.cognizant.projectmanagement.repo.ParentTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class ParentTaskController {

    @Autowired
    private ParentTaskRepository parentTaskRepository;

    @GetMapping("/parenttask/{id}")
    @ResponseBody
    public ParentTask getParentTask(@PathVariable long id) {
        Optional<ParentTask> parentTask = parentTaskRepository.findById(id);

        if (!parentTask.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        return parentTask.get();
    }

    @GetMapping("/parenttask")
    @ResponseBody
    public List<ParentTask> getParentTasks() {
        List<ParentTask> parentTasks = parentTaskRepository.findAll();

        if (parentTasks.isEmpty())
            throw new RuntimeException("Not found");

        return parentTasks;
    }

    @PostMapping("/parenttask")
    @ResponseBody
    public ResponseEntity<Object> addParentTask(@RequestBody ParentTask parentTask) {
        ParentTask savedParentTask = parentTaskRepository.save(parentTask);
        if (savedParentTask == null)
            return ResponseEntity.noContent().build();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedParentTask.getParentId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/parenttask/{id}")
    @ResponseBody
    public ResponseEntity<Object> editParentTask(@RequestBody ParentTask parentTask, @PathVariable long id) {
        Optional<ParentTask> parentTaskOptional = parentTaskRepository.findById(id);

        if (!parentTaskOptional.isPresent())
            return ResponseEntity.notFound().build();

        parentTask.setParentId(id);

        parentTaskRepository.save(parentTask);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/parenttask/{id}")
    @ResponseBody
    public void deleteParentTask(@PathVariable long id) {
        parentTaskRepository.deleteById(id);
    }
}
