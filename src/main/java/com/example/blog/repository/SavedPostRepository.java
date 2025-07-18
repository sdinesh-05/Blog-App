package com.example.blog.repository;

import com.example.blog.model.BlogPost;
import com.example.blog.model.SavedPost;
import com.example.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    Optional<SavedPost> findByUserAndPost(User user, BlogPost post);

    List<SavedPost> findByUser(User user);

    // 👇 ADD THIS METHOD to fix your error
    boolean existsByUserAndPost(User user, BlogPost post);
}
