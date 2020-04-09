package com.community.weare.Services;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CommentService {
    List<Comment> findAll();

    List<Comment> findAll(Sort sort);

    List<Comment> findAllCommentsOfPost(Post post, Sort sort);

    Comment getOne(int commentId);

    Comment save(Comment comment);

    void likeComment(int commentId, User user);

    void unlikeComment(int commentId, User user);

    void editComment(int commentId, CommentDTO commentDTO);

    void deleteComment(int commentId);
}
