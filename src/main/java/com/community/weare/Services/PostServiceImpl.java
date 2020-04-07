package com.community.weare.Services;

import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.models.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> findAll(Sort sort) {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.getOne(userId);
    }

    @Override
    public void likeAPost(int postId) {
        Post postToLike = postRepository.getOne(postId);
        int currentLikes = postToLike.getLikes();
        postToLike.setLikes(currentLikes + 1);
        postRepository.save(postToLike);
    }
}
