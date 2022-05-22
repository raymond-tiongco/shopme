package com.shopme.admin.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.admin.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT u FROM User u WHERE lower(u.firstName) like lower(concat('%', :keyword, '%')) OR lower(u.lastName) like lower(concat('%', :keyword, '%'))")
    public List<User> findByFirstNameOrLastNameOrEmail(@Param("keyword") String keyword);
}
