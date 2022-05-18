package com.shopme.admin.service;

import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.SearchRequest;
import com.shopme.admin.entity.User;
import com.shopme.admin.utils.Log;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo,
                           RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveRootUser(User rootUser) {
        rootUser.setPassword(passwordEncoder.encode(rootUser.getPassword()));
        userRepo.save(rootUser);
    }

    @Override
    public User saveUser(Optional<User> optionalUser, Optional<ArrayList<Integer>> optionalEnabled,
                         Optional<ArrayList<Integer>> optionalRoles, Optional<MultipartFile> optionalPhoto,
                         boolean isUpdate) throws IOException {

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            processRole(user, optionalRoles);
            processPhoto(user, optionalPhoto, isUpdate);
            processEnabled(user, optionalEnabled);

            return userRepo.save(user);
        } else {
            throw new NullPointerException("Parameter \"user\" of type User is null");
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
        if (optionalPhoto.isPresent()) {
            if (optionalPhoto.get().getSize() > 0) {
                user.setPhotos(optionalPhoto.get().getBytes());
            } else {
                if (isUpdate) {
                    user.setPhotos(findById(user.getId()).getPhotos());
                }
            }
        } else {
            if (isUpdate) {
                user.setPhotos(findById(user.getId()).getPhotos());
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
	public String getBase64(User user) {
		try {
			byte[] encodeBase64 = Base64.getEncoder().encode(user.getPhotos());
			return new String(encodeBase64, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "UnsupportedEncodingException when converting file to base64";
		}
	}

	@Override
	public byte[] getBytes(User user) {
		return user.getPhotos();
	}

	@Override
	public void getImageAsStream(int id, HttpServletResponse response) {
		response.setContentType("image/png");

    	try (InputStream inputStream = new ByteArrayInputStream(getBytes(findById(id)))) {
			IOUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception ignored) {}
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
    public Page<User> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 5);

        return userRepo.findAll(pageable);
    }

    @Override
    public Page<User> findUserWithSort(String field, String direction, int pageNumber) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(field).ascending() : Sort.by(field).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);

        return userRepo.findAll(pageable);
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> search(String keyword, SearchRequest searchRequest) {
        List<String> columns;

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> userCriteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = userCriteriaQuery.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        columns = searchRequest.getColumns();

        for (int i = 0; i < columns.size(); i++) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(userRoot.get(String.valueOf(columns.get(i)))
                            .as(String.class), "%"+keyword+"%")
            ));
        }

        userCriteriaQuery.select(userRoot).where(
                criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]))
        );

        List<User> resultList = entityManager.createQuery(userCriteriaQuery).getResultList();

        return resultList;
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