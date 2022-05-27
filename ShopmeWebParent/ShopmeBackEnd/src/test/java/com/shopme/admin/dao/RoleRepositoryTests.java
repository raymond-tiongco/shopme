package com.shopme.admin.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class RoleRepositoryTests {

	private RoleRepository roleRepository;
	
	@Autowired
	public RoleRepositoryTests(RoleRepository roleRepo) {
		this.roleRepository = roleRepo;
	}
	
	@Test
	@DisplayName("Test single role creation")
	@Order(1)
	public void testCreateSingleRole() {
		Role role = new Role();
		role.setName("SELLER");
		role.setDescription("Sells products");
		roleRepository.save(role);
		
		assertThat(role.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test get list of roles")
	@Order(2)
	public void testReadAll() {
		List<Role> roles = roleRepository.findAll();
		
		assertThat(roles).isNotEmpty();
	}
	
	@Test
	@DisplayName("Test get single role")
	@Order(3)
	public void testSingleRole() {
		Role role = roleRepository.findById(1).get();
		
		assertThat(role).isNotNull();
		assertThat(role.getId()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("Test update role")
	@Order(4)
	public void testUpdateRole() {
		Role role = roleRepository.findById(1).get();
		role.setName("Seller");
		roleRepository.save(role);
		
		assertThat(role.getName()).isNotEqualTo("ADMIN");
	}
	
	@Test
	@DisplayName("Test delete role")
	@Order(5)
	public void testDeleteRole() {
		Role role = new Role("Seller", "Sells products");
		roleRepository.save(role);
		int id = role.getId();
		roleRepository.deleteById(id);
		
		assertThat(roleRepository.existsById(id)).isFalse();
	}
	
}
