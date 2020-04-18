package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.factories.CommentFactory;
import com.community.weare.Services.contents.CommentService;
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
@RequestMapping("/api/comment")
public class RESTCommentController {

    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    private CommentFactory commentFactory;

    @Autowired
    public RESTCommentController(CommentService commentService, PostService postService, UserService userService,
                                 CommentFactory commentFactory) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.commentFactory = commentFactory;
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
        if (commentService.getOne(commentId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format
                    ("Comment with id %d does not exists", commentId));
        }
        return commentService.getOne(commentId);
    }

    @PostMapping("/create")
    public Comment save(@RequestBody CommentDTO commentDTO) {
        Comment newComment = commentFactory.createCommentFromDTO(commentDTO);
        return commentService.save(newComment);
    }

    @PutMapping("/like")
    public void likeComment(@RequestParam int commentId, int userId) {
        User user = userService.getUserById(userId);
//        if (!userService.checkIfUserExist(user)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
//        }
        //TODO Fix it when the method userService.checkIfUserExist is fixed
        try {
            commentService.likeComment(commentId, user);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/dislike")
    public void dislikeComment(@RequestParam int commentId, int userId) {
        User user = userService.getUserById(userId);
//        if (!userService.checkIfUserExist(user)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
//        }
        //TODO Fix it when the method userService.checkIfUserExist is fixed
        try {
            commentService.dislikeComment(commentId, user);
        } catch (DuplicateEntityException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/edit")
    public void editComment(Principal principal, @RequestParam int commentId, String content) {
        try {
            commentService.editComment(commentId, content, principal);
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
