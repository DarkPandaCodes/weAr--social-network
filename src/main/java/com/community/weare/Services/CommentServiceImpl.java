package com.community.weare.Services;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Repositories.CommentRepository;
import com.community.weare.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
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

        return commentRepository.findByPostOrderByCommentId
                (post, Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Override
    public Comment getOne(int commentId) {
        return commentRepository.getOne(commentId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void likeComment(int commentId) {
        Comment comment = getOne(commentId);
        int currentLikes = comment.getLikes();
        comment.setLikes(currentLikes + 1);
        commentRepository.save(comment);
    }

    @Override
    public void editComment(int commentId, CommentDTO commentDTO) {
        Comment commentToEdit = getOne(commentId);
        commentToEdit.setContent(commentDTO.getContent());
        commentRepository.save(commentToEdit);
    }

    @Override
    public void deleteComment(int commentId) {
        Comment commentToDelete = getOne(commentId);
        commentRepository.delete(commentToDelete);
    }
}
