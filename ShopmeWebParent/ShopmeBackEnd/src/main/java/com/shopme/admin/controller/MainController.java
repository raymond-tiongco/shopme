package com.shopme.admin.controller;

import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import com.shopme.admin.utils.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

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

	@GetMapping("/ErrorPage")
	public String errorPage() {
		return "error-page";
	}

	@GetMapping("/Login")
	public String login() {
		return "login";
	}

	@GetMapping("/LoginError")
	public String loginFailureUrl(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession(false);
		String errorMessage = null;

		if (session != null) {
			AuthenticationException ex = (AuthenticationException) session
					.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

			if (ex != null) {
				errorMessage = ex.getMessage();
				//if (ex.getMessage().equals("Bad credentials"))
			}
		}

		System.out.println("errorMessage="+errorMessage);

		return "login";
	}

	@GetMapping("/Logout")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			model.addAttribute("logout", "Signing out "+auth.getName());
			Log.info(auth.getName()+" has logged out");
		}

		return "redirect:/Login";
	}

	@GetMapping("/Fragments")
	public String header() {
		return "fragments";
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

	@GetMapping("/Profile")
	public String profile() {
		return "profile";
	}
}