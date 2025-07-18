package com.example.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SavedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime savedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private BlogPost post;

    public SavedPost() {}

    public SavedPost(User user, BlogPost post, LocalDateTime savedAt) {
        this.user = user;
        this.post = post;
        this.savedAt = savedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BlogPost getPost() { return post; }
    public void setPost(BlogPost post) { this.post = post; }
}
