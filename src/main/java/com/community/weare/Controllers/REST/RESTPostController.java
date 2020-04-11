package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.contents.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class RESTPostController {
    private PostService postService;

    @Autowired
    public RESTPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> findAll(Sort sort) {
        return postService.findAll(sort);
    }

    @GetMapping("/showComments")
    public List<Comment> showComments(@RequestParam int postId) {
        return postService.showComments(postId);
    }

    @PostMapping("/create")
    public Post save(@RequestBody PostDTO postDTO) {
        Post newPost = Mapper.createPostFromDTO(postDTO);
        return postService.save(newPost);
    }

    @PutMapping("/like")
    public void likeAPost(@RequestParam int postId, int userId) {
        if (!postService.ifUserExistsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
        }
        User userLike = postService.getUserById(userId);
        try {
            postService.likePost(postId, userLike);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/unlike")
    public void unlikeAPost(@RequestParam int postId, int userId) {
        if (!postService.ifUserExistsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
        }
        User userUnlike = postService.getUserById(userId);
        try {
            postService.unlikePost(postId, userUnlike);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/edit")
    public void editPost(Principal principal, @RequestParam int postId, @RequestBody PostDTO postDTO) {
        try {
            postService.editPost(postId, postDTO, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public void deletePost(Principal principal, @RequestParam int postId) {
        try {
            postService.deletePost(postId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
