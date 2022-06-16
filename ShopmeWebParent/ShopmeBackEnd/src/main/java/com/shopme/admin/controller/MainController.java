package com.shopme.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/categories")
	public String viewCategories() {
		return "under_construction";
	}
	
	@GetMapping("/brands")
	public String viewBrands() {
		return "under_construction";
	}
	
	@GetMapping("/products")
	public String viewProducts() {
		return "under_construction";
	}
	
	@GetMapping("/customers")
	public String viewCustomers() {
		return "under_construction";
	}
	
	@GetMapping("/shipping")
	public String viewShipping() {
		return "under_construction";
	}
	
	@GetMapping("/orders")
	public String viewOrders() {
		return "under_construction";
	}
	
	@GetMapping("/salesReport")
	public String viewSalesReport() {
		return "under_construction";
	}
	
	@GetMapping("/articles")
	public String viewArticles() {
		return "under_construction";
	}
	
	@GetMapping("/menus")
	public String viewMenus() {
		return "under_construction";
	}
	
	@GetMapping("/settings")
	public String viewSettings() {
		return "under_construction";
	}
}
