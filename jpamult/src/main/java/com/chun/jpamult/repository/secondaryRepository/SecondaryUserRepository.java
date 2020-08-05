package com.chun.jpamult.repository.secondaryRepository;

import com.chun.jpamult.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecondaryUserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    User findByUserName(String username);

    User findByUserNameOrEmail(String username, String email);
}
