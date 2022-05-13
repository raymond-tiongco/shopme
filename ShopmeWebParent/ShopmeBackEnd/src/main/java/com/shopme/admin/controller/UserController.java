package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private final RoleService roleService;
    private final UserService userService;
    private UserPDFExporter pdfExporter;
    private Model model;

    public UserController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/GetPhoto/{id}")   //  test
    public void getImage(@PathVariable(value = "id") int id, HttpServletResponse response) {

        userService.getImageAsStream(id, response);
    }

    @PostMapping("/Search") //  test
    public String search(@RequestParam(value = "keyword") String keyword, Model model) {
        List<User> users = userService.findByEmailLike(keyword);

        model.addAttribute("users", users);
        model.addAttribute("searchMessage", "About "+users.size()+" results for \""+keyword+"\"");
        model.addAttribute("isSearching", true);

        return "users";
    }

    @PostMapping("/SaveUser") // endpoint for saving or updating user. might create a separate endpoint for update if it gets complicated
    public String saveUser(
            @Valid @ModelAttribute("user") User user, Errors errors,
            @RequestParam(value = "roles", required = false) ArrayList<Integer> roles,
            @RequestParam(value = "photo") MultipartFile photo,
            @RequestParam(value = "enabled") ArrayList<Integer> enabled,
            @RequestParam(value = "isUpdate") boolean isUpdate, Model model) throws IOException {

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
        }

        userService.saveUser(user, enabled, roles, photo, isUpdate);
        model.addAttribute("alertMessage",
                "UserID "+user.getId()+" has been "+(isUpdate ? "Updated" : "Added")+".");

        return users(model);
    }

    @GetMapping("/AddUserForm") // test
    public String addUserForm(Model model) {
        User user = new User();

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roleService.findAll());
        model.addAttribute("isUpdate", false);

        return "user-form";
    }

    @GetMapping("/UpdateUserForm") // test
    public String updateUserForm(@RequestParam("userId") int userId, Model model) {
        User user = userService.findById(userId);

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roleService.findAll());
        model.addAttribute("isUpdate", true);

        return "user-form";
    }

    @GetMapping("/DeleteUser")  //  test
    public String delete(@RequestParam("userId") int userId, Model model) {
        userService.deleteById(userId);

        model.addAttribute("alertMessage",
                "A user with an id of "+userId+" has been deleted.");

        return users(model);
    }

    @GetMapping("/Enable")  //  test
    public String enable(@RequestParam(value = "userid") int userid,
                         @RequestParam(value = "page") int page,
                         Model model) {
        userService.enable(userid);

        model.addAttribute("alertMessage", "Successfully enabled User ID "+userid);
        return getOnePage(model, page);
    }

    @GetMapping("/Disable") // test
    public String disable(@RequestParam(value = "userid") int userid,
                          @RequestParam(value = "page") int page,
                          Model model) {
        userService.disable(userid);

        model.addAttribute("alertMessage", "Successfully disabled User ID "+userid);
        return getOnePage(model, page);
    }

    @GetMapping("/") // test
    public String root() {
        return "redirect:/Users";
    }

    @GetMapping("/Users") // test
    public String users(Model model) {
        return getOnePage(model, 1);
    }

    @GetMapping("/Users/{pageNumber}") // test
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage) {

        Page<User> page = userService.findPage(currentPage);

        int totalPages = page != null ? page.getTotalPages() : 1;
        long totalItems = page != null ? page.getTotalElements() : 0;
        List<User> users = page != null ? page.getContent() : new ArrayList<>();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("reverseSortDir", "desc");
        model.addAttribute("users", users);
        model.addAttribute("isSearching", false);

        return "users";
    }

    @GetMapping("/Users/{pageNumber}/{field}") // test
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir) {

        Page<User> page = userService.findPage(currentPage);

        int totalPages = page != null ? page.getTotalPages() : 1;
        long totalItems = page != null ? page.getTotalElements() : 0;
        List<User> users = page != null ? page.getContent() : new ArrayList<>();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        if (sortDir != null) {
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } else {
            model.addAttribute("sortDir", "asc");
            model.addAttribute("reverseSortDir", "desc");
        }

        model.addAttribute("users", userService.modifyList(new ArrayList<>(users), field, sortDir));

        model.addAttribute("isSearching", false);
        model.addAttribute("field", field);

        return "users";
    }

    @GetMapping("/CsvExport") // test
    public void downloadCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"users.csv\"");

        new UserCSVExporter(userService.findAll()).exportToCsv(response.getWriter());
    }

    @GetMapping("/ExcelExport") // test
    public void downloadExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

        IOUtils.copy(new UserExcelExporter(userService.findAll()).exportToExcel(), response.getOutputStream());
    }

    @GetMapping("/PdfExport") // test
    public void downloadPdf(HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=users.pdf");

        new UserPDFExporter(userService.findAll()).exportToPdf(response);
    }
}
