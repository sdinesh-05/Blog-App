package com.example.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.blog.model.BlogPost;
import com.example.blog.model.User;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByUser(User user);
    List<BlogPost> findByTitleContainingIgnoreCase(String keyword);
    List<BlogPost> findByCategory(String category);
}