package com.shopme.admin.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    //  tested
    void saveRootUser(User rootUser);

    User saveUser(Optional<User> optionalUser, Optional<ArrayList<Integer>> optionalEnabled,
                  Optional<ArrayList<Integer>> optionalRoles, Optional<MultipartFile> optionalPhoto,
                  boolean isUpdate) throws IOException;

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

    ArrayList<User> modifyList(ArrayList<User> users, String field, String direction);

    //  tested
    boolean isDuplicate(String email);

    //  test
    boolean ownerOwnedEmail(String email, int id);
}
