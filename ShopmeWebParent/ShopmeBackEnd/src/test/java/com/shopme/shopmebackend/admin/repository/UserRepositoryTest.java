package com.shopme.shopmebackend.admin.repository;

import com.shopme.shopmebackend.admin.entity.Role;
import com.shopme.shopmebackend.admin.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    private UserRepository userRepository;
    private EntityManager entityManager;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository,EntityManager entityManager){
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateNewUserWithRole(){
        BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();
        Role admin = entityManager.find(Role.class,3);
        String password = bCryptPasswordEncoder.encode("p@ssword");
        User user = new User("abc.test@gmail.com",password,"Mark","Stan");
        user.addRole(admin);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);

    }
}
