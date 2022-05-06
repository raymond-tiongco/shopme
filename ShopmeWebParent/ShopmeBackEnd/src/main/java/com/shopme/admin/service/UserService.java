package com.shopme.admin.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void saveRootUser(User rootUser);
    User saveUser(User user, ArrayList<Integer> enabled, ArrayList<Integer> roles, MultipartFile photo, boolean isUpdate) throws IOException;
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
    void enable(int userid);
    void disable(int userid);
    List<User> findByEmailLike(String email);

    Page<User> findPage(int pageNumber);

    Page<User> findUserWithSort(String field, String direction, int pageNumber);

    void exportToCsv(Writer writer);

    ByteArrayInputStream exportToExcel(List<User> users);

    void exportToPdf(HttpServletResponse response);
}
