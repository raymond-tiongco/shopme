package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/SearchKey")
    public List<User> searchKey(@RequestParam(value = "keyword") String keyword) {
        return userService.search(keyword, Arrays.asList("id", "email", "firstName", "lastName"));
    }

    @GetMapping("/DeleteUserRest")
    public String delete(@RequestParam("userId") int userId, Model model) {
        userService.deleteById(userId);

        return "A user with an id of "+userId+" has been deleted.";
    }
}
