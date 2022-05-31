package com.shopme.admin.controller;

import com.shopme.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/CheckDuplicateEmail")
    public String checkDuplicateEmail(@RequestParam("email") String email) {
        return userService.isDuplicate(email) ? email+" exists" : email+" does not exist";
    }
}
