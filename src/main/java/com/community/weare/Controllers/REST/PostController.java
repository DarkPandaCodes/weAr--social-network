package com.community.weare.Controllers.REST;

import com.community.weare.Models.Post;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.PostService;
import com.community.weare.Services.models.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;
    private UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostController(PostService postService, UserService userService, ModelMapper modelMapper) {
        this.postService = postService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Post> findAll(Sort sort) {
        return postService.findAll(sort);
    }

    @PostMapping("/create")
    public Post save(@RequestBody PostDTO postDTO) {
        Post newPost = modelMapper.map(postDTO, Post.class);
        newPost.setUser(postService.getUserById(postDTO.getUserId()));
        return postService.save(newPost);
    }

    @PutMapping("/like")
    public void likeAPost(int postId) {
        postService.likeAPost(postId);
    }

}
