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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    public static final int POSTS_PER_PAGE = 5;
    private static final double ALGORITHM_EFFECT_LIKES = -1.0;
    private static final double ALGORITHM_EFFECT_COMMENTS = -3.0;
    private static final double ALGORITHM_EFFECT_TIME = 1.0;
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
        List<Post> allPost = postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
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
    public void likePost(int postId, Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
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
    public void dislikePost(int postId, Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
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
        return postRepository.getOne(postId).isLiked(principal.getName());
    }

    @Override
    @Transactional
    public void editPost(int postId, PostDTO postDTO, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToEdit = postRepository.getOne(postId);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), postToEdit.getUser());
        postToEdit.setPublic(postDTO.isPublic());
        postToEdit.setContent(postDTO.getContent());
        //TODO think of other way to check if the picture needs to be changed
        if (postDTO.getPicture().length() > 200) {
            postToEdit.setPicture(postDTO.getPicture());
        }
        postRepository.save(postToEdit);
    }


    @Override
    @Transactional
    public void deletePost(int postId, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToDelete = getOne(postId);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), postToDelete.getUser());
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
        return postRepository.getOne(postId).getComments();
    }

    private List<Post> applyAlgorithm(Principal principal, List<Post> allPost) {
        for (int i = 0; i < allPost.size(); i++) {
            double timeEffect = i * ALGORITHM_EFFECT_TIME;
            int numberOfPostsForMonth = 0;
            Post currentPost = allPost.get(i);
            List<Post> allPostsOfUser = findAllByUser(currentPost.getUser().getUsername());
            double likesEffect = currentPost.getLikes().size() * ALGORITHM_EFFECT_LIKES;
            double commentsEffect = currentPost.getComments().size() * ALGORITHM_EFFECT_COMMENTS;
            double friendsEffect;
            if (principal != null) {
                User loggedUser = userService.getUserByUserName(principal.getName());
                friendsEffect = loggedUser.isFriend
                        (currentPost.getUser().getUsername()) ? 1 * ALGORITHM_EFFECT_FRIENDS : 0;
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
            double multiPostsEffect = numberOfPostsForMonth * ALGORITHM_EFFECT_MULTI_POSTS;
            numberOfPostsForMonth = 0;

            currentPost.setRank(timeEffect + likesEffect + commentsEffect + friendsEffect + multiPostsEffect);
            save(currentPost);
        }
        return postRepository.findAll(Sort.by(Sort.Direction.ASC, "rank"));
    }
}
