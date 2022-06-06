package com.shopme.admin.service;

import com.shopme.admin.domain.UserSpecification;
import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import com.shopme.admin.utils.Log;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader;
    private final Path root = Paths.get("ShopmeWebParent/ShopmeBackEnd/uploads");
    private final UserSpecification userSpecification;

    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo,
                           RoleService roleService, PasswordEncoder passwordEncoder, ResourceLoader resourceLoader,
                           UserSpecification userSpecification) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.resourceLoader = resourceLoader;
        this.userSpecification = userSpecification;
    }

    @Override
    public void saveUser(User rootUser) {
        rootUser.setPassword(passwordEncoder.encode(rootUser.getPassword()));
        userRepo.save(rootUser);
    }

    @Override
    public void createUploadsFolder() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root);
                Log.info("No uploads folder. Creating uploads folder.");
            }
        } catch (IOException e) {
            Log.error(e.toString());
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public Resource getResource(String filename) {
        return resourceLoader.getResource("classpath:/static/images/"+filename);
    }

    @Override
    public void displayFileFromFolder(int id, HttpServletResponse response) throws IOException {
        Optional<User> userOptional = userRepo.findById(id);

        if (userOptional.isPresent()) {
            Path path = getResource("default.png").getFile().toPath();

            if (userOptional.get().getFilename() != null) {

                path = root.resolve(userOptional.get().getFilename().isEmpty()
                        ? "default.png" : userOptional.get().getFilename());

                if (!path.toFile().exists()) {
                    path = getResource("default.png").getFile().toPath();
                }
            }

            response.setContentType(Files.probeContentType(path));

            try (InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(path))) {
                IOUtils.copy(inputStream, response.getOutputStream());
            } catch (NoSuchFileException ignored) {}

        } else {
            Log.error("Fetching userid "+id+" returned null.");
        }
    }

    @Override
    public void deleteAll() {
        userRepo.deleteAll();
    }

    @Override
    public User saveUser(Optional<User> optionalUser,
                         Optional<ArrayList<Integer>> optionalEnabled,
                         Optional<ArrayList<Integer>> optionalRoles,
                         Optional<MultipartFile> optionalPhoto,
                         boolean isUpdate) throws IOException {

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            processUserPassword(user, isUpdate);
            processRole(user, optionalRoles);
            processPhoto(user, optionalPhoto, isUpdate);
            processEnabled(user, optionalEnabled);

            return userRepo.save(user);
        } else {
            throw new NullPointerException("Parameter \"user\" of type User is null");
        }
    }

    private void processUserPassword(User user, boolean update) {

        if (!user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            if (update) {

                String existingPassword = userRepo.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("Cannot find userid "+user.getId()))
                        .getPassword();

                user.setPassword(existingPassword);
            }
        }
    }

    private void processRole(User user, Optional<ArrayList<Integer>> optionalRoles) {
        if (optionalRoles.isPresent()) {
            List<Role> roleList = optionalRoles.get().stream()
                    .filter(role -> role > 0)
                    .map(id -> roleService.findOne(id))
                    .collect(Collectors.toList());
            user.getRoles().addAll(roleList);
        } else {
            throw new NullPointerException("Parameter \"roles\" of type ArrayList<Integer> is null");
        }
    }

    private void processEnabled(User user, Optional<ArrayList<Integer>> optionalEnabled) {
        if (optionalEnabled.isPresent()) {
            Optional<Integer> enabled = optionalEnabled.get().stream().filter(enable -> enable > 0).findFirst();
            user.setEnabled(enabled.orElse(0));
        } else {
            throw new NullPointerException("Parameter \"enabledLIst\" of type ArrayList<Integer> is null");
        }
    }

    private void processPhoto(User user, Optional<MultipartFile> optionalPhoto, boolean isUpdate) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (optionalPhoto.isPresent()) {
            MultipartFile file = optionalPhoto.get();

            if (file.getSize() > 0) {
                Path path = this.root.resolve(timestamp.getTime()+"-"+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path);
                user.setFilename(timestamp.getTime()+"-"+file.getOriginalFilename());
            } else {
                if (isUpdate) {
                    user.setFilename(user.getFilename());
                }
            }
        } else {
            if (isUpdate) {
                user.setFilename(user.getFilename());
            }
        }
    }

    @Override
    public void saveRole(String name, String description) {
        roleRepo.save(new Role(name, description));
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        User user = userRepo.findByEmail(email);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override public void deleteById(int userId) {userRepo.deleteById(userId);}

    @Override
    public User findById(int userId) {
    	return userRepo.findById(userId).orElseThrow((() -> new RuntimeException("did not find user id - "+userId)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);

        if (user == null) {
            Log.error("User returned is null. Cannot login.");
            throw new UsernameNotFoundException(username);
        }

        if (!user.isEnabled()) {
            Log.error("User is disabled. Enable the user to login.");
            throw new UsernameNotFoundException("User is disabled. Enable the user to login.");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        Log.info(username+" has logged in");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public void enable(int userid) {
        Optional<User> optionalUser = Optional.ofNullable(
                userRepo.findById(userid)
                        .orElseThrow(() -> new RuntimeException("Cannot find user with id of "+userid)));

        optionalUser.ifPresent(user -> user.setEnabled(1));
    }

    @Override
    public void disable(int userid) {
        Optional<User> optionalUser = Optional.ofNullable(
                userRepo.findById(userid)
                        .orElseThrow(() -> new RuntimeException("Cannot find user with id of "+userid)));

        optionalUser.ifPresent(user -> user.setEnabled(0));
    }

    @Override
    public List<User> findByEmailLike(String email) {
        return userRepo.searchByEmailLike(email);
    }

    @Override
    public List<User> findByFirstnameLike(String firstname) {
        return userRepo.searchByFirstnameLike(firstname);
    }

    @Override
    public List<User> findByLastnameLike(String lastname) {
        return userRepo.searchByLastnameLike(lastname);
    }

    @Override
    public ArrayList<User> modifyList(ArrayList<User> users, String field, String direction) {
        users.sort((User user1, User user2) -> {

            try {
                Field field1 = user1.getClass().getDeclaredField(field);
                field1.setAccessible(true);
                Object object1 = field1.get(user1);

                Field field2 = user2.getClass().getDeclaredField(field);
                field2.setAccessible(true);
                Object object2 = field2.get(user2);

                int result = 0;

                if (isInt(object1.toString())) {
                    result = Integer.parseInt(object1.toString()) - Integer.parseInt(object2.toString());
                } else {
                    result = object1.toString().compareToIgnoreCase(object2.toString());
                }

                if (result > 0) {
                    return direction.equalsIgnoreCase("asc") ? 1 : -1;
                }

                if (result < 0) {
                    return direction.equalsIgnoreCase("asc") ? -1 : 1;
                }

                return 0;

            } catch (Exception e) {
                Log.error(e.toString());
                return 0;
            }
        });

        return users;
    }

    @Override
    public boolean isDuplicate(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public boolean ownerOwnedEmail(String email, int id) {
        User user = findByEmail(email);

        if (user != null) {
            return user.getId() == id;
        } else {
            return false;
        }
    }

    @Override
    public Page<User> findPageByKeyword(String keyword, int pageNo) {

        Page<User> page = userRepo.findAll(
                userSpecification.emailSpec(keyword)
                        .or(userSpecification.idSpec(keyword))
                        .or(userSpecification.fnameSpec(keyword))
                        .or(userSpecification.lnameSpec(keyword)),
                PageRequest.of(pageNo-1, 10));

        return page;
    }

    @Override
    public List<User> search(String keyword) {
        return userRepo.findAll(
                userSpecification.emailSpec(keyword)
                        .or(userSpecification.idSpec(keyword))
                        .or(userSpecification.fnameSpec(keyword))
                        .or(userSpecification.lnameSpec(keyword)));
    }

    @Override
    public Page<User> findUserWithSort(String field, String direction, int pageNumber) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(field).ascending()
                : Sort.by(field).descending();

        return userRepo.findAll(PageRequest.of(pageNumber - 1, 10, sort));
    }

    @Override
    public Page<User> findPage(int pageNumber) {
        return userRepo.findAll(PageRequest.of(pageNumber - 1, 10, Sort.by("id").descending()));
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}