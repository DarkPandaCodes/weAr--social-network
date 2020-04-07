package com.community.weare.Services;

import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PostService {

    List<Post> findAll();

    List<Post> findAll(Sort sort);

    Post save(Post post);

    User getUserById(int userId);

    void likeAPost(int postId);
}
