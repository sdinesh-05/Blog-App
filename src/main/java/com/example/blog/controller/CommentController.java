package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentController {

    @Autowired private BlogService blogService;
    @Autowired private CommentService commentService;
    @Autowired private UserService userService;

    @PostMapping("/posts/{id}/comment")
    public String addComment(@PathVariable Long id,
                            @RequestParam("commentContent") String content) {
        BlogPost post = blogService.getPostById(id);
        String username = getCurrentUsername();
        if (post != null && username != null) {
            User user = userService.findByUsername(username);
            commentService.addComment(post, user, content);
        }
        return "redirect:/posts/" + id;
    }
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && !auth.getName().equals("anonymousUser")) ? auth.getName() : null;
    }

}
