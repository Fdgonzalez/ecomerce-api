package com.cloud.ecomerce.user.repository;

import com.cloud.ecomerce.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query(value = "insert into user_roles (user_id,role_id) values (:userid, :roleid)",
            nativeQuery = true)
    void setRole(@Param("userid") Long uid, @Param("roleid") Integer roleid);
}
