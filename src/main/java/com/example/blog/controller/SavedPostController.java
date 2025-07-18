package com.example.blog.controller;

import com.example.blog.model.BlogPost;
import com.example.blog.model.User;
import com.example.blog.service.BlogService;
import com.example.blog.service.SavedPostService;
import com.example.blog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SavedPostController {

    @Autowired private BlogService blogService;
    @Autowired private SavedPostService savedPostService;
    @Autowired private UserService userService;

    @PostMapping("/posts/save/{id}")
    public String savePost(@PathVariable Long id, Authentication auth) {
        BlogPost post = blogService.getPostById(id);
        User user = userService.findByUsername(auth.getName());
        savedPostService.savePost(user, post);
        return "redirect:/posts/" + id;
    }
}
