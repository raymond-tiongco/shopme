package com.shopme.admin.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.admin.dao.RoleRepo;
import com.shopme.admin.dao.UserRepo;
import com.shopme.admin.entity.Role;
import com.shopme.admin.entity.User;
import com.shopme.admin.utils.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.*;
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
    public User saveUser(User user, ArrayList<Integer> enabledList, ArrayList<Integer> roles,
                         MultipartFile photo, boolean isUpdate) throws IOException {

        Optional<User> optionalUser = Optional.ofNullable(user);

        if (optionalUser.isPresent() ) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Optional<ArrayList<Integer>> optionalRoles = Optional.ofNullable(roles);
            if (optionalRoles.isPresent()) {
                List<Role> roleList = optionalRoles.get().stream().filter(role -> role > 0)
                        .map(id -> roleService.findOne(id)).collect(Collectors.toList());
                user.getRoles().addAll(roleList);
            } else {
                throw new NullPointerException("Parameter \"roles\" of type ArrayList<Integer> is null");
            }

            Optional<MultipartFile> optionalMultipartFile = Optional.ofNullable(photo);
            if (optionalMultipartFile.isPresent()) {
                if (photo.getSize() > 0) {
                    user.setPhotos(photo.getBytes());
                }
            } else {
                if (isUpdate) {
                    user.setPhotos(findById(user.getId()).getPhotos());
                }
            }

            Optional<ArrayList<Integer>> enabledOptional = Optional.ofNullable(enabledList);
            if (enabledOptional.isPresent()) {
                Optional<Integer> enabled = enabledOptional.get().stream().filter(enable -> enable > 0).findFirst();
                user.setEnabled(enabled.orElse(0));
            } else {
                throw new NullPointerException("Parameter \"enabledLIst\" of type ArrayList<Integer> is null");
            }

            return userRepo.save(user);
        } else {
            throw new NullPointerException("Parameter \"user\" of type User is null");
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

    @Override
    public void deleteById(int userId) {
        userRepo.deleteById(userId);
    }

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
        Objects.requireNonNull(user, "User not found in the database");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

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
}