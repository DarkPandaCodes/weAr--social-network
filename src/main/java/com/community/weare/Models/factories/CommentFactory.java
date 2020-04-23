package com.community.weare.Models.factories;

import com.community.weare.Models.Comment;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Component
public class CommentFactory {
    private PostService postService;
    private UserService userService;

    @Autowired
    public CommentFactory(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public Comment createCommentFromDTO(CommentDTO commentDTO) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(commentDTO.getPostId()));
        newComment.setUser(userService.getUserById(commentDTO.getUserId()));
        return newComment;
    }

    public Comment createCommnetFromInput(@ModelAttribute("comment") CommentDTO commentDTO, @PathVariable(name = "id") int postId, Principal principal) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(postId));
        newComment.setUser(userService.getUserByUserName(principal.getName()));
        return newComment;
    }
}
