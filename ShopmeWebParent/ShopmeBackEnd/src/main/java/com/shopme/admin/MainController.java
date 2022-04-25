package com.shopme.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/Users")
	public String users() {
		return "users";
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
