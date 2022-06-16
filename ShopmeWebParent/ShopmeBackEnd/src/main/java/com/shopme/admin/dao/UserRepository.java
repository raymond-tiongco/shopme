package com.shopme.admin.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.shopmecommon.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE"
			+ " r.name LIKE CONCAT('%', :keyword, '%')"
			+ " OR"
			+ " CONCAT(u.id, ' ', u.firstName, ' ', u.lastName, ' ', u.email)"
			+ " LIKE CONCAT('%', :keyword, '%')")
    public List<User> findByIdOrFirstNameOrLastNameOrEmailOrRoles(@Param("keyword") String keyword);
	
}
