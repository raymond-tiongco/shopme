package com.shopme.shopmebackend.admin.repository;

import com.shopme.shopmebackend.admin.entity.Role;
;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole() {
        List<Role> roleList = new ArrayList<>();

        Role roleAdmin = new Role("Admin", "manage everything");
        roleList.add(roleAdmin);
        Role roleEditor = new Role("Editor","Manage Categories, Brands, Products, Articles and Menus");
        roleList.add(roleEditor);

        roleRepository.saveAll(roleList);

    }


}
