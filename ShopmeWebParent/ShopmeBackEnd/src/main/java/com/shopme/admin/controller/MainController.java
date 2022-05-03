package com.shopme.admin.controller;

import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

	private final RoleService roleService;
	private final UserService userService;

	public MainController(RoleService roleService, UserService userService) {
		this.roleService = roleService;
		this.userService = userService;
	}

	@GetMapping("/AccessDenied")
	public String accessDenied() {
		return "access-denied";
	}

	@GetMapping("/Login")
	public String login() {
		return "login";
	}

	@GetMapping("/Logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/Login";
	}

	@PostMapping("/Search")
	public String search(@RequestParam(value = "keyword") String keyword) {
		return "users";
	}

	@PostMapping("/SaveUser")
	public String saveUser(
			@Valid @ModelAttribute("user") User user,
			Errors errors/*,
			@RequestParam(value = "roles") ArrayList<Integer> roles,
			@RequestParam(value = "photo") MultipartFile photo,
			@RequestParam(value = "enabled") ArrayList<Integer> enabled*/) throws IOException {

		if (errors.hasErrors()) {
			System.out.println("ERROR");
			return "user-form";
		}

		//userService.saveUser(user, enabled, roles, photo);

		return "redirect:/Users";
	}

	@GetMapping("/AddUserForm")
	public String addUserForm(Model model) {
		User user = new User();
		//user.setEmail("joebiden@gmail.com");
		/*user.setEmail("m");
		user.setFirstName("Joe");
		user.setLastName("Biden");
		user.setPassword("iuouiou56757657");*/

		model.addAttribute("user", user);
		//model.addAttribute("rolesList", roleService.findAll());

		return "user-form";
	}

	@GetMapping("/UpdateUserForm")
	public String updateUserForm(@RequestParam("userId") int userId, Model model) {
		User user = userService.findById(userId);

		model.addAttribute("user", user);
		model.addAttribute("rolesList", roleService.findAll());

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

	@GetMapping("/Categories")
	public String categories() {
		return "categories";
	}

	@GetMapping("/Brands")
	public String brands() {
		return "brands";
	}

	@GetMapping("/Products")
	public String products() {
		return "products";
	}

	@GetMapping("/Customers")
	public String customers() {
		return "customers";
	}

	@GetMapping("/Shipping")
	public String shipping() {
		return "shipping";
	}

	@GetMapping("/Orders")
	public String orders() {
		return "orders";
	}

	@GetMapping("/SalesReport")
	public String salesReport() {
		return "sales-report";
	}

	@GetMapping("/Articles")
	public String articles() {
		return "articles";
	}

	@GetMapping("/Menus")
	public String menus() {
		return "menus";
	}

	@GetMapping("/Settings")
	public String settings() {
		return "settings";
	}
}