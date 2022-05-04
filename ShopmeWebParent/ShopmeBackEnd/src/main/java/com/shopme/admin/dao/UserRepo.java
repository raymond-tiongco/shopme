package com.shopme.admin.dao;

import com.shopme.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    List<User> searchByEmailLike(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:firstname%")
    List<User> searchByFirstnameLike(@Param("firstname") String firstname);

    @Query("SELECT u FROM User u WHERE u.lastName LIKE %:lastname%")
    List<User> searchByLastnameLike(@Param("lastname") String lastname);
}
