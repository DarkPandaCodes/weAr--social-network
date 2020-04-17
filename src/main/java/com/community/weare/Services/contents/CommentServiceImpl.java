package com.community.weare.Services.contents;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Repositories.CommentRepository;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Repositories.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAll(Sort sort) {
        return commentRepository.findAll(Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Override
    public List<Comment> findAllCommentsOfPost(Post post, Sort sort) {
        throwsNotFoundIfNeeded(post.getPostId(), postRepository.existsById(post.getPostId()),
                "Post with id %d does not exists");
        return commentRepository.findByPostOrderByCommentId
                (post, Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Override
    public boolean existsById(int commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    public Comment getOne(int commentId) {
        throwsNotFoundIfNeeded(commentId, commentRepository.existsById(commentId),
                "Comment with id %d does not exists");
        return commentRepository.getOne(commentId);
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        Post post = comment.getPost();
        post.getComments().add(comment);
        postRepository.save(post);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void likeComment(int commentId, User user) {
        throwsNotFoundIfNeeded(commentId, commentRepository.existsById(commentId),
                "Comment with id %d does not exists");
        Comment commentToLike = commentRepository.getOne(commentId);
        if (commentToLike.getLikes().contains(user)) {
            throw new DuplicateEntityException("You already liked this");
        }
        commentToLike.getLikes().add(user);
        commentRepository.save(commentToLike);
    }

    @Override
    @Transactional
    public void unlikeComment(int commentId, User user) {
        throwsNotFoundIfNeeded(commentId, commentRepository.existsById(commentId),
                "Comment with id %d does not exists");
        Comment commentToUnlike = commentRepository.getOne(commentId);
        if (!commentToUnlike.getLikes().contains(user)) {
            throw new EntityNotFoundException("Before unlike you must like");
        }
        commentToUnlike.getLikes().remove(user);
        commentRepository.save(commentToUnlike);
    }

    @Override
    public void editComment(int commentId, CommentDTO commentDTO, Principal principal) {
        validatesAuthority(commentId, principal);
        Comment commentToEdit = commentRepository.getOne(commentId);
        commentToEdit.setContent(commentDTO.getContent());
        commentRepository.save(commentToEdit);
    }


    @Override
    public void deleteComment(int commentId, Principal principal) {
        validatesAuthority(commentId, principal);
        Comment commentToDelete = commentRepository.getOne(commentId);
        commentRepository.delete(commentToDelete);
    }

    private void validatesAuthority(int commentId, Principal principal) {
        throwsNotFoundIfNeeded(commentId, commentRepository.existsById(commentId),
                "Comment with id %d does not exists");
        Comment comment = getOne(commentId);
        User userPrincipal = userRepository.findByUsername(principal.getName());

        if (!((comment.getUser().getUsername().equals(principal.getName()))
                || userRepository.findByAuthorities("ROLE_ADMIN").contains(userPrincipal))) {
            throw new IllegalArgumentException("You can only edit/delete your comments");
        }
    }

    private void throwsNotFoundIfNeeded(int id, boolean b, String s) {
        if (!b) {
            throw new EntityNotFoundException
                    (String.format(s, id));
        }
    }
}
