package com.chun.jpademo.repository;

import com.chun.jpademo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Transactional(timeout = 10)
    @Modifying
    @Query("update User set userName = ?1 where id = ?2")
    void updateUserNameById(String username, Long id);

    @Transactional
    @Modifying
    @Query("delete from User where id = ?1")
    void deleteById();

    @Query("select u from User u")
    Page<User> findAll(Pageable pageable);

}
