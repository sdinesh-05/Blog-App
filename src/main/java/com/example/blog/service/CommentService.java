package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByPost(BlogPost post) {
        return commentRepository.findByPost(post);
    }

    public void addComment(BlogPost post, User user, String content) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        commentRepository.save(comment);
    }
}
