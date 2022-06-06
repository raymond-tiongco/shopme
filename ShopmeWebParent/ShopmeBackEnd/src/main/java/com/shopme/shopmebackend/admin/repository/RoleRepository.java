package com.shopme.shopmebackend.admin.repository;

import com.shopme.shopmebackend.admin.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role,Integer> {

}
