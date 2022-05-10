package com.shopme.admin;

import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import com.shopme.admin.service.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

//@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Rollback(value = false)
public class UserServiceTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    UserService userService;

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

        int id = 4;

        User user = userService.findById(4);
        user.enable();
        userService.saveRootUser(user);

        org.junit.jupiter.api.Assertions.assertEquals(1, userService.findById(4).getEnabled());
    }

    @Test
    public void testDisable() {

        int id = 4;

        User user = userService.findById(4);
        user.disable();
        userService.saveRootUser(user);

        org.junit.jupiter.api.Assertions.assertEquals(0, userService.findById(4).getEnabled());
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
}