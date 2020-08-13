package com.chun.springsecurityjpa.repository;

import com.chun.springsecurityjpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
