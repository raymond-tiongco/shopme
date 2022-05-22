package com.shopme.admin.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.admin.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
