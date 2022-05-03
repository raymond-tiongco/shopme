package com.shopme.admin.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void saveRootUser(User rootUser);
    User saveUser(User user, ArrayList<Integer> enabled, ArrayList<Integer> roles, MultipartFile photo) throws IOException;
    void saveRole(String name, String description);
    void addRoleToUser(String username, String roleName);
    User findByEmail(String Email);
    List<User> getUsers();
    List<User> findAll();
    void deleteById(int userId);
    User findById(int userId);
    String getBase64(User user);
    byte[] getBytes(User user);
    void getImageAsStream(int id, HttpServletResponse response);
}
