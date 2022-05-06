package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    private final RoleService roleService;
    private final UserService userService;
    private Model model;

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
        List<User> users = userService.findByEmailLike(keyword);

        model.addAttribute("users", users);
        model.addAttribute("searchMessage", "About "+users.size()+" results for \""+keyword+"\"");
        model.addAttribute("isSearching", true);

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
        model.addAttribute("isUpdate", isUpdate);

        if (roles == null) {
            model.addAttribute("roleEmptyError", "Select at least 1 role");
            return "user-form";
        }

        if (errors.hasErrors()) {
            return "user-form";
        }

        //  if new user, check for duplicate email and photo size
        if (!isUpdate) {

            if (userService.findByEmail(user.getEmail()) != null) {
                model.addAttribute("emailDuplicateError", "Email Address is taken");
                return "user-form";
            }

            if (photo.getSize() < 1) {
                model.addAttribute("photoError", "Photo is required");
                return "user-form";
            }
        }

        userService.saveUser(user, enabled, roles, photo, isUpdate);

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

    /*@GetMapping("/Users")
    public String users(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }*/

    @GetMapping("/Users")
    public String users(Model model) {
        return getOnePage(model, 1);
    }

    @GetMapping("/Users/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage) {

        Page<User> page = userService.findPage(currentPage);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<User> users = page.getContent();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("reverseSortDir", "desc");
        model.addAttribute("users", users);
        model.addAttribute("isSearching", false);

        return "users";
    }

    @GetMapping("/Users/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir) {

        Page<User> page = userService.findPage(currentPage);
        List<User> users = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        List<User> modifiableUsers = new ArrayList<>(users);

        modifiableUsers.sort((User user1, User user2) -> {

            try {
                Field field1 = user1.getClass().getDeclaredField(field);
                field1.setAccessible(true);
                Object object1 = field1.get(user1);

                Field field2 = user2.getClass().getDeclaredField(field);
                field2.setAccessible(true);
                Object object2 = field2.get(user2);

                int result = 0;

                if (isInt(object1.toString())) {
                    result = Integer.parseInt(object1.toString()) - Integer.parseInt(object2.toString());
                } else {
                    result = object1.toString().compareToIgnoreCase(object2.toString());
                }

                if (result > 0) {
                    return sortDir.equalsIgnoreCase("asc") ? 1 : -1;
                }

                if (result < 0) {
                    return sortDir.equalsIgnoreCase("asc") ? -1 : 1;
                }

                return 0;

            } catch (Exception e) {
                return 0;
            }
        });

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("users", modifiableUsers);

        model.addAttribute("isSearching", false);
        model.addAttribute("field", field);

        return "users";
    }

    @GetMapping("/CsvExport")
    public void downloadCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
        userService.exportToCsv(response.getWriter());
    }

    @GetMapping("/ExcelExport")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");
        IOUtils.copy(userService.exportToExcel(userService.findAll()), response.getOutputStream());
    }

    @GetMapping("/PdfExport")
    public void downloadPdf(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=user.pdf");
        userService.exportToPdf(response);
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
