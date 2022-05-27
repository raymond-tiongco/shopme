package com.shopme.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.UserService;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {
	private final UserService userService;

	@Autowired
	public UserRestController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/checkEmail")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		if(!userService.isUniqueEmail(id, email)) {
			return "Duplicated";
		}
		else {
			return "Ok";
		}
	}
	
	@GetMapping
	List<User> listAll() {
		return userService.findAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<User> findUserById(@PathVariable("id") int id){
		return new ResponseEntity<User>(userService.findById(id), HttpStatus.OK);
	}
	
	@PostMapping("/users")  
	public ResponseEntity<Object> createUser(@RequestBody User user) {
		User sevedUser = userService.save(user);    
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(sevedUser.getId()).toUri();  
		return ResponseEntity.created(location).build();  
	}
	
	@PutMapping("{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody User user){
		return new ResponseEntity<User>(userService.save(user), HttpStatus.OK);
	}
	
	@GetMapping("/delete/{id}")
	public void deleteUser(@PathVariable int id)  {  
		userService.deleteById(id);
	}  
}
