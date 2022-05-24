package com.shopme.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.shopme.admin.entity.User;

public interface UserService {
	public List<User> findAll();
	public List<User> findUsersWithSorting(String field, String sortDir);
	public List<User> findUsersWithSearch(String keyword);
	public Page<User> findUsersWithPagination(int offset, int pageSize);
	public Page<User> findUsersWithSortingAndPagination(String field, String sortDir, int offset, int pageSize);
	public Page<User> searchUsers(String field, String sortDir, int offset, int pageSize, String keyword);
	public User findById(int id);
	public User findByEmail(String email);
	public User save(User user);
	public void deleteById(int id);
}
