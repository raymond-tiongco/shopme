package com.shopme.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.dao.RoleRepository;
import com.shopme.shopmecommon.entity.Role;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class RoleServiceTest {

	@MockBean
	private RoleRepository roleRepository;
	
	@InjectMocks
	private RoleServiceImpl roleService;
	
	@Test
	@DisplayName("Test if roles are returned")
	public void testGetAllRoles() {
		List<Role> roles = new ArrayList<>();
		roles.add(new Role("Admin", "Manages everything"));
		roles.add(new Role("Shipper", "Manages shipment of order"));
		roles.add(new Role("Seller", "Sells products"));
		
		Mockito.when(roleRepository.findAll()).thenReturn(roles);
		
		assertThat(roles).isNotEmpty();
		assertTrue(roles.size() == 3);
	}

}
