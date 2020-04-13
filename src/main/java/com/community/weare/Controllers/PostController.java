package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.contents.PostService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    //OLD
//    @GetMapping("/feed")
//    public String showFeed(Model model) {
//        model.addAttribute("posts", postService.findAll());
//        return "posts";
//    }

    @GetMapping("")
    public String showNewFeed(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "allPosts";
    }

    @GetMapping("/post/{id}")
    public String showPost(Model model, @PathVariable(name = "id") int postId) {
        Post post01 = postService.getOne(postId);
        model.addAttribute("post", post01);
        model.addAttribute("comments", post01.getComments());
        return "post-single";
    }

    @GetMapping("/newPost")
    public String newPostData(Model model) {
        PostDTO post = new PostDTO();
        model.addAttribute("post", post);
        return "newPost";
    }

    @GetMapping("/post/{id}/postImage")
    public void renderPostImageFormDB(@PathVariable int id, HttpServletResponse response) throws IOException {
        Post post = postService.getOne(id);

        if (post.getPicture() != null) {
            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(post.getPicture()));
            IOUtils.copy(is, response.getOutputStream());
        }

    }

    @PostMapping("/newPost")
    public String newPost(Model model, @ModelAttribute("post") PostDTO post, BindingResult errors,
                          @RequestParam("imagefile") MultipartFile file) throws IOException {

        if (errors.hasErrors()) {
            return "newPost";
        }

        Post newPost = Mapper.createPostFromDTO(post);
        newPost.setPicture(Base64.getEncoder().encodeToString(file.getBytes()));

        try {
            postService.save(newPost);
        } catch (IllegalArgumentException | DuplicateEntityException e) {
            model.addAttribute("error", e.getMessage());
            return "newPost";
        }
        return "newPost";
    }

}

