package com.shopme.admin.service;

import java.util.List;

import javax.transaction.Transactional;

import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.entity.Role;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepo roleRepo;
	
	public RoleServiceImpl(RoleRepo roleRepo) {
		this.roleRepo = roleRepo;
	}

	@Override
	public List<Role> findAll() {
		return roleRepo.findAll();
	}

	@Override
	public Role findOne(int id) {
		return roleRepo.findById(id).orElseThrow(RuntimeException::new);
	}
}