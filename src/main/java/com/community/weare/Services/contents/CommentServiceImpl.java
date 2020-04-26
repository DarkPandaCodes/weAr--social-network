package com.community.weare.Services.contents;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CommentRepository;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
                              UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAll(Sort sort) {
        //TODO only public posts?
        return commentRepository.findAll(Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Override
    public List<Comment> findAllCommentsOfPost(Post post, Sort sort) {
        //TODO is it public or are you admin, friend, creator?
        if (!postRepository.existsById(post.getPostId())) {
            throw new EntityNotFoundException(String.format
                    ("Post with id %d does not exists", post.getPostId()));
        }
        return commentRepository.findByPostOrderByCommentId
                (post, Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Override
    public boolean existsById(int commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    public Comment getOne(int commentId) {
        //TODO only public posts?
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format
                    ("Comment with id %d does not exists", commentId));
        }
        return commentRepository.getOne(commentId);
    }

    @Override
    @Transactional
    public Comment save(Comment comment, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        Post post = comment.getPost();
        post.getComments().add(comment);
        postRepository.save(post);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void likeComment(int commentId, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        User user = userService.getUserByUserName(principal.getName());
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format
                    ("Comment with id %d does not exists", commentId));
        }
        Comment comment = commentRepository.getOne(commentId);
        if (comment.getLikes().contains(user)) {
            throw new DuplicateEntityException("You already liked this");
        }
        comment.getLikes().add(user);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void dislikeComment(int commentId, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        User user = userService.getUserByUserName(principal.getName());
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format
                    ("Comment with id %d does not exists", commentId));
        }
        Comment comment = commentRepository.getOne(commentId);
        if (!comment.getLikes().contains(user)) {
            throw new EntityNotFoundException("Before dislike you must like");
        }
        comment.getLikes().remove(user);
        commentRepository.save(comment);
    }

    @Override
    public void editComment(int commentId, String content, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format
                    ("Comment with id %d does not exists", commentId));
        }
        Comment comment = commentRepository.getOne(commentId);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), comment.getUser());
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(int commentId, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format
                    ("Comment with id %d does not exists", commentId));
        }
        Comment comment = commentRepository.getOne(commentId);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), comment.getUser());
        commentRepository.delete(comment);
    }

    @Override
    public int deleteCommentByPostPostId(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format
                    ("Post with id %d does not exists", postId));
        }
        return commentRepository.deleteCommentByPostPostId(postId);
    }
}
