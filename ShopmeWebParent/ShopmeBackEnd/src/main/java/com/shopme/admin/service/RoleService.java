package com.shopme.admin.service;

import com.shopme.admin.entity.Role;

import java.util.List;

public interface RoleService {

	//	tested
	List<Role> findAll();

	//	tested
	Role findOne(int id);
}
