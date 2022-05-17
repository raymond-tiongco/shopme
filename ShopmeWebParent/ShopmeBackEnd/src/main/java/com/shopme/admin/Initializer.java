package com.shopme.admin;

import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Initializer implements CommandLineRunner {

    private final UserService userService;

    public Initializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        //  this body is meant to run first time only

        /*userService.saveRole(Roles.Admin.name(), Roles.Admin.DESCRIPTION);
        userService.saveRole(Roles.Salesperson.name(), Roles.Salesperson.DESCRIPTION);
        userService.saveRole(Roles.Editor.name(), Roles.Editor.DESCRIPTION);
        userService.saveRole(Roles.Shipper.name(), Roles.Shipper.DESCRIPTION);
        userService.saveRole(Roles.Assistant.name(), Roles.Assistant.DESCRIPTION);

        User root = new User()
                .email("superuser@gmail.com")
                .enabled(1)
                .firstName("RootUser")
                .lastName("RootUser")
                .password("rootuser4567");

        userService.saveRootUser(root);

        userService.addRoleToUser("superuser@gmail.com", Roles.Admin.name());*/
    }
}
