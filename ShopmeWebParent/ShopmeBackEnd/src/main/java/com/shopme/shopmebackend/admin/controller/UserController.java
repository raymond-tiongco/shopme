package com.shopme.shopmebackend.admin.controller;


import com.shopme.shopmebackend.admin.entity.Role;
import com.shopme.shopmebackend.admin.entity.User;
import com.shopme.shopmebackend.admin.exception.UserNotFoundException;
import com.shopme.shopmebackend.admin.export.UserExcelExporter;
import com.shopme.shopmebackend.admin.export.UserPDFExporter;
import com.shopme.shopmebackend.admin.UploadImage;
import com.shopme.shopmebackend.admin.service.UserService;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }


    @GetMapping("/list")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "firstName", "asc", null);  //default is "firstName" field and ascending
    }
    @GetMapping("/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber,
                             Model model, String sortField, String sortDir, String keyword) {

        Page<User> page =  userService.listByPage(pageNumber, sortField, sortDir, keyword);
        List<User> listUsers = page.getContent();

        long startCount = (pageNumber - 1) * userService.USER_PER_PAGE + 1;
        long endCount = startCount + userService.USER_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDir = "";
        if (sortDir.equals("asc")) {
            reverseSortDir = "desc";
        } else {
            reverseSortDir = "asc";
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "users/list-users";
    }
    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model){
        User user = new User();
        List<Role> roles = userService.listRoles();
        model.addAttribute("user",user);

        model.addAttribute("listRoles",roles);
        model.addAttribute("pageTitle","Create new User");
        return "users/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           @RequestParam("image")MultipartFile multipartFile, Model model)
                          throws IOException {

        if(bindingResult.hasErrors()){
            return "users/user-form";
        }

       if(!userService.isEmailUnique(user.getEmail(), user.getId())){
           model.addAttribute("duplicateEmail", "Sorry, the email "+user.getEmail()+" already exist!");
           if(user.getId() != null){
               return showFormForUpdate(user.getId(), model, redirectAttributes);
           }else{

               return showFormForAdd(model);
           }

       }else{
           if(!multipartFile.isEmpty()){
               String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
               user.setPhotos(fileName);
               User savedUser =   userService.save(user);
               String uploadDir = "user-photos/" + savedUser.getId();

               UploadImage.cleanDir(uploadDir);
               UploadImage.saveFile(uploadDir, fileName, multipartFile);
           }else{
               if(user.getPhotos().isEmpty()){
                   user.setPhotos(null);
               }
                if(user.getId() == null){
                    redirectAttributes.addFlashAttribute("message","The user has been successfully added!");
                    userService.save(user);
                }else{
                    redirectAttributes.addFlashAttribute("message","The user has been successfully updated!");
                }

           }


       }
        model.addAttribute("users", userService.listUsers());


        return redirectURLToUser(user);
    }
    private String redirectURLToUser(User user){
        String email = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + email;
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId")Integer id,
                                    Model model,
                                    RedirectAttributes redirectAttributes){
        try{
            User user = userService.findById(id);
            model.addAttribute("user",user);
            List<Role> roles = userService.listRoles();
            model.addAttribute("listRoles",roles);
            model.addAttribute("pageTitle","Update user (ID: " + id + ")");
            return "users/user-form";
        }catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users/list";
        }
    }
    @GetMapping("/delete")
    public String delete(@RequestParam("userId") Integer id){
        userService.deleteById(id);
        return "redirect:/users/list";
    }


    @GetMapping("/enabled/{status}")
    public String enabledStatusUser(@RequestParam("userId")Integer id,
                                    @PathVariable("status") boolean enabled,
                                    RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            userService.updateUserEnabledStatus(id, enabled);
            String status = enabled ? "enable" : "disable";
            String message = "The user " + user.getEmail() + " has been " + status;
            redirectAttributes.addFlashAttribute("message", message);
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/users/list";
    }


    // exports

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse httpServletResponse) throws IOException{
        httpServletResponse.setContentType("text/csv");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime +".csv";
        httpServletResponse.setHeader(headerKey,headerValue);

        List<User> users = userService.listUsers();

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(httpServletResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User Id", "Email", "First Name","Last Name","Roles","Enabled"};

        String[] nameMapping = {"id","email","firstName","lastName","roles","enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for(User user : users){
            csvBeanWriter.write(user,nameMapping);
        }
        csvBeanWriter.close();
     }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        httpServletResponse.setHeader(headerKey, headerValue);

        List<User> users = userService.listUsers();

        UserExcelExporter excelExporter = new UserExcelExporter(users);

        excelExporter.export(httpServletResponse);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse httpServletResponse) throws DocumentException, IOException {
        httpServletResponse.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        httpServletResponse.setHeader(headerKey, headerValue);

        List<User> users = userService.listUsers();

        UserPDFExporter userPDFExporter = new UserPDFExporter(users);

        userPDFExporter.export(httpServletResponse);
    }
}
