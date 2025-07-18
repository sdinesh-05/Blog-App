package com.example.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 350)  // ✅ NEW: preview stored in DB
    private String preview;

    private String category;

    @Column(name = "image_url" , columnDefinition = "TEXT") // ✅ NEW: image URL stored in DB
    private String imageUrl; // ✅ Field for image path

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "likes")
    private int likes = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SavedPost> savedPosts = new ArrayList<>();

    public BlogPost() {}

    public BlogPost(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdDate = LocalDateTime.now();
    }

    // 🔽 Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public void incrementLikes() { this.likes++; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<SavedPost> getSavedPosts() { return savedPosts; }
    public void setSavedPosts(List<SavedPost> savedPosts) { this.savedPosts = savedPosts; }
}
