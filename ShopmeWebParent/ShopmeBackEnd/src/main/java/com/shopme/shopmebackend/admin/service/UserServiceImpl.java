package com.shopme.shopmebackend.admin.service;

import com.shopme.shopmebackend.admin.entity.Role;
import com.shopme.shopmebackend.admin.entity.User;
import com.shopme.shopmebackend.admin.exception.UserNotFoundException;
import com.shopme.shopmebackend.admin.repository.RoleRepository;
import com.shopme.shopmebackend.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserServiceImpl implements UserService{


    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
       return (List<User>) userRepository.findAll();
    }

    @Override
    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, USER_PER_PAGE, sort);
        if(keyword != null){
            return userRepository.findAll(keyword, pageable);
        }


        return userRepository.findAll(pageable);
    }

    @Override
    public boolean isEmailUnique(String email, Integer id) {
        User userByEmail = userRepository.findByEmail(email);

        if (userByEmail == null) {
            return true;
        }

        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (userByEmail != null) {
                return false;
            }
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    @Override
    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            user.setPassword(passwordEncoder.encode("p@ssw0rd"));
        }else{
            user.setPassword(passwordEncoder.encode("p@ssw0rd"));
        }

        return userRepository.save(user);
    }

    @Override
    public User findById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new UserNotFoundException("Could not find any user by id: " + id);
        }
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    @Override
    public User getByEmail(String email) {
        return null;
    }


}
