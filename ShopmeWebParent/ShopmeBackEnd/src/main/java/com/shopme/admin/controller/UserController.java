package com.shopme.admin.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import com.shopme.admin.exporter.UserExcelExporter;
import com.shopme.admin.exporter.UserPDFExporter;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	private RoleService roleService;
	
	@Autowired
	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}
	
	@InitBinder
    public void initBinder(WebDataBinder dataBinder) {
		
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

	@GetMapping
	public String listAll(Model model) {

		return listAllWithSortAndPage("id", "asc", 0, 10, model);
	}
	
	@GetMapping("/{field}/{sortDir}")
	public String listAllWithSort(@PathVariable String field, @PathVariable String sortDir, Model model) {
		
		List<User> users = userService.findUsersWithSorting(field, sortDir);
		
		model.addAttribute("users", users);
		model.addAttribute("sortDir", sortDir);
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("reverseSortDir", reverseSortDir);
		
		return "users";
	}
	
	@GetMapping("/{field}/{sortDir}/{offset}/{pageSize}")
	public String listAllWithSortAndPage(@PathVariable String field,
										@PathVariable String sortDir,
										@PathVariable int offset,
										@PathVariable int pageSize,
										Model model) {
		
		Page<User> users = userService.findUsersWithSortingAndPagination(field, sortDir, offset, pageSize);
		
		long totalItems = users.getTotalElements();
		int totalPages = users.getTotalPages();
		
		model.addAttribute("users", users);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("field", field);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("offset", offset);
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("reverseSortDir", reverseSortDir);
		
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
		
		return "users";
	}
	
	@GetMapping("/userForm")
	public String showUserForm(Model model) {
		User user = new User();
		List<Role> roles = roleService.findAll();
		
		model.addAttribute("user", user);
		model.addAttribute("roles", roles);
		
		return "user_form";
	}
	
	@GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId") int id, Model model) {
        User user = userService.findById(id);
        List<Role> roles = roleService.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "user_form";
    }
	
	@PostMapping("/save")
	public String savePhoto(@Valid @ModelAttribute("user") User user,
			BindingResult bindingResult,
			@RequestParam("image") MultipartFile multipartFile,
			RedirectAttributes redirAttrs,
			Model model) throws IOException {
		
		if (bindingResult.hasErrors()) {
			List<Role> roles = roleService.findAll();
			model.addAttribute("roles", roles);
            return "user_form";
        }
		
		if(userService.findByEmail(user.getEmail()) != null && user.getId() < 1) {
			redirAttrs.addFlashAttribute("error", "The email " + user.getEmail() + " already exists!");
			return "redirect:/users/userForm";
		}
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		if (multipartFile.isEmpty() || multipartFile == null) {
			if(user.getPhotos() == null) user.setPhotos(null);
			userService.save(user);
			redirAttrs.addFlashAttribute("message", "User [id: " + user.getId() + "] successfully saved");
			return "redirect:/users";
        }
		
		user.setPhotos(fileName);
		userService.save(user);
		
		String uploadDir = "./src/main/resources/static/images/user-photos/" + user.getId();
		
		Path uploadPath = Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try(InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			System.out.println(filePath.toFile().getAbsolutePath());
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			redirAttrs.addFlashAttribute("error", "Could not save uploaded file. Error: " + e);
			return "redirect:/users";
		}
		
		redirAttrs.addFlashAttribute("message", "User successfully saved!" + "[id: " + user.getId() + "]");
		
		return "redirect:/users";
	}
	
	@GetMapping("/delete")
    public String deleteUser(@RequestParam("userId") int id) {
        userService.deleteById(id);

        return "redirect:";
    }
	
	@GetMapping("/search/{field}/{sortDir}/{offset}/{pageSize}")
    private String searchUsers(@PathVariable String field,
			@PathVariable String sortDir,
			@PathVariable int offset,
			@PathVariable int pageSize,
    		@RequestParam("keyword") String keyword, Model model) {

        Page<User> users = userService.searchUsers(field, sortDir, offset, pageSize, keyword);

        long totalItems = users.getTotalElements();
		int totalPages = users.getTotalPages();
		
		model.addAttribute("users", users);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("field", field);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("offset", offset);
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("reverseSortDir", reverseSortDir);
		
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        return "users";
    }
	
	@GetMapping("/exportToCSV")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		String fileName = "users.csv";
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		
		response.setHeader(headerKey, headerValue);
		
		List<User> users = userService.findAll();
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
		String[] nameMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (User user : users) {
			csvWriter.write(user, nameMapping);
		}
		
		csvWriter.close();
	}
	
	@GetMapping("/exportToExcel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		String fileName = "users.xlsx";
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		
		response.setHeader(headerKey, headerValue);
		
		List<User> users = userService.findAll();
		
		UserExcelExporter excelExporter = new UserExcelExporter(users);
		excelExporter.export(response);
	}
	
	@GetMapping("/exportToPdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		String fileName = "users.pdf";
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		
		response.setHeader(headerKey, headerValue);
		
		List<User> users = userService.findAll();
		
		UserPDFExporter pdfExporter = new UserPDFExporter(users);
		pdfExporter.export(response);
	}
}
