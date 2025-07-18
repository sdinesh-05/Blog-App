package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.model.SavedPost;
import com.example.blog.model.User;
import com.example.blog.repository.SavedPostRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedPostService {

    private final SavedPostRepository savedPostRepository;

    public SavedPostService(SavedPostRepository savedPostRepository) {
        this.savedPostRepository = savedPostRepository;
    }

    // ✅ Save (bookmark) a post if not already saved
    public void savePost(User user, BlogPost post) {
        Optional<SavedPost> existing = savedPostRepository.findByUserAndPost(user, post);
        if (existing.isEmpty()) {
            SavedPost savedPost = new SavedPost(user, post, LocalDateTime.now());
            savedPostRepository.save(savedPost);
        }
    }

    // ✅ Get all saved posts for a user
    public List<SavedPost> getSavedPosts(User user) {
        return savedPostRepository.findByUser(user);
    }

    // ✅ Unsave post
    public void unsavePost(User user, BlogPost post) {
        Optional<SavedPost> existing = savedPostRepository.findByUserAndPost(user, post);
        existing.ifPresent(savedPostRepository::delete);
    }

    // ✅ Check if a post is saved
    public boolean isPostSaved(User user, BlogPost post) {
        return savedPostRepository.findByUserAndPost(user, post).isPresent();
    }

    // ✅ NEW: Get list of saved post IDs
    public List<Long> getSavedPostIdsByUser(User user) {
        return savedPostRepository.findByUser(user)
                .stream()
                .map(saved -> saved.getPost().getId())
                .collect(Collectors.toList());
    }
}
