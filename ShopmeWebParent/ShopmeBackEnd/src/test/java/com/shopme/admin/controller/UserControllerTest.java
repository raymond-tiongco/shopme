package com.shopme.admin.controller;

import com.shopme.admin.config.ShopmeBackendSecurityConfig;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.Roles;
import com.shopme.admin.entity.User;
import com.shopme.admin.service.RoleService;
import com.shopme.admin.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = {UserController.class, ShopmeBackendSecurityConfig.class, UserRestController.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testRoot() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").with(csrf())).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testUsersRoot() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/Users").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testUsersPage() throws Exception {
        int page = 2;

        mockMvc.perform(MockMvcRequestBuilders.get("/Users/"+page).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testUsersPageWithDirection() throws Exception {
        int page = 2;

        mockMvc.perform(MockMvcRequestBuilders.get("/Users/"+page+"/id").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoadAddUserForm() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/AddUserForm").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoadUpdateUserForm() throws Exception {
        int userId = 5;

        User user = new User().id(5).email("newuser5@gmail.com").enabled(0)
                .firstName("User Firstname 5").lastName("User Lastname 5");

        Mockito.when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/UpdateUserForm")
                        .param("userId", String.valueOf(userId)).with(csrf()))
                        .andExpect(status().isOk());
    }

    @Test
    public void testEnable() throws Exception {
        int userid = 1;
        int page = 1;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/Enable")
                .param("userid", String.valueOf(userid))
                .param("page", String.valueOf(page)).with(csrf())).andExpect(status().isOk()).andReturn();

        String msg = mvcResult.getModelAndView().getModel().get("alertMessage").toString();

        org.assertj.core.api.Assertions.assertThat(msg).isEqualTo("Successfully enabled User ID "+userid);
    }

    @Test
    public void testDisable() throws Exception {
        int userid = 1;
        int page = 1;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/Disable")
                .param("userid", String.valueOf(userid))
                .param("page", String.valueOf(page)).with(csrf())).andExpect(status().isOk()).andReturn();

        String msg = mvcResult.getModelAndView().getModel().get("alertMessage").toString();

        org.assertj.core.api.Assertions.assertThat(msg).isEqualTo("Successfully disabled User ID "+userid);
    }

    @Test
    public void testDelete() throws Exception {
        int userId = 1;

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/DeleteUser")
                        .param("userId", String.valueOf(userId)).with(csrf())).andExpect(status().isOk()).andReturn();

        //System.out.println(mvcResult.getResponse().getContentAsString());
        //System.out.println(mvcResult.getModelAndView().getModel().get("alertMessage"));

        String msg = mvcResult.getModelAndView().getModel().get("alertMessage").toString();

        org.assertj.core.api.Assertions.assertThat(msg).isEqualTo("A user with an id of "+userId+" has been deleted.");
    }

    @Test
    public void testSearch() throws Exception {

        String emailKeyword = "user2@gmail";

        List<User> expectedUsers = Arrays.asList(new User().id(5).email("newuser5@gmail.com").enabled(1)
                        .firstName("User Firstname 5").lastName("User Lastname 5"));

        Mockito.when(userService.findByEmailLike(emailKeyword)).thenReturn(expectedUsers);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/Search")
                .param("keyword", emailKeyword).with(csrf())).andReturn();

        Object object = mvcResult.getModelAndView().getModel().get("users");

        Assertions.assertNotNull(object);

        List<User> returnedUsers = (List<User>) object;

        org.assertj.core.api.Assertions.assertThat(returnedUsers.get(0).getEmail())
                        .isEqualTo(expectedUsers.get(0).getEmail());

    }

    @Test
    public void testCheckDuplicateEmail() throws Exception {

        String email = "newuser0@gmail.com";

        Mockito.when(userService.isDuplicate(email)).thenReturn(true);

        String url = "/CheckDuplicateEmail";

        mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .param("email", email).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(email+" exists"));
                //.andExpect(content().string(email+" does not exist"));
    }

    @Test
    public void testDownloadExcel() throws Exception {
        //  comment line 40 from ShopmeBackendSecurityConfig of the antMatchers to
        //  disable login requirement when testing exporting

        List<User> users = generate();

        Mockito.when(userService.findAll()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/ExcelExport"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
        Path path = Paths.get("users.xlsx");
        Files.write(path, bytes);
    }

    @Test
    public void testDownloadCsv() throws Exception {
        //  comment line 40 from ShopmeBackendSecurityConfig of the antMatchers to
        //  disable login requirement when testing exporting

        List<User> users = generate();

        Mockito.when(userService.findAll()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/CsvExport"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
        Path path = Paths.get("users.csv");
        Files.write(path, bytes);
    }

    @Test
    public void testDownloadPdf() throws Exception {
        //  comment line 40 from ShopmeBackendSecurityConfig of the antMatchers to
        //  disable login requirement when testing exporting

        List<User> users = generate();

        Mockito.when(userService.findAll()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/PdfExport"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
        Path path = Paths.get("users.pdf");
        Files.write(path, bytes);
    }

    @Test
    public void testGetImage() throws Exception {
        //  comment line 40 from ShopmeBackendSecurityConfig of the antMatchers to
        //  disable login requirement when testing exporting

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/GetPhoto/1"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();
        Path path = Paths.get("user.png");
        Files.write(path, bytes);
    }

    public List<User> generate() {
        List<User> users = new ArrayList<>();

        users.add(new User().id(1).email("superuser1@gmail.com").enabled(1)
                .firstName("RootUser1").lastName("RootUser1").password("rootuser4567")
                .addRole(new Role(Roles.Admin.name(), "The Admin")));
        users.add(new User().id(2).email("superuser2@gmail.com").enabled(1)
                .firstName("RootUser2").lastName("RootUser2").password("rootuser4567")
                .addRole(new Role(Roles.Salesperson.name(), "The Salesperson")));
        users.add(new User().id(3).email("superuser3@gmail.com").enabled(1)
                .firstName("RootUser3").lastName("RootUser3").password("rootuser4567")
                .addRole(new Role(Roles.Shipper.name(), "The Shipper")));
        users.add(new User().id(4).email("superuser4@gmail.com").enabled(1)
                .firstName("RootUser4").lastName("RootUser4").password("rootuser4567")
                .addRole(new Role(Roles.Admin.name(), "The Admin")));
        users.add(new User().id(5).email("superuser5@gmail.com").enabled(1)
                .firstName("RootUser5").lastName("RootUser5").password("rootuser4567")
                .addRole(new Role(Roles.Salesperson.name(), "The Salesperson")));
        users.add(new User().id(6).email("superuser6@gmail.com").enabled(1)
                .firstName("RootUser6").lastName("RootUser6").password("rootuser4567")
                .addRole(new Role(Roles.Shipper.name(), "The Shipper")));
        users.add(new User().id(7).email("superuser1@gmail.com").enabled(1)
                .firstName("RootUser7").lastName("RootUser7").password("rootuser4567")
                .addRole(new Role(Roles.Admin.name(), "The Admin")));
        users.add(new User().id(8).email("superuser8@gmail.com").enabled(1)
                .firstName("RootUser8").lastName("RootUser8").password("rootuser4567")
                .addRole(new Role(Roles.Salesperson.name(), "The Salesperson")));
        users.add(new User().id(9).email("superuser9@gmail.com").enabled(1)
                .firstName("RootUser9").lastName("RootUser9").password("rootuser4567")
                .addRole(new Role(Roles.Shipper.name(), "The Shipper")));

        return users;
    }
}