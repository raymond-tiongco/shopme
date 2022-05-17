package com.shopme.admin;

import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import com.shopme.admin.utils.Log;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.Rollback;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Rollback(value = false)
public class UserServiceTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserDetailsService userDetailsService;

    @Test
    public void saveRootUserTest() {
        String newEmail = "darylldagondon@gmail.com";

        User root = new User()
                .email(newEmail)
                .enabled(1)
                .firstName("Daryll David")
                .lastName("Dagondon")
                .password("daryll123");

        userService.saveRootUser(root);

        userService.addRoleToUser(newEmail, Roles.Admin.name());

        User user = userService.findByEmail(newEmail);

        org.junit.jupiter.api.Assertions.assertEquals(newEmail, user.getEmail());
    }

    @Test   //  set spring.jpa.hibernate.ddl-auto=none before running this test
    public void testAddManyUsers() {

        userService.saveRole(Roles.Admin.name(), Roles.Admin.DESCRIPTION);
        userService.saveRole(Roles.Salesperson.name(), Roles.Salesperson.DESCRIPTION);
        userService.saveRole(Roles.Editor.name(), Roles.Editor.DESCRIPTION);
        userService.saveRole(Roles.Shipper.name(), Roles.Shipper.DESCRIPTION);
        userService.saveRole(Roles.Assistant.name(), Roles.Assistant.DESCRIPTION);

        ArrayList<Integer> roles =  new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        ArrayList<Integer> enabled = new ArrayList<>(Arrays.asList(1, 0));
        //ArrayList<Integer> enabled = new ArrayList<>(Arrays.asList(0));

        IntStream.range(1, 30).forEach(number -> {

            User newUser = new User()
                    .email("newuser"+number+"@gmail.com")
                    .enabled(1)
                    .firstName("User Firstname "+number)
                    .lastName("User Lastname "+number)
                    .password("newuser"+number+"@gmail.com");

            try {
                userService.saveUser(Optional.ofNullable(newUser), Optional.ofNullable(enabled),
                        Optional.ofNullable(roles), Optional.ofNullable(null), false);
            } catch (IOException e) {Log.error(e.toString());}
        });

        Assertions.assertThat(userService.findAll()).size().isGreaterThan(0);
    }

    @Test
    public void findUserByEmailTest() {
        String givenEmail = "darylldagondon@gmail.com";

        User user = userRepo.findByEmail(givenEmail);

        Assertions.assertThat(user.getEmail()).isEqualTo(givenEmail);
    }

    @Test
    public void testFindUserById() {
        int id = 4;

        org.junit.jupiter.api.Assertions.assertTrue(userRepo.findById(id).isPresent());
    }

    @Test
    public void testDeleteUserById() {
        int id = 3;

        userRepo.deleteById(id);

        org.junit.jupiter.api.Assertions.assertFalse(userRepo.findById(id).isPresent());
    }

    @Test
    public void testEnable() {
        int id = 1;

        User user = userService.findById(id);
        user.enable();
        user.setPassword("rootuser4567");
        userService.saveRootUser(user);

        org.junit.jupiter.api.Assertions.assertEquals(1, userService.findById(id).getEnabled());
    }

    @Test
    public void testDisable() {

        int id = 1;

        User user = userService.findById(id);
        user.disable();
        user.setPassword("rootuser4567");
        userService.saveRootUser(user);

        org.junit.jupiter.api.Assertions.assertEquals(0, userService.findById(id).getEnabled());
    }

    @Test
    public void testSearchEmailKeyword() {

        String keyword = "@yahoo";

        List<User> results = userService.findByEmailLike(keyword);

        Assertions.assertThat(results).size().isGreaterThan(0);
    }

    @Test
    public void testFindPage() {

        Page<User> userPage = userService.findPage(1);

        Assertions.assertThat(userPage).size().isGreaterThan(0);
    }

    @Test
    public void testGetAllUsers() {

        List<User> users = userService.findAll();

        Assertions.assertThat(users).size().isGreaterThan(0);
    }

    @Test
    public void getUsersSortedByPage() {

        Page<User> sortedUsers = userService.findUserWithSort("firstName", "desc", 3);

        sortedUsers.getContent().stream().forEach(user -> System.out.println(user.getFirstName()+","+user.getLastName()));

        Assertions.assertThat(sortedUsers).size().isGreaterThan(0);
    }

    @Test
    public void testGetBase64() {

        User user = userService.findById(21);

        String base64 = userService.getBase64(user);

        org.junit.jupiter.api.Assertions.assertFalse(base64.isEmpty());
    }

    @Test
    public void testGetBytes() {

        User user = userService.findById(27);

        byte[] bytes = userService.getBytes(user);

        org.junit.jupiter.api.Assertions.assertNull(bytes);
    }

    @Test
    public void testLoadUserByUsername() {
        String username = "newuser0@gmail.com";
        //String username = "darylldavid@gmail.com";

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        org.junit.jupiter.api.Assertions.assertNotNull(userDetails);
    }

    @Test
    public void testEmailDuplicate() {
        String email = "newuser1@gmail.com";
        org.junit.jupiter.api.Assertions.assertTrue(userService.isDuplicate(email));
    }

    @Test
    public void testIfOwnerOwnsTheEmail() {
        String email = "newuser1@gmail.com";
        int id = 1;

        org.junit.jupiter.api.Assertions.assertTrue(userService.ownerOwnedEmail(email, id));
    }

    @Test
    public void testAddRoleToUser() {
        String email = "rodrigoduterte@gmail.com";
        String role = Roles.Salesperson.name();

        org.junit.jupiter.api.Assertions.assertNotNull(userService.findByEmail(email));

        userService.addRoleToUser(email, role);

        User user = userService.findByEmail(email);
        Role checkRole = roleService.findOne(2);

        Set<String> set = user.getRoles().stream().map(eachRole -> eachRole.getName()).collect(Collectors.toSet());

        Assertions.assertThat(set).contains(checkRole.getName());
    }
}