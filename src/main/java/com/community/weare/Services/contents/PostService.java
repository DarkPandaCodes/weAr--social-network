package com.community.weare.Services.contents;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.List;
import java.util.Set;

public interface PostService {

    List<Post> findAll();

    List<Post> findAll(Sort sort);

    Post getOne(int postId);

    boolean existsById(int postId);

    Post save(Post post);

    void likePost(int postId, User user);

    void dislikePost(int postId, User user);

    boolean isLiked(int postId, Principal principal);

    void editPost(int postId, PostDTO postDTO, Principal principal);

    void deletePost(int postId, Principal principal);

    List<Comment> showComments(int postId);
}
