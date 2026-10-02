package org.example.taskmanager.controller;

import org.example.taskmanager.model.AppUser;
import org.example.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @ResponseBody
    public List<AppUser> getUsers() {
        return userRepository.findAllByOrderByUsernameAscRegistrationDateAsc();
    }
}






