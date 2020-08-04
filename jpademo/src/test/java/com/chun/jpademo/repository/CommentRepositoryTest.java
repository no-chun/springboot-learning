package com.chun.jpademo.repository;

import com.chun.jpademo.model.Comment;
import com.chun.jpademo.model.CommentInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Test
    public void testComment() {
        commentRepository.save(new Comment(2L, "chun", "I like this!"));
        commentRepository.save(new Comment(2L, "chun", "Ha ha!"));
    }

    @Test
    public void testGetComment() {
        List<CommentInfo> comments = commentRepository.findCommentInfoByName("chun");
        for (CommentInfo comment : comments) {
            System.out.println(comment.getUserName() + ":" + comment.getWord());
        }
    }
}