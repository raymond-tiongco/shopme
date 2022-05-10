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

        //  this body is meant to run first time only
        /*
        userService.saveRole(Roles.Admin.name(), "Manage everything");
        userService.saveRole(Roles.Salesperson.name(), "Manage product price, customers, shipping, orders and sales report");
        userService.saveRole(Roles.Editor.name(), "Manage caetgories, brands, products, articles and menus");
        userService.saveRole(Roles.Shipper.name(), "View products, view orders and update order status");
        userService.saveRole(Roles.Assistant.name(), "Manage product price, customers, shipping, orders and sales report");

        User root = new User()
                .email("superuser@gmail.com")
                .enabled(1)
                .firstName("RootUser")
                .lastName("RootUser")
                .password("rootuser4567");

        userService.saveRootUser(root);

        userService.addRoleToUser("superuser@gmail.com", Roles.Admin.name());
         */
    }
}
