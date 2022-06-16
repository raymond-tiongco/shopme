package com.shopme.admin.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.shopmecommon.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
