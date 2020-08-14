package com.chun.springsecurityrememberme.repository;

import com.chun.springsecurityrememberme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
