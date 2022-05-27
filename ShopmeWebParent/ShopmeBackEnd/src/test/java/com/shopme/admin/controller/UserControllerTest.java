package com.shopme.admin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.dao.UserRepository;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc(addFilters = false)
@Rollback(value = true)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private RoleService roleService;
	
	@InjectMocks
	private UserController userController;
	
	@Test
	public void testGetListofUsers() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", roles));
		
		Mockito.when(userService.findAll()).thenReturn(users);
		String url = "/users";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk())
						.andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
		assertTrue(users.size() == 2);
	} 
	
	@Test
	public void testGetAllUsersWithSortAndPage() throws Exception {
		String field = "firstName";
		String sortDirection = "asc";
		int offset = 0;
		int pageSize = 5;
		
		String url = "/users/" + field + "/" + sortDirection + "/" + offset + "/" + pageSize;
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
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
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles);
		
		Mockito.when(userService.findById(1)).thenReturn(user);
		String url = "/users/showFormForUpdate?userId=1";
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
	} 
	
	@Test
	public void testSaveUser() throws Exception {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles);
		
		Mockito.when(userService.save(user)).thenReturn(user);
		
		String url = "/users/save";
		
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
							.content(objectMapper.writeValueAsString(user)).with(csrf()))
							.andExpect(status().isOk()).andReturn();
		
		assertEquals(200, result.getResponse().getStatus());
	} // not yet done
	
	@Test
	public void testDeleteUser() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", roles));
		
		Mockito.when(userService.findAll()).thenReturn(users);
		String url = "/users/delete?userId=1";
		
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isFound()).andReturn();
		
		assertEquals(302, result.getResponse().getStatus());
	} 
	
	@Test
	public void testExportCsv() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToCsv";

        MvcResult result = mockMvc.perform(get(url)).andExpect(status().isNotFound()).andReturn();

        byte[] bytes = result.getResponse().getContentAsByteArray();
        Path path = Paths.get("users.csv");
        Files.write(path, bytes);
	}
	
	@Test
	public void testExportExcel() throws Exception {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToExcel";
		
		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		byte[] bytes = result.getResponse().getContentAsByteArray();
		Path path = Paths.get("users.xlsx");
		Files.write(path, bytes);
	} 
	
	@Test
	public void testExportPdf() throws Exception {	
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", roles));
		Mockito.when(userService.findAll()).thenReturn(users);
		
		String url = "/users/exportToPdf";
		
		mockMvc.perform(get(url)).andExpect(status().isOk());
	}

}
