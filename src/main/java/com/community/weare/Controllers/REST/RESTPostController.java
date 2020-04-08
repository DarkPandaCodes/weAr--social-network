package com.community.weare.Controllers.REST;

import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.PostService;
import com.community.weare.Services.models.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class RESTPostController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public RESTPostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public List<Post> findAll(Sort sort) {
        return postService.findAll(sort);
    }

    @PostMapping("/create")
    public Post save(@RequestBody PostDTO postDTO) {
        Post newPost = Mapper.createPostFromDTO(postDTO);
        return postService.save(newPost);
    }

    @PutMapping("/like")
    public void likeAPost(int postId) {
        postService.likePost(postId);
    }

    @PutMapping("/edit")
    public void editPost(@RequestParam int postId, @RequestBody PostDTO postDTO) {
        postService.editPost(postId, postDTO);
    }

    @DeleteMapping("/delete")
    public void deletePost(int postId) {
        postService.deletePost(postId);
    }

}
