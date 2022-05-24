package com.shopme.admin.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    void createUploadsFolder();

    Resource load(String filename);

    void deleteAllPhotos();

    void deleteAll();

    void saveUser(User rootUser);

    User saveUser(Optional<User> optionalUser, Optional<ArrayList<Integer>> optionalEnabled,
                  Optional<ArrayList<Integer>> optionalRoles, Optional<MultipartFile> optionalPhoto,
                  boolean isUpdate) throws IOException;

    void saveRole(int id, String name, String description);

    void addRoleToUser(String username, String roleName);

    User findByEmail(String Email);

    List<User> findAll();

    void deleteById(int userId);

    User findById(int userId);

    void displayFileFromFolder(int id, HttpServletResponse response) throws IOException;

    void enable(int userid);

    void disable(int userid);

    List<User> findByEmailLike(String email);

    Page<User> findPage(int pageNumber);

    Page<User> findUserWithSort(String field, String direction, int pageNumber);

    ArrayList<User> modifyList(ArrayList<User> users, String field, String direction);

    boolean isDuplicate(String email);

    boolean ownerOwnedEmail(String email, int id);

    List<User> search(String keyword, List<String> columns);
}
