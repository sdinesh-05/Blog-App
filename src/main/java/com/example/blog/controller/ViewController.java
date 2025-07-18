package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.model.SavedPost;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.SavedPostService;
import com.example.blog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private SavedPostService savedPostService;

    // 🏠 Home Page
    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<BlogPost> posts = blogService.findAllPosts();
        List<String> categories = blogService.findAllCategories();

        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);
        model.addAttribute("newPost", new BlogPost());

        String currentUsername = getCurrentUsername();
        model.addAttribute("currentUsername", currentUsername);

        if (currentUsername != null) {
            User user = userService.findByUsername(currentUsername);
            List<Long> savedPostIds = savedPostService.getSavedPostIdsByUser(user);
            model.addAttribute("savedPostIds", savedPostIds);
        }

        return "index";
    }

    // 📂 View posts by category
    @GetMapping("/category/{name}")
    public String viewPostsByCategory(@PathVariable("name") String category, Model model) {
        List<BlogPost> posts = blogService.findPostsByCategory(category);
        List<String> categories = blogService.findAllCategories();

        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("newPost", new BlogPost());

        String currentUsername = getCurrentUsername();
        model.addAttribute("currentUsername", currentUsername);

        if (currentUsername != null) {
            User user = userService.findByUsername(currentUsername);
            List<Long> savedPostIds = savedPostService.getSavedPostIdsByUser(user);
            model.addAttribute("savedPostIds", savedPostIds);
        }

        return "index";
    }

    // 📄 View post details
    @GetMapping("/posts/{id}")
    public String viewPostDetails(@PathVariable Long id, Model model) {
        BlogPost post = blogService.getPostById(id);
        if (post == null) {
            return "redirect:/";
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", post.getComments());

        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            User user = userService.findByUsername(currentUsername);
            model.addAttribute("user", user);
        }

        return "postdetails";
    }

    // ✏️ Create post form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("newPost", new BlogPost());
        return "create-post";
    }

    // ✅ Create post action
    @PostMapping("/posts")
    public String createPost(@ModelAttribute("newPost") BlogPost blogPost) {
        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            // ✅ Auto-extract image from TinyMCE content
            String content = blogPost.getContent();
            String firstImageUrl = extractFirstImageUrl(content);
            if (firstImageUrl != null) {
                blogPost.setImageUrl(firstImageUrl);
            }

            blogService.savePostWithUser(blogPost, currentUsername);
        }
        return "redirect:/";
    }

    // ✏️ Edit post form
    @GetMapping("/posts/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BlogPost post = blogService.getPostById(id);
        String currentUsername = getCurrentUsername();

        if (post == null || !post.getUser().getUsername().equals(currentUsername)) {
            return "redirect:/profile";
        }

        model.addAttribute("post", post);
        return "edit-post";
    }

    // 🔁 Update post
    @PostMapping("/posts/update")
    public String updatePost(@ModelAttribute("post") BlogPost post) {
        blogService.updatePost(post.getId(), post);
        return "redirect:/profile";
    }

    // ❌ Delete post
    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        String currentUsername = getCurrentUsername();
        BlogPost post = blogService.getPostById(id);
        if (post != null && post.getUser().getUsername().equals(currentUsername)) {
            blogService.deletePost(id);
        }
        return "redirect:/profile";
    }

    // 👤 Profile
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(currentUsername);
        List<BlogPost> posts = blogService.findPostsByUsername(currentUsername);
        List<SavedPost> savedPosts = blogService.getSavedPostsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("savedPosts", savedPosts);
        model.addAttribute("newPost", new BlogPost());

        return "profile";
    }

    @PostMapping("/posts/unsave/{postId}")
    public String unsavePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        BlogPost post = blogService.getPostById(postId); // Assuming this method returns BlogPost or throws
        savedPostService.unsavePost(user, post);
        return "redirect:/profile";
    }

    // ❤️ Like a post
    @PostMapping("/posts/like/{id}")
    public String likePost(@PathVariable Long id) {
        blogService.likePost(id);
        return "redirect:/posts/" + id;
    }

    // 🔍 Search
    @GetMapping("/search")
    public String searchPosts(@RequestParam String keyword, Model model) {
        List<BlogPost> results = blogService.searchPostsByTitle(keyword);
        List<String> categories = blogService.findAllCategories();

        model.addAttribute("posts", results);
        model.addAttribute("categories", categories);
        model.addAttribute("newPost", new BlogPost());

        String currentUsername = getCurrentUsername();
        model.addAttribute("currentUsername", currentUsername);

        if (currentUsername != null) {
            User user = userService.findByUsername(currentUsername);
            List<Long> savedPostIds = savedPostService.getSavedPostIdsByUser(user);
            model.addAttribute("savedPostIds", savedPostIds);
        }

        return "index";
    }

    // ✅ Utility: Get current logged-in username
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName() : null;
    }

    // ✅ Utility: Extract first image URL from content
    private String extractFirstImageUrl(String content) {
        if (content == null) return null;
        int imgStart = content.indexOf("<img");
        if (imgStart == -1) return null;

        int srcStart = content.indexOf("src=\"", imgStart);
        if (srcStart == -1) return null;

        srcStart += 5;
        int srcEnd = content.indexOf("\"", srcStart);
        if (srcEnd == -1) return null;

        return content.substring(srcStart, srcEnd);
    }
}
