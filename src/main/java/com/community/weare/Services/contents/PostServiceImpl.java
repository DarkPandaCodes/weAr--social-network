package com.community.weare.Services.contents;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Category;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

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
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "postId"));
    }

    @Override
    public List<Post> findAllByUser(String userName, Principal principal) {
        User postCreator = userService.getUserByUserName(userName);
        List<Post> allUserPosts = postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"), userName);
        if (principal == null) {
            return filterPostsByPublicity(allUserPosts, true);
        }
        User userViewer = userService.getUserByUserName(principal.getName());
        if (postCreator == userViewer || postCreator.getFriendList().contains(userViewer)) {
            return allUserPosts;
        } else {
            return filterPostsByPublicity(allUserPosts, true);
        }
    }

    @Override
    public Slice<Post> findSliceWithPosts(int startIndex, int pageSize, String sortParam, User user, String principal) {
        if (pageSize == 0 && startIndex == 0) {
            throw new EntityNotFoundException();
        }
        Pageable page = PageRequest.of(startIndex, pageSize, Sort.by(sortParam).descending());
        if (user.isFriend(principal)||user.getUsername().equals(principal)) {
            return postRepository.findAllByUserUsername(page, user.getUsername());
        } else {
            return postRepository.findAllByUserUsernamePublic(page, user.getUsername());
        }
    }

    @Override
    @Transactional
    public List<Post> findAllPostsByAlgorithm(Sort sort, Principal principal) {
        List<Post> allPost = postRepository.findAll(Sort.by(Sort.Direction.DESC, "postId"));
        return applyAlgorithm(principal, allPost);
    }

    @Override
    public List<Post> findPostsPersonalFeed(Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
        List<Post> friendsPosts = filterPostsByFriends
                (findAll(), user);

        Category usersCategory = user.getExpertiseProfile().getCategory();
        List<Post> postsFromMyCategory = filterPostsByCategory
                (findAll(), usersCategory.getName());
        return mergeTwoLists(friendsPosts, postsFromMyCategory, principal);
    }

    @Override
    public List<Post> findPostsByAuthority(Sort sort, Principal principal) {
        if (principal == null) {
            return filterPostsByPublicity(findAllPostsByAlgorithm(sort, principal), true);
        }
        return findPostsPersonalFeed(principal);
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
    public List<Post> filterPostsByFriends(List<Post> posts, User user) {
        return posts.stream()
                .filter(p -> p.getUser().getFriendList().contains(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> mergeTwoLists(List<Post> list1, List<Post> list2, Principal principal) {
        Set<Post> set = new LinkedHashSet<>(list1);
        set.addAll(list2);
        List<Post> listResult = new ArrayList<>(set);
        return applyAlgorithm(principal, listResult);
    }

    @Override
    public Post getOne(int postId, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post post = postRepository.getOne(postId);
        ifNotAuthorizedToVewPostThrow(principal, post);
        return post;
    }

    @Override
    public boolean existsById(int postId) {
        return postRepository.existsById(postId);
    }

    @Override
    public Post save(Post post, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        if (post.getContent().length() > 1000) {
            throw new InvalidOperationException("Content size must be up to 1000 symbols");
        }
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void likePost(int postId, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        User user = userService.getUserByUserName(principal.getName());
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToLike = postRepository.getOne(postId);
        if (postToLike.getLikes().contains(user)) {
            throw new DuplicateEntityException("You already liked this");
        }
        postToLike.getLikes().add(user);
        double currentRank = postToLike.getRank();
        postToLike.setRank(currentRank + ALGORITHM_EFFECT_LIKES);
        postToLike.setLiked(true);
        postRepository.save(postToLike);
    }

    @Override
    @Transactional
    public void dislikePost(int postId, Principal principal) {
        if (principal == null) {
            throw new InvalidOperationException("User isn't authorised");
        }
        User user = userService.getUserByUserName(principal.getName());
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        Post postToDislike = postRepository.getOne(postId);
        if (!postToDislike.getLikes().contains(user)) {
            throw new EntityNotFoundException("Before dislike you must like");
        }
        postToDislike.getLikes().remove(user);
        double currentRank = postToDislike.getRank();
        postToDislike.setRank(currentRank - ALGORITHM_EFFECT_LIKES);
        postToDislike.setLiked(false);
        postRepository.save(postToDislike);
    }

    @Override
    public boolean isLiked(int postId, Principal principal) {
        return postRepository.getOne(postId).isLiked2(principal.getName());
    }

    @Override
    @Transactional
    public void editPost(int postId, PostDTO postDTO, Principal principal) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Post with id %d does not exists", postId));
        }
        if (postDTO.getContent().length() > 1000) {
            throw new InvalidOperationException("Content size must be up to 1000 symbols");
        }
        Post postToEdit = postRepository.getOne(postId);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), postToEdit.getUser());
        postToEdit.setPublic(postDTO.isPublic());
        postToEdit.setContent(postDTO.getContent());
        if (postDTO.getPicture().length() > 0) {
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
        Post postToDelete = getOne(postId, principal);
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

    private List<Post> applyAlgorithm(Principal principal, List<Post> postList) {
        List<Post> sortedListByDate = postList.stream()
                .sorted(Comparator.comparing(Post::getPostId).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedListByDate.size(); i++) {
            double timeEffect = i * ALGORITHM_EFFECT_TIME;
            int numberOfPostsForMonth = 0;
            Post currentPost = sortedListByDate.get(i);
            List<Post> allPostsOfUser = postRepository.findAllByUserUsername
                    (Sort.by(Sort.Direction.DESC, "postId"), currentPost.getUser().getUsername());
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
            postRepository.save(currentPost);
        }
        return sortedListByDate.stream()
                .sorted(Comparator.comparing(Post::getRank))
                .collect(Collectors.toList());
    }

    @Override
    public void ifNotAuthorizedToVewPostThrow(Principal principal, Post post) {
        if (principal == null) {
            if (!post.isPublic()) {
                throw new InvalidOperationException("User isn't authorised");
            }
        } else {
            User postCreator = userService.getUserByUserName(post.getUser().getUsername());
            if (!(post.isPublic() || postCreator.getFriendList().stream()
                    .anyMatch(u -> u.getUsername().equals(principal.getName())) ||
                    postCreator.getUsername().equals(principal.getName()))) {
                throw new InvalidOperationException("User isn't authorised");
            }
        }
    }
}
