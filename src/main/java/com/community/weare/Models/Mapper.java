package com.community.weare.Models;

import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Services.contents.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    private static PostService postService;

    //TODO refactoring - make a factory
    @Autowired
    public Mapper(PostService postService) {
        Mapper.postService = postService;
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

}
