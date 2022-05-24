package com.shopme.admin.service;

import java.util.List;

import javax.transaction.Transactional;

import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.Roles;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepo roleRepo;
	private final UserRepo userRepo;
	
	public RoleServiceImpl(RoleRepo roleRepo, UserRepo userRepo) {
		this.roleRepo = roleRepo;
		this.userRepo = userRepo;
	}

	@Override
	public List<Role> findAll() {
		return roleRepo.findAll();
	}

	@Override
	public Role findOne(int id) {
		return roleRepo.findById(id).orElseThrow(RuntimeException::new);
	}

	@Override
	public Role findByName(String name) {
		return roleRepo.findByName(name);
	}

	public void deleteAll() {
		roleRepo.deleteAll();
	}

	@Override
	public void fillRoles() {
		roleRepo.save(new Role(Roles.Admin.ID, Roles.Admin.name(), Roles.Admin.DESCRIPTION));
		roleRepo.save(new Role(Roles.Salesperson.ID, Roles.Salesperson.name(), Roles.Salesperson.DESCRIPTION));
		roleRepo.save(new Role(Roles.Editor.ID, Roles.Editor.name(), Roles.Editor.DESCRIPTION));
		roleRepo.save(new Role(Roles.Shipper.ID, Roles.Shipper.name(), Roles.Shipper.DESCRIPTION));
		roleRepo.save(new Role(Roles.Assistant.ID, Roles.Assistant.name(), Roles.Assistant.DESCRIPTION));
	}
}