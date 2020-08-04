package com.chun.jpademo.repository;

import com.chun.jpademo.model.Comment;
import com.chun.jpademo.model.CommentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaSpecificationExecutor<Comment>, JpaRepository<Comment, Long> {
    @Query("select u.email as email, u.userName as userName, c.word as word from User u ,Comment c where c.userId = u.id and c.userName = ?1")
    List<CommentInfo> findCommentInfoByName(String name);
}