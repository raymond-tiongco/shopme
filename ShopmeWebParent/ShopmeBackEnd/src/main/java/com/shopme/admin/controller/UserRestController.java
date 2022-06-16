package com.shopme.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public String checkDuplicateEmail(@Param("id") int id, @Param("email") String email) {
		if(!userService.isUniqueEmail(id, email)) {
			return "Duplicated";
		}
		else {
			return "Ok";
		}
	}
}