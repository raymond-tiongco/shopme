package com.shopme.admin.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    //  tested
    void saveRootUser(User rootUser);

    User saveUser(User user, ArrayList<Integer> enabled, ArrayList<Integer> roles, MultipartFile photo, boolean isUpdate) throws IOException;

    //  tested
    void saveRole(String name, String description);
    void addRoleToUser(String username, String roleName);

    //  tested
    User findByEmail(String Email);

    //  tested
    List<User> findAll();

    //  tested
    void deleteById(int userId);

    //  tested
    User findById(int userId);

    //  tested
    String getBase64(User user);

    byte[] getBytes(User user);

    void getImageAsStream(int id, HttpServletResponse response);

    //  tested
    void enable(int userid);

    //  tested
    void disable(int userid);

    //  tested
    List<User> findByEmailLike(String email);

    //  tested
    Page<User> findPage(int pageNumber);

    //  tested
    Page<User> findUserWithSort(String field, String direction, int pageNumber);

    void exportToCsv(Writer writer);
    ByteArrayInputStream exportToExcel(List<User> users);
    void exportToPdf(HttpServletResponse response);
}
