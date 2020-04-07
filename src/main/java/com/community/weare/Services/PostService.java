package com.community.weare.Services;

import com.community.weare.Models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PostService {

    List<Post> findAll();

    List<Post> findAll(Sort sort);
}
