package com.shopme.admin.controller;

import com.shopme.admin.service.UserService;
import com.shopme.admin.utils.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<EmailResponse > checkDuplicateEmail(@RequestParam("email") String email) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                .body(new EmailResponse(userService.findByEmail(email) != null));
    }

    class EmailResponse {
        private boolean isDuplicate;

        public EmailResponse(boolean isDuplicate) {
            this.isDuplicate = isDuplicate;
        }

        public boolean isDuplicate() {
            return isDuplicate;
        }
    }
}
