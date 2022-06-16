package com.shopme.admin.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;

@DataJpaTest
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class UserRepositoryTests {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	@DisplayName("Test create User with one role")
	@Order(1)
	public void testCreateUserWithOneRole() {
		User user = new User("testemail@gmail.com", true, "Stephen", "Strange", "{noop}test123", "test.jpg", java.time.LocalDate.now());
		List<Role> roles  = new ArrayList<>();
		roles.add(roleRepository.getOne(1));
		user.setRoles(roles);
		userRepository.save(user);
		
		assertThat(user).isNotNull();
		assertThat(user.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test create user with no photos")
	@Order(2)
	public void testCreateUserWithNoPhotos() {
		User user = new User("testemail@gmail.com", true, "John", "Watts", "{noop}test123", null, java.time.LocalDate.now());
		List<Role> roles  = new ArrayList<>();
		roles.add(roleRepository.getOne(1));
		user.setRoles(roles);
		userRepository.save(user);
		
		assertThat(user.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test create disabled user")
	@Order(3)
	public void testCreateUserDisabled() {
		User user = new User("testemail@gmail.com", false, "Jerry", "Yan", "{noop}test123", "test.jpg", java.time.LocalDate.now());
		List<Role> roles  = new ArrayList<>();
		roles.add(roleRepository.getOne(1));
		user.setRoles(roles);
		userRepository.save(user);
		
		assertThat(user.getEnabled()).isFalse();
		assertThat(user.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test create user with Two Roles")
	@Order(4)
	public void testCreateUserWithTwoRoles() {
		User user = new User("wandamaximoff@shopme.com", true, "Wanda", "Maximoff", "test123", "test.jpg", java.time.LocalDate.now());
		List<Role> roles  = new ArrayList<>();
		roles.add(roleRepository.getOne(1));
		roles.add(roleRepository.getOne(2));
		roles.add(roleRepository.getOne(3));
		user.setRoles(roles);
		userRepository.save(user);
		
		assertThat(user.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Throw RuntimeException when creating User with null First Name")
	@Order(5)
	public void testCreateUserWithNullFirstName() {
		User user = new User("wandamaximoff@shopme.com", true, null, "Doe", "test123", "test.jpg", java.time.LocalDate.now());
		
		assertThrows(RuntimeException.class, () -> {
			userRepository.save(user);
		});
	}
	
	@Test
	@DisplayName("Throw RuntimeException when creating User with null Last Name")
	@Order(6)
	public void testCreateUserWithNullLastName() {
		User user = new User("wandamaximoff@shopme.com", true, "John", null, "test123", "test.jpg", java.time.LocalDate.now());
		
		assertThrows(RuntimeException.class, () -> {
			userRepository.save(user);
		});
	}
	
	@Test
	@DisplayName("Throw RuntimeException when creating User with null Email")
	@Order(7)
	public void testCreateUserWithNullEmail() {
		User user = new User(null, true, "John", "Doe", "test123", "test.jpg", java.time.LocalDate.now());
		
		assertThrows(RuntimeException.class, () -> {
			userRepository.save(user);
		});
	}
	
	@Test
	@DisplayName("Throw RuntimeException when creating User with duplicate Email")
	@Order(8)
	public void testCreateUserWithDuplicateEmail() {
		User user1 = new User("jdoe@shopme.com", true, "John", "Doe", "test123", "test.jpg", java.time.LocalDate.now());
		User user2 = new User("jdoe@shopme.com", true, "Josh", "Doe", "test123", "test.jpg", java.time.LocalDate.now());
		userRepository.save(user1);
		
		assertThrows(RuntimeException.class, () -> {
			userRepository.save(user2);
		});
	}
	
	@Test
	@DisplayName("Test get List of users")
	@Order(9)
	public void testReadAll() {
		List<User> users = userRepository.findAll();
		
		assertThat(users).isNotEmpty();
	}
	
	@Test
	@DisplayName("Test get one user")
	@Order(10)
	public void testGetSingleUser() {
		User user = userRepository.findById(3).get();
		
		assertThat(user.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Throw Runtime Exception if getting single user by id is null")
	@Order(11)
	public void testGetSingleUserThrowsRuntimeException() {
		assertThrows(RuntimeException.class, () -> {
			userRepository.findById(30).get();
		});
	}
	
	@Test
	@DisplayName("Test update user email")
	@Order(12)
	public void testUpdateUserEmail() {
		User user = userRepository.findById(2).get();
		user.setEmail("user@testemail.com");
		userRepository.save(user);
		
		assertThat(user.getEmail()).isEqualTo("user@testemail.com");
	}
	
	@Test
	@DisplayName("Test update user first name")
	@Order(13)
	public void testUpdateUserFirstName() {
		User user = userRepository.findById(2).get();
		user.setFirstName("Jai");
		userRepository.save(user);
		
		assertThat(user.getFirstName()).isEqualTo("Jai");
	}
	
	@Test
	@DisplayName("Test update user last name")
	@Order(14)
	public void testUpdateUserLastName() {
		User user = userRepository.findById(2).get();
		user.setLastName("Dela Cruz");
		userRepository.save(user);
		
		assertThat(user.getLastName()).isEqualTo("Dela Cruz");
	}
	
	@Test
	@DisplayName("Test update user password")
	@Order(15)
	public void testUpdateUserPassword() {
		User user = userRepository.findById(2).get();
		user.setPassword("test22");
		userRepository.save(user);
		
		assertThat(user.getPassword()).isEqualTo("test22");
	}
	
	@Test
	@DisplayName("Test update user photo")
	@Order(16)
	public void testUpdateUserPhoto() {
		User user = userRepository.findById(2).get();
		user.setPhotos("image2.jpg");
		userRepository.save(user);
		
		assertThat(user.getPhotos()).isEqualTo("image2.jpg");
	}
	
	@Test
	@DisplayName("Delete user by id")
	@Order(17)
	public void testDeleteUser() {
		userRepository.deleteById(1);
		
		assertThat(userRepository.existsById(1)).isFalse();
	}
	
	@Test
	@DisplayName("Throws Runtime Exception when user to be deleted does not exist")
	@Order(18)
	public void testDeleteUserThrowsRuntimeException() {
		assertThrows(RuntimeException.class, () -> {
			userRepository.deleteById(99);
		});
	}
	
}