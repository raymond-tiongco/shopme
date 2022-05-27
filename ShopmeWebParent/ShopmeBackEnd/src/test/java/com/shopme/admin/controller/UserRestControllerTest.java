package com.shopme.admin.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shopme.admin.dao.UserRepository;
import com.shopme.admin.service.UserService;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepo;
	
	@Test
	public void testCheckDuplicateEmail() throws Exception {
		int id = 1;
		String email = "admin@shopme.com";
		
		Mockito.when(userService.isUniqueEmail(id, email)).thenReturn(true);
		String url = "/users/checkEmail";
		mockMvc.perform(post(url).param("id", "1").param("email", email).with(csrf()))
				.andExpect(status().isFound	());
	}

}
