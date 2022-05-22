package com.shopme.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.dao.UserRepository;
import com.shopme.admin.entity.User;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {
	private final UserRepository userRepository;

	@Autowired
	public UserRestController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	List<User> all() {
		return userRepository.findAll();
	}
	
	@GetMapping("/search/{keyword}")
    private List<User> searchUsers(@PathVariable String keyword, Model theModel) {

        System.out.println("[UserController] Searching...");
        List<User> users = userRepository.findByFirstNameOrLastNameOrEmail(keyword);
        
        if(users.isEmpty()) System.out.println("[UserController] Sorry, user is not found!");
        else {
        	System.out.println("Users found! users: " + users);
        }
        
        return users;
    }
}
