package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class RESTCommentController {

    private CommentService commentService;
    private PostService postService;

    @Autowired
    public RESTCommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping
    public List<Comment> findAll(Sort sort) {
        return commentService.findAll(sort);
    }

    @GetMapping("/byPost")
    public List<Comment> findAllCommentsOfPost(@RequestParam int postId, Sort sort) {
        if (!postService.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post does not exists");
        }
        Post post = postService.getOne(postId);
        return commentService.findAllCommentsOfPost(post, sort);
    }

    @GetMapping("/getOne")
    public Comment getOne(@RequestParam int commentId) {
        return commentService.getOne(commentId);
    }

    @PostMapping("/create")
    public Comment save(@RequestBody CommentDTO commentDTO) {
        Comment newComment = Mapper.createCommentFromDTO(commentDTO);
        return commentService.save(newComment);
    }

    @PutMapping("/like")
    public void likeComment(@RequestParam int commentId, int userId) {
        if (!postService.ifUserExistsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
        }
        User userLiking = postService.getUserById(userId);
        try {
            commentService.likeComment(commentId, userLiking);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/unlike")
    public void unlikeComment(@RequestParam int commentId, int userId) {
        if (!postService.ifUserExistsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
        }
        User userUnlike = postService.getUserById(userId);
        try {
            commentService.unlikeComment(commentId, userUnlike);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/edit")
    public void editComment(Principal principal, @RequestParam int commentId, CommentDTO commentDTO) {
        try {
            commentService.editComment(commentId, commentDTO, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public void deleteComment(Principal principal, @RequestParam int commentId) {
        try {
            commentService.deleteComment(commentId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
