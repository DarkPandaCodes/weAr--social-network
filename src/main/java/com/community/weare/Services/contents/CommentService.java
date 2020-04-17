package com.community.weare.Services.contents;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    List<Comment> findAll();

    List<Comment> findAll(Sort sort);

    List<Comment> findAllCommentsOfPost(Post post, Sort sort);

    boolean existsById(int commentId);

    Comment getOne(int commentId);

    Comment save(Comment comment);

    void likeComment(int commentId, User user);

    void dislikeComment(int commentId, User user);

    void editComment(int commentId, String content, Principal principal);

    void deleteComment(int commentId, Principal principal);

    int deleteCommentByPostPostId(int postId);
}
