package com.shopme.admin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.shopme.admin.dao.UserRepository;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Rollback(value = true)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private RoleService roleService;
	
	@InjectMocks
	private UserController userController;
	
	@Test
	public void testListAll() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		
		Mockito.when(userService.findAll()).thenReturn(users);
		String url = "/users";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
		assertTrue(users.size() > 0);
	} 
	
	@Test
	public void testShowUserFormForCreate() throws Exception {
		String url = "/users/userForm";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
	} 
	
	@Test
	public void testShowUserFormForUpdate() throws Exception {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles);
		
		Mockito.when(userService.findById(1)).thenReturn(user);
		String url = "/users/showFormForUpdate?userId=1";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles);
		
		Mockito.when(userService.findById(1)).thenReturn(user);
		String url = "/users/delete?userId=1";
		
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isFound()).andReturn();
		
		assertEquals(302, result.getResponse().getStatus());
	} 
	
	@Test
	public void testExportCSV() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToCSV";

        mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
	}
	
	@Test
	public void testExportExcel() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToExcel";
		
		mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
	} 
	
	@Test
	public void testExportPdf() throws Exception {	
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToPdf";
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
	}

}
