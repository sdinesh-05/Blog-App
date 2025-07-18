package com.example.blog.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Posts authored by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlogPost> posts = new HashSet<>();

    // ✅ Saved/Bookmarked posts by this user
    @ManyToMany
    @JoinTable(
        name = "user_saved_posts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<BlogPost> savedPosts = new HashSet<>();

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<BlogPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<BlogPost> posts) {
        this.posts = posts;
    }

    public Set<BlogPost> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(Set<BlogPost> savedPosts) {
        this.savedPosts = savedPosts;
    }
}
