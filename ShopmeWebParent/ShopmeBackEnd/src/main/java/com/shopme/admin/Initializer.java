package com.shopme.admin;

import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final UserService userService;

    public Initializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        userService.createFolder();
        userService.initRolesAndUser();
    }
}
