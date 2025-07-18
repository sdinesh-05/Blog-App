package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.service.BlogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // ✅ Get all posts
    @GetMapping("/posts")
    public ResponseEntity<List<BlogPost>> getAllPosts() {
        List<BlogPost> posts = blogService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    // ✅ Get post by ID
    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long id) {
        Optional<BlogPost> post = blogService.findPostById(id);
        return post.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Create a new post
    @PostMapping("/posts")
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost blogPost) {
        // Check if the user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : null;

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Set creation and update timestamps
        blogPost.setCreatedDate(LocalDateTime.now());
        blogPost.setUpdatedDate(LocalDateTime.now());

        // Extract the first image URL from the content if not set
        if (blogPost.getImageUrl() == null || blogPost.getImageUrl().isEmpty()) {
            String firstImageUrl = extractFirstImageUrl(blogPost.getContent());
            if (firstImageUrl != null) {
                blogPost.setImageUrl(firstImageUrl);
            }
        }

        // ✅ Generate preview text (plain text max 200 chars)
        String cleanText = stripHtml(blogPost.getContent());
        blogPost.setPreview(cleanText.length() > 200 ? cleanText.substring(0, 200) + "..." : cleanText);

        // Save with user
        BlogPost savedPost = blogService.savePostWithUser(blogPost, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    // ✅ Update post
    @PutMapping("/posts/{id}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @RequestBody BlogPost updatedPost) {
        return blogService.updatePost(id, updatedPost)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete post
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean deleted = blogService.deletePost(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ✅ Get posts by category
    @GetMapping("/posts/category/{category}")
    public ResponseEntity<List<BlogPost>> getPostsByCategory(@PathVariable String category) {
        List<BlogPost> posts = blogService.findPostsByCategory(category);
        return ResponseEntity.ok(posts);
    }

    // ✅ Search posts by title
    @GetMapping("/posts/search")
    public ResponseEntity<List<BlogPost>> searchByTitle(@RequestParam("keyword") String keyword) {
        List<BlogPost> results = blogService.searchPostsByTitle(keyword);
        return ResponseEntity.ok(results);
    }

    // ✅ Utility: Extract first image URL from HTML
    private String extractFirstImageUrl(String content) {
        if (content == null) return null;
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // ✅ Utility: Strip HTML tags for preview generation
    private String stripHtml(String html) {
        return html == null ? "" : html.replaceAll("<[^>]*>", "").trim();
    }
}
