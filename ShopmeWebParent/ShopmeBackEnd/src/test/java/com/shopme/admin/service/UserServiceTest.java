package com.shopme.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.dao.UserRepository;
import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	@Test
	@DisplayName("Test find all users")
	public void testFindAllUsers() {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		
		Mockito.when(userRepository.findAll()).thenReturn(users);
		
		List<User> getUsers = userService.findAll();
		
		assertThat(getUsers).isNotNull();
		assertTrue(getUsers.size() > 0);
	}
	
	@Test
	@DisplayName("Test search for users by keyword - search in id, first name, last name and email")
	public void testSearchUsersUsingKeyword() {
		List<User> users = new ArrayList<>();
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		users.add(new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(2, "janedoe@shopme.com", false, "Jane", "Doe", "test123", "photo.jpeg", java.time.LocalDate.now(), roles));
		users.add(new User(3, "test@shopme.com", false, "Tom", "Baily", "test1234", "photo.jpeg", java.time.LocalDate.now(), roles));
		
		Mockito.when(userRepository.findByIdOrFirstNameOrLastNameOrEmailOrRoles("John")).thenReturn(users);
		
		Page<User> searchResults = userService.searchUsers("id", "asc", 0, 5, "John");
		List<User> resultsList = searchResults.toList();
		
		assertThat(resultsList).isNotNull();
		assertThat(resultsList.size()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test find users by email")
	public void testFindByEmail() {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles);
		
		Mockito.when(userRepository.getUserByEmail(anyString())).thenReturn(user);
		
		User searchUser = userService.findByEmail("johndoe@shopme.com");
		
		assertNotNull(searchUser);
		assertEquals("John", searchUser.getFirstName());
		
	}
	
	@Test
	@DisplayName("Test save one user")
	public void testSaveOneUser() {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		User user = new User(1, "johndoe@shopme.com", true, "John", "Doe", "testpassword", "photo.jpeg", java.time.LocalDate.now(), roles);
		
		Mockito.when(bcryptPasswordEncoder.encode(anyString())).thenReturn("%$%^%^^%^%$%$%$&");
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
		
		User savedUser = userService.save(user);
		assertNotNull(savedUser);
		assertEquals(user.getFirstName(), savedUser.getFirstName());
	}
	
	@Test
	@DisplayName("Test if email is unique - returns true")
	public void testUniqueEmailWhenCreatingNewUser() {
		int id = 0;
		String email = "johndoe@gmail.com";
		
		Mockito.when(userRepository.getUserByEmail(email)).thenReturn(null);
		
		boolean result = userService.isUniqueEmail(id, email);
		
		assertTrue(result);
	}
	
	@Test
	@DisplayName("Test if email is not unique - returns false")
	public void testNotUniqueEmailWhenCreatingNewUser() {
		int id = 0;
		String email = "admin@shopme.com";
		
		User user = new User();
		user.setId(1);
		user.setEmail(email);
		
		Mockito.when(userRepository.getUserByEmail(email)).thenReturn(user);
		
		boolean result = userService.isUniqueEmail(id, email);
		
		assertFalse(result);
	}

}
