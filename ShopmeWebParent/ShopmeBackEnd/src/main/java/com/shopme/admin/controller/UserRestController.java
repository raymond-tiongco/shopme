package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/SearchUsersWithPage")
    public ResponseEntity<List<User>> searchRest(
            @RequestParam("keyword") String keyword,
            @RequestParam("pageNo") int pageNo) {

        return ResponseEntity.ok(userService.findPageByKeyword(keyword, pageNo).getContent());
    }

    @GetMapping("/SearchUsers")
    public ResponseEntity<List<User>> searchCriteria(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(userService.findListByKeyword(keyword));
    }
}