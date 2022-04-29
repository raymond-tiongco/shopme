package com.shopme.admin;

import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {

        /*
        userService.saveRole(Roles.Admin.name(), "The Administrator");
        userService.saveRole(Roles.Assistant.name(), "The Assistant");
        userService.saveRole(Roles.Editor.name(), "The Editor");
        userService.saveRole(Roles.Salesperson.name(), "The Salesperson");

        User root = new User()
                .email("darylldagondon@gmail.com")
                .enabled(1)
                .firstName("Daryll David")
                .lastName("Dagondon")
                .password("daryll123");

        userService.saveRootUser(root);

        userService.addRoleToUser("darylldagondon@gmail.com", Roles.Admin.name());

         */
    }
}
