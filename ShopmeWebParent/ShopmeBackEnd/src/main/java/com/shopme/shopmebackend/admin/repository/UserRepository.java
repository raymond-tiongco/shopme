package com.shopme.shopmebackend.admin.repository;

import com.shopme.shopmebackend.admin.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @Query(value = "SELECT u FROM User u WHERE u.email =:email")
    User findByEmail(@Param("email")String email);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles ur WHERE " +
            " CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName, ' ', ur.name) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);
}
