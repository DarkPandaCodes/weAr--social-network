package com.community.weare.Controllers.REST;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Services.CommentService;
import com.community.weare.Services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
    public void likeComment(int commentId) {
        commentService.likeComment(commentId);
    }

    @PutMapping("/edit")
    public void editComment(int commentId, CommentDTO commentDTO) {
        commentService.editComment(commentId, commentDTO);
    }

    @DeleteMapping("/delete")
    public void deleteComment(int commentId) {
        commentService.deleteComment(commentId);
    }
}
