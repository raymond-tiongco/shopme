package com.shopme.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@GetMapping("/showLoginPage")
    public String showMyLoginPage() {
        return "user_login";
    }
	
	@GetMapping("/access-denied")
	public String accessDenied() {
	    return "access-denied";
	}
}
