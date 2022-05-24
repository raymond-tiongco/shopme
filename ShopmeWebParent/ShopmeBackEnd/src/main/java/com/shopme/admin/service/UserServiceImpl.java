package com.shopme.admin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.dao.UserRepository;
import com.shopme.admin.entity.User;
import com.shopme.admin.exception.UserNotFoundException;

@Service
public class UserServiceImpl implements UserService {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findUsersWithSearch(String keyword) {
		return userRepository.findByIdOrFirstNameOrLastNameOrEmail(keyword);
	}

	@Override
	public List<User> findUsersWithSorting(String field, String sortDir) {
		Sort sort = Sort.by(field);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		return userRepository.findAll(sort);
	}

	@Override
	public Page<User> findUsersWithPagination(int offset, int pageSize) {
		Page<User> users = userRepository.findAll(PageRequest.of(offset, pageSize));
		
		return users;
	}

	@Override
	public Page<User> findUsersWithSortingAndPagination(String field, String sortDir, int offset, int pageSize) {
		Sort sort = Sort.by(field);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Page<User> users = userRepository.findAll(PageRequest.of(offset, pageSize, sort));
		
		return users;
	}

	@Override
	public Page<User> searchUsers(String field, String sortDir, int offset, int pageSize, String keyword) {
		Sort sort = Sort.by(field);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		List<User> usersList = userRepository.findByIdOrFirstNameOrLastNameOrEmail(keyword);
		
		Pageable paging = PageRequest.of(offset, pageSize);
		
		Page<User> users = new PageImpl<>(usersList, paging, usersList.size());
		
		return users;
	}

	@Override
	public User findById(int id) {
		Optional<User> result = userRepository.findById(id);
		User user = null;
		
		if(result.isPresent()) {
			user = result.get();
		}
		else {
			throw new UserNotFoundException("User with id: " + id + " not found.");
		}
		
		return user;
	}
	
	@Override
	public User findByEmail(String email) {
		User user = userRepository.getUserByEmail(email);
		
		return user;
	}

	@Override
	public User save(User user) {
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public void deleteById(int id) {
		userRepository.deleteById(id);
	}

	
}
