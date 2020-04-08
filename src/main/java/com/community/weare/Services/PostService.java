package com.community.weare.Services;

import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import org.springframework.data.domain.Sort;

import java.util.List;


public interface PostService {

    List<Post> findAll();

    List<Post> findAll(Sort sort);

    Post getOne(int postId);

    Post save(Post post);

    User getUserById(int userId);

    void likePost(int postId);

    void editPost(int postId, PostDTO postDTO);

    void deletePost(int postId);
}
