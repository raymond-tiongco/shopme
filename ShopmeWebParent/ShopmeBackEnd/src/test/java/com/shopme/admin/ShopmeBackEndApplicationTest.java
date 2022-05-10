package com.shopme.admin;

import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class ShopmeBackEndApplicationTest {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Test
    @Rollback(value = false)
    public void testSaveAllRoles() {

        userService.saveRole(Roles.Admin.name(), "Manage everything");
        userService.saveRole(Roles.Salesperson.name(), "Manage product price, customers, shipping, orders and sales report");
        userService.saveRole(Roles.Editor.name(), "Manage categories, brands, products, articles and menus");
        userService.saveRole(Roles.Shipper.name(), "View products, view orders and update order status");
        userService.saveRole(Roles.Assistant.name(), "Manage product price, customers, shipping, orders and sales report");

        org.assertj.core.api.Assertions.assertThat(roleService.findAll()).size().isGreaterThan(4);
    }

    @Test
    @Rollback(value = false)
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
    @Rollback(value = false)
    public void testEnable() {

        int id = 4;

        User user = userService.findById(4);
        user.enable();

        org.junit.jupiter.api.Assertions.assertEquals(1, user.getEnabled());
    }

    @Test
    @Rollback(value = false)
    public void testDisable() {

        int id = 4;

        User user = userService.findById(4);
        user.disable();

        System.out.println(user);

        org.junit.jupiter.api.Assertions.assertEquals(0, user.getEnabled());
    }

    /*
    @Test
	@Rollback(false)
	public void testCreateProduct() {
		Product product = new Product("iPhone 13", 789);
		Product savedProduct = repo.save(product);

		Assertions.assertNotNull(savedProduct);
	}

	@Test
	public void testFindProductByNameExist() {
		String name = "iPhone 10";
		Product product = repo.findByName(name);

		org.assertj.core.api.Assertions.assertThat(product.getName()).isEqualTo(name);
	}

	@Test
	public void testFindProductByNameNotExist() {
		String name = "iPhone 11";
		Product product = repo.findByName(name);

		Assertions.assertNull(product);
	}

	@Test
	@Rollback(value = false)
	public void testUpdateProduct() {

		Product updateProduct = repo.findByName("iPhone 12");
		updateProduct.setName("Daryll Gwapo");
		updateProduct.setPrice(999);
		repo.save(updateProduct);

		Product updatedProduct = repo.findByName("Daryll Gwapo");
		org.assertj.core.api.Assertions.assertThat(updatedProduct.getName()).isEqualTo("Daryll Gwapo");
	}

	@Test
	public void testListProducts() {
		List<Product> products = repo.findAll();

		for (Product product : products) {
			System.out.println(product);
		}

		org.assertj.core.api.Assertions.assertThat(products).size().isGreaterThan(0);
	}

	@Test
	@Rollback(value = false)
	public void testDeleteProduct() {
		Integer id = 1;

		boolean isExistBeforeDelete = repo.findById(id).isPresent();

		repo.deleteById(id);

		boolean notExistAfterDelete = repo.findById(id).isPresent();

		Assertions.assertTrue(isExistBeforeDelete);
		Assertions.assertFalse(notExistAfterDelete);
	}
     */
}