package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private final RoleService roleService;
    private final UserService userService;

    public UserController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/GetPhoto/{id}")
    public void getImage(@PathVariable(value = "id") int id, HttpServletResponse response) {

        userService.getImageAsStream(id, response);
    }

    @PostMapping("/Search")
    public String search(@RequestParam(value = "keyword") String keyword, Model model) {

        model.addAttribute("users", userService.findByEmailLike(keyword));

        return "users";
    }

    @PostMapping("/SaveUser")
    public String saveUser(
            @Valid @ModelAttribute("user") User user, Errors errors,
            @RequestParam(value = "roles", required = false) ArrayList<Integer> roles,
            @RequestParam(value = "photo") MultipartFile photo,
            @RequestParam(value = "enabled") ArrayList<Integer> enabled,
            @RequestParam(value = "isUpdate") boolean isUpdate, Model model
    ) throws IOException {

        model.addAttribute("rolesList", roleService.findAll());

        if (roles == null) {
            model.addAttribute("roleEmptyError", "Select at least 1 role");
            return "user-form";
        }

        if (photo.getSize() < 1) {
            model.addAttribute("photoError", "Photo is required");
            return "user-form";
        }

        if (errors.hasErrors()) {
            return "user-form";
        }

        if (!isUpdate) {
            if (userService.findByEmail(user.getEmail()) != null) {
                model.addAttribute("emailDuplicateError", "Email Address is taken");
                return "user-form";
            }
        }

        userService.saveUser(user, enabled, roles, photo);

        return "redirect:/Users";
    }

    @GetMapping("/AddUserForm")
    public String addUserForm(Model model) {
        User user = new User();
        user.setEmail("joebiden@gmail.com");
        user.setFirstName("Super");
        user.setLastName("Mario");
        user.setPassword("supermario123");

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roleService.findAll());
        model.addAttribute("isUpdate", false);

        return "user-form";
    }

    @GetMapping("/UpdateUserForm")
    public String updateUserForm(@RequestParam("userId") int userId, Model model) {
        User user = userService.findById(userId);

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roleService.findAll());
        model.addAttribute("isUpdate", true);

        return "user-form";
    }

    @GetMapping("/Users")
    public String users(Model model) {
        List<User> users = userService.findAll();

        model.addAttribute("users", users);

        return "users";
    }

    @GetMapping("/DeleteUser")
    public String delete(@RequestParam("userId") int userId) {

        userService.deleteById(userId);

        return "redirect:/Users";
    }

    @GetMapping("/Enable")
    public String enable(@RequestParam(value = "userid") int userid) {
        userService.enable(userid);

        return "redirect:/Users";
    }

    @GetMapping("/Disable")
    public String disable(@RequestParam(value = "userid") int userid) {
        userService.disable(userid);

        return "redirect:/Users";
    }
}
