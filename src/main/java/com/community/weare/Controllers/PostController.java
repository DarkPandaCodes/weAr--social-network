package com.community.weare.Controllers;

import com.community.weare.Models.Post;
import com.community.weare.Services.contents.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public String showFeed(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "posts";
    }

    @GetMapping("/post/{id}")
    public String showPost(Model model, @PathVariable(name = "id")int postId) {
        Post post01 = postService.getOne(postId);
        model.addAttribute("post", post01);
        model.addAttribute("comments", post01.getComments());
        return "post";
    }


}
