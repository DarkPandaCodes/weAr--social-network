package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Models.factories.PostFactory;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
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
    private UserService userService;
    private PostFactory postFactory;

    @Autowired
    public RESTPostController(PostService postService, UserService userService, PostFactory postFactory) {
        this.postService = postService;
        this.userService = userService;
        this.postFactory = postFactory;
    }

    @GetMapping("/")
    public List<Post> findAll(Sort sort, Principal principal) {
        return postService.findPostsByAlgorithm(sort, principal);
    }

    @GetMapping("/showComments")
    public List<Comment> showComments(@RequestParam int postId) {
        return postService.showComments(postId);
    }

    @PostMapping("/create")
    public Post save(@RequestBody PostDTO postDTO, Principal principal) {
        Post newPost = postFactory.createPostFromDTO(postDTO);
        newPost.setUser(userService.getUserByUserName(principal.getName()));
        return postService.save(newPost);
    }

    @PutMapping("/like")
    public void likeAPost(@RequestParam int postId, Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        boolean isPostLiked = postService.getOne(postId).isLiked(user.getUsername());

        if (isPostLiked) {
            try {
                postService.dislikePost(postId, user);
            } catch (DuplicateEntityException | EntityNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            try {
                postService.likePost(postId, user);
            } catch (DuplicateEntityException | EntityNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

//    @PutMapping("/unlike")
//    public void dislikeAPost(@RequestParam int postId, int userId) {
//        User user = userService.getUserById(userId);
//        if (!userService.checkIfUserExist(user)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
//        }
//        try {
//            postService.dislikePost(postId, user);
//        } catch (DuplicateEntityException | EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }

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
