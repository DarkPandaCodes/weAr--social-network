package com.community.weare.Services.contents;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    public Post getOne(int postId) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        return postRepository.getOne(postId);
    }

    @Override
    public boolean existsById(int postId) {
        return postRepository.existsById(postId);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public User getUserById(int userId) {
        throwsNotFoundIfNeeded(userId, userRepository.existsById(userId),
                "User with id %d does not exists");
        return userRepository.getOne(userId);
    }

    @Override
    public boolean ifUserExistsById(int userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public void likePost(int postId, User user) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        Post postToLike = postRepository.getOne(postId);
        if (postToLike.getLikes().contains(user)) {
            throw new DuplicateEntityException("You already liked this");
        }
        postToLike.getLikes().add(user);
        postRepository.save(postToLike);
    }

    @Override
    public void unlikePost(int postId, User user) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        Post postToUnlike = postRepository.getOne(postId);
        if (!postToUnlike.getLikes().contains(user)) {
            throw new EntityNotFoundException("Before unlike you must like");
        }
        postToUnlike.getLikes().remove(user);
        postRepository.save(postToUnlike);
    }

    @Override
    public void editPost(int postId, PostDTO postDTO, Principal principal) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        User userPrincipal = userRepository.findByUsername(principal.getName());

        if (!((getUserById(postDTO.getUserId()).getUsername().equals(principal.getName()))
                || userRepository.findByAuthorities("ROLE_ADMIN").contains(userPrincipal))) {
            throw new IllegalArgumentException("You can only edit your posts");
        }

        Post postToEdit = getOne(postId);
        postToEdit.setPublic(postDTO.isPublic());
        postToEdit.setContent(postDTO.getContent());
        postToEdit.setPicture(postDTO.getPicture());
        postRepository.save(postToEdit);
    }

    @Override
    public void deletePost(int postId, Principal principal) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        Post postToDelete = getOne(postId);
        User userPrincipal = userRepository.findByUsername(principal.getName());

        if (!((postToDelete.getUser().getUsername().equals(principal.getName()))
                || userRepository.findByAuthorities("ROLE_ADMIN").contains(userPrincipal))) {
            throw new IllegalArgumentException("You can only delete your posts");
        }

        postRepository.delete(postToDelete);
    }

    @Override
    public List<Comment> showComments(int postId) {
        throwsNotFoundIfNeeded(postId, postRepository.existsById(postId),
                "Post with id %d does not exists");
        Post post = getOne(postId);
        return post.getComments();
    }

    private void throwsNotFoundIfNeeded(int postId, boolean b, String s) {
        if (!b) {
            throw new EntityNotFoundException
                    (String.format(s, postId));
        }
    }
}
