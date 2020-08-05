package com.chun.jpamult.repository.primaryRepository;

import com.chun.jpamult.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrimaryUserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    User findByUserName(String username);

    User findByUserNameOrEmail(String username, String email);
}
