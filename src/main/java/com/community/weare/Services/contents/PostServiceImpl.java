package com.community.weare.Services.contents;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    public static final int POSTS_PER_PAGE = 5;
    private static final double ALGORITHM_EFFECT_LIKES = -1.0;
    private static final double ALGORITHM_EFFECT_COMMENTS = -3.0;
    private static final double ALGORITHM_EFFECT_FRIENDS = -10.0;
    private static final double ALGORITHM_EFFECT_MULTI_POSTS = 10.0;

    private PostRepository postRepository;
    private UserService userService;
    private CommentService commentService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserService userService,
                           CommentService commentService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> findAll(Sort sort) {
        List<Post> allPost = postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
//        List<Post> listSinglePage = new ArrayList<>();
//
//        if (allPost.size() > (page - 1) * POSTS_PER_PAGE) {
//            for (int i = 0; i < POSTS_PER_PAGE; i++) {
//                if ((page - 1) * POSTS_PER_PAGE + i == allPost.size()) {
//                    break;
//                }
//                listSinglePage.add(allPost.get((page - 1) * POSTS_PER_PAGE + i));
//            }
//        }
        return allPost;
    }

    @Override
    public List<Post> findAllByUser(String userName) {
        return postRepository.findAllByUserUsername(Sort.by(Sort.Direction.DESC, "date"), userName);
    }

    @Override
    @Transactional
    public List<Post> findPostsByAlgorithm(Sort sort, Principal principal) {
        List<Post> allPost = findAll(sort);
        return applyAlgorithm(principal, allPost);
    }

    @Override
    public List<Post> filterPostsByPublicity(List<Post> posts, boolean isPublic) {
        return posts.stream()
                .filter(p -> p.isPublic() == isPublic)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> filterPostsByCategory(List<Post> posts, String categoryName) {
        return posts.stream()
                .filter(p -> p.getCategory().getName().equals(categoryName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> filterPostsByUsername(List<Post> posts, String userName) {
        return posts.stream()
                .filter(p -> p.getUser().getUsername().equals(userName))
                .collect(Collectors.toList());
    }


    @Override
    public Post getOne(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
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
    @Transactional
    public void likePost(int postId, User user) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToLike = postRepository.getOne(postId);
        if (postToLike.getLikes().contains(user)) {
            throw new DuplicateEntityException("You already liked this");
        }
        postToLike.getLikes().add(user);
        postRepository.save(postToLike);
    }

    @Override
    @Transactional
    public void dislikePost(int postId, User user) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToDislike = postRepository.getOne(postId);
        if (!postToDislike.getLikes().contains(user)) {
            throw new EntityNotFoundException("Before dislike you must like");
        }
        postToDislike.getLikes().remove(user);
        postRepository.save(postToDislike);
    }

    @Override
    public boolean isLiked(int postId, Principal principal) {
        User loggedUser = userService.getUserByUserName(principal.getName());
        Post post = postRepository.getOne(postId);
        return post.isLiked(principal.getName());
    }

    @Override
    @Transactional
    public void editPost(int postId, PostDTO postDTO, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        User userPrincipal = userService.getUserByUserName(principal.getName());
        Post postToEdit = getOne(postId);

        if (!((postToEdit.getUser().getUsername().equals(principal.getName()))
                || userService.findByAuthorities("ROLE_ADMIN").contains(userPrincipal))) {
            throw new IllegalArgumentException("You can only edit your posts");
        }
        postToEdit.setPublic(postDTO.isPublic());
        postToEdit.setContent(postDTO.getContent());
        postRepository.save(postToEdit);
    }


    @Override
    @Transactional
    public void deletePost(int postId, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToDelete = getOne(postId);
        User userPrincipal = userService.getUserByUserName(principal.getName());

        if (!((postToDelete.getUser().getUsername().equals(principal.getName()))
                || userService.findByAuthorities("ROLE_ADMIN").contains(userPrincipal))) {
            throw new IllegalArgumentException("You can only delete your posts");
        }

        postToDelete.getLikes().clear();
        postToDelete.getComments().clear();
        postRepository.save(postToDelete);
        commentService.deleteCommentByPostPostId(postId);
        postRepository.delete(postToDelete);
    }

    @Override
    public List<Comment> showComments(int postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post post = getOne(postId);
        return post.getComments();
    }

    private List<Post> applyAlgorithm(Principal principal, List<Post> allPost) {
        User loggedUser = new User();
        if (principal != null) {
            loggedUser = userService.getUserByUserName(principal.getName());
        }

        double timeEffect;
        double likesEffect;
        double commentsEffect;
        double friendsEffect;
        double multiPostsEffect;
        int numberOfPostsForMonth = 0;

        for (int i = 0; i < allPost.size(); i++) {
            Post currentPost = allPost.get(i);
            List<Post> allPostsOfUser = findAllByUser(currentPost.getUser().getUsername());
            timeEffect = i;
            likesEffect = currentPost.getLikes().size() * ALGORITHM_EFFECT_LIKES;
            commentsEffect = currentPost.getComments().size() * ALGORITHM_EFFECT_COMMENTS;
            if (principal != null && loggedUser.getFriendList().contains(currentPost.getUser())) {
                friendsEffect = 1 * ALGORITHM_EFFECT_FRIENDS;
            } else {
                friendsEffect = 0;
            }

            String monthOfCurrentPost = currentPost.getDate().substring(3, 10);
            String monthOfPreviousPost;

            for (Post post : allPostsOfUser) {
                if (currentPost.getPostId() == post.getPostId()) {
                    break;
                }
                monthOfPreviousPost = post.getDate().substring(3, 10);
                if (monthOfCurrentPost.equals(monthOfPreviousPost)) {
                    numberOfPostsForMonth++;
                }
            }

            multiPostsEffect = numberOfPostsForMonth * ALGORITHM_EFFECT_MULTI_POSTS;
            numberOfPostsForMonth = 0;

            currentPost.setRank(timeEffect + likesEffect + commentsEffect + friendsEffect + multiPostsEffect);
            save(currentPost);
        }
        return postRepository.findAll(Sort.by(Sort.Direction.ASC, "rank"));
    }
}
