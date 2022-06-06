package com.shopme.shopmebackend.admin.service;

import com.shopme.shopmebackend.admin.entity.Role;
import com.shopme.shopmebackend.admin.entity.User;
import com.shopme.shopmebackend.admin.exception.UserNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    public static final int USER_PER_PAGE = 3;
    public List<User> listUsers();
    public List<Role> listRoles();
    Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword);
    boolean isEmailUnique(String email, Integer id);
    public User save(User user);
    public User findById(Integer id) throws UserNotFoundException;
    public void deleteById(Integer id);
    void updateUserEnabledStatus(Integer id, boolean enabled);
    User getByEmail(String email);


}
