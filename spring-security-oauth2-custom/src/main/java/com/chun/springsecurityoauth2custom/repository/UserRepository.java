package com.chun.springsecurityoauth2custom.repository;

import com.chun.springsecurityoauth2custom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
