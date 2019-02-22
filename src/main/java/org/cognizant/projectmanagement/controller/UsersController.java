package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Users;
import org.cognizant.projectmanagement.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/users/{id}")
    @ResponseBody
    public Users getUser(@PathVariable long id) {
        Optional<Users> user = usersRepository.findById(id);

        if (!user.isPresent())
            throw new RuntimeException("Not found: id-" + id);

        return user.get();
    }

    @GetMapping("/users")
    @ResponseBody
    public List<Users> getUsers() {
        List<Users> users = usersRepository.findAll();

        if (users.isEmpty())
            throw new RuntimeException("Not found");

        return users;
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<Void> addUser(@RequestBody Users users) {
        Users savedUser = usersRepository.save(users);
        if (savedUser == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getEmployeeId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Object> editUser(@RequestBody Users users, @PathVariable long id) {
        Optional<Users> usersOptional = usersRepository.findById(id);

        if (!usersOptional.isPresent())
            return ResponseEntity.notFound().build();

        users.setEmployeeId(id);

        usersRepository.save(users);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable long id) {
        usersRepository.deleteById(id);
    }
}
