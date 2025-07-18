package com.example.blog.service;

import com.example.blog.model.BlogPost;
import com.example.blog.model.SavedPost;
import com.example.blog.model.User;
import com.example.blog.repository.BlogPostRepository;
import com.example.blog.repository.SavedPostRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final SavedPostRepository savedPostRepository;
    private final UserService userService;

    public BlogService(BlogPostRepository blogPostRepository,
                       SavedPostRepository savedPostRepository,
                       UserService userService) {
        this.blogPostRepository = blogPostRepository;
        this.savedPostRepository = savedPostRepository;
        this.userService = userService;
    }

    // ✅ Save a new post with user info and preview
    public BlogPost savePostWithUser(BlogPost post, String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            post.setUser(user);
            post.setCreatedDate(LocalDateTime.now());
            post.setUpdatedDate(LocalDateTime.now());

            // ✅ Generate preview
            String cleanText = stripHtml(post.getContent());
            post.setPreview(cleanText.length() > 200 ? cleanText.substring(0, 200) + "..." : cleanText);

            return blogPostRepository.save(post);
        }
        return null;
    }

    public BlogPost savePost(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    public List<BlogPost> findAllPosts() {
        return blogPostRepository.findAll();
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id).orElse(null);
    }

    public Optional<BlogPost> findPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    public Optional<BlogPost> updatePost(Long id, BlogPost updatedPost) {
        return blogPostRepository.findById(id).map(existing -> {
            existing.setTitle(updatedPost.getTitle());
            existing.setContent(updatedPost.getContent());
            existing.setCategory(updatedPost.getCategory());
            existing.setUpdatedDate(LocalDateTime.now());

            // ✅ Regenerate preview on update
            String cleanText = stripHtml(updatedPost.getContent());
            existing.setPreview(cleanText.length() > 300 ? cleanText.substring(0, 300) + "..." : cleanText);

            return blogPostRepository.save(existing);
        });
    }

    public boolean deletePost(Long id) {
        Optional<BlogPost> existing = blogPostRepository.findById(id);
        if (existing.isPresent()) {
            blogPostRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void likePost(Long id) {
        BlogPost post = getPostById(id);
        if (post != null) {
            post.incrementLikes();
            blogPostRepository.save(post);
        }
    }

    public void savePostForUser(Long postId, String username) {
        BlogPost post = getPostById(postId);
        User user = userService.findByUsername(username);

        if (post != null && user != null) {
            boolean alreadySaved = savedPostRepository.existsByUserAndPost(user, post);
            if (!alreadySaved) {
                SavedPost savedPost = new SavedPost(user, post, LocalDateTime.now());
                savedPostRepository.save(savedPost);
            }
        }
    }

    public List<SavedPost> getSavedPostsForUser(User user) {
        return savedPostRepository.findByUser(user);
    }

    public List<SavedPost> findSavedPostsByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return savedPostRepository.findByUser(user);
        }
        return List.of();
    }

    public List<BlogPost> searchPostsByTitle(String keyword) {
        return blogPostRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<BlogPost> findPostsByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return blogPostRepository.findByUser(user);
        }
        return List.of();
    }

    public List<BlogPost> findPostsByCategory(String category) {
        return blogPostRepository.findByCategory(category);
    }

    public List<String> findAllCategories() {
        return blogPostRepository.findAll().stream()
                .map(BlogPost::getCategory)
                .filter(cat -> cat != null && !cat.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    // ✅ Utility to strip HTML tags
    private String stripHtml(String html) {
        return html == null ? "" : html.replaceAll("<[^>]*>", "").trim();
    }
}
