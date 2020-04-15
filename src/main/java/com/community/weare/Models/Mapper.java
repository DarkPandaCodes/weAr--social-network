package com.community.weare.Models;

import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class Mapper {
    private static PostService postService;
    private static UserService userService;

    //TODO refactoring - make a factory
    @Autowired
    public Mapper(PostService postService, UserService userService) {
        Mapper.postService = postService;
        Mapper.userService = userService;
    }

    public static Post createPostFromDTO(PostDTO postDTO) {
        Post newPost = new Post();
        newPost.setPublic(postDTO.isPublic());
        newPost.setContent(postDTO.getContent());
        newPost.setPicture(postDTO.getPicture());
        return newPost;
    }

    public static Comment createCommentFromDTO(CommentDTO commentDTO) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(commentDTO.getPostId()));
        newComment.setUser(postService.getUserById(commentDTO.getUserId()));
        return newComment;
    }

    public static boolean isLiked(int postId, Principal principal) {
        return postService.isLiked(postId, principal);
    }

}
