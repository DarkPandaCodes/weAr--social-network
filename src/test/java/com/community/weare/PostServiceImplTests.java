package com.community.weare;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Category;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostServiceImpl;
import com.community.weare.Services.users.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTests {

    @InjectMocks
    PostServiceImpl mockPostService;

    @Mock
    UserServiceImpl userService;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentService commentService;

    @Test
    public void findAllShould_CallRepository() {
        //arrange
        Post post = FactoryPostComment.createPost();
        List<Post> list = new ArrayList<>();
        list.add(post);

        Mockito.when(postRepository.findAll()).thenReturn(list);
        //act
        mockPostService.findAll();
        //assert
        Mockito.verify(postRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findAllShould_SortPostsByDate() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("1");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("2");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("3");

        List<Post> list = new ArrayList<>();
        list.add(post2);
        list.add(post1);
        list.add(post3);

        List<Post> listSorted = new ArrayList<>();
        listSorted.add(post3);
        listSorted.add(post2);
        listSorted.add(post1);

        Mockito.when(postRepository.findAll(Sort.by(Sort.Direction.DESC, "date"))).thenReturn(listSorted);
        //act
        List<Post> listResult = mockPostService.findAll(Sort.by(Sort.Direction.DESC, "date"));
        //assert
        assertEquals(list.get(0), listResult.get(1));
        assertEquals(list.get(1), listResult.get(2));
        assertEquals(list.get(2), listResult.get(0));
    }

    @Test
    public void findAllByUserShould_ReturnCorrectPosts() {
        //arrange
        Post post4 = FactoryPostComment.createPost();

        User user1 = Factory.createUser();
        user1.setUsername("TheChosenOne");

        post4.setUser(user1);

        List<Post> listOfUser = new ArrayList<>();
        listOfUser.add(post4);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "date"), "TheChosenOne")).thenReturn(listOfUser);
        //act
        List<Post> listResult = mockPostService.findAllByUser("TheChosenOne");

        //assert
        assertEquals(post4, listResult.get(0));
        assertEquals(1, listResult.size());
    }

    @Test
    public void sortingAlgorithmShould_SortCorrectly() {
        //arrange
        User user01 = Factory.createUser();
        user01.setUsername("otherName");
        User userTedi = Factory.createUser();
        userTedi.getFriendList().add(user01);

        Post post1 = FactoryPostComment.createPost();
        post1.setDate("18/04/2020 23:34:13");
        post1.setUser(userTedi);
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());

        post1.getComments().add(new Comment());
        post1.getComments().add(new Comment());

        Post post2 = FactoryPostComment.createPost();
        post1.setDate("18/04/2020 23:34:13");
        post2.setUser(userTedi);

        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());

        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());

        Post post3 = FactoryPostComment.createPost();
        post3.setUser(userTedi);
        post1.setDate("18/04/2020 23:34:13");

        List<Post> sortedList = new ArrayList<>();
        sortedList.add(post3);
        sortedList.add(post2);
        sortedList.add(post1);

        Principal principal = () -> "tedi";
        Mockito.when(postRepository.findAll(Sort.by(Sort.Direction.DESC, "date")))
                .thenReturn(sortedList);
        Mockito.when(postRepository.findAll(Sort.by(Sort.Direction.ASC, "rank")))
                .thenReturn(sortedList);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "date"),
                        post1.getUser().getUsername())).thenReturn(sortedList);
        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);

        //act
        List<Post> listResult = mockPostService.findPostsByAlgorithm
                ((Sort.by(Sort.Direction.DESC, "date")), principal);

        //assert
        assertEquals(0, listResult.get(0).getRank(), 0);
        assertEquals(-3, listResult.get(1).getRank(), 0);
        assertEquals(13, listResult.get(2).getRank(), 0);
    }

    @Test
    public void filterPostsByPublicityShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();


        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByPublicity(list, true);

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post1, listResult.get(0));
        assertEquals(post3, listResult.get(1));
    }

    @Test
    public void filterPostsByCategoryShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        Post post3 = FactoryPostComment.createPost();

        User user1 = Factory.createUser();
        user1.setUsername("User1");
        user1.getExpertiseProfile().setCategory(new Category("Lawyer"));
        User user2 = Factory.createUser();
        user2.setUsername("User2");
        user2.getExpertiseProfile().setCategory(new Category("Lawyer"));
        User user3 = Factory.createUser();
        user3.setUsername("User3");
        user3.getExpertiseProfile().setCategory(new Category("Lifeguard"));

        post1.setUser(user1);
        post2.setUser(user2);
        post3.setUser(user3);

        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByCategory(list, "Lawyer");

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post1, listResult.get(0));
        assertEquals(post2, listResult.get(1));
    }

    @Test
    public void filterPostsByUsernameShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();

        User user1 = Factory.createUser();
        user1.setUsername("User1");
        User user2 = Factory.createUser();
        user2.setUsername("User2");
        User user3 = Factory.createUser();
        user3.setUsername("User2");

        post1.setUser(user1);
        post2.setUser(user2);
        post3.setUser(user3);

        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByUsername(list, "User2");

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post2, listResult.get(0));
        assertEquals(post3, listResult.get(1));
    }

    @Test
    public void getOneShould_CallRepositoryIfPostExists() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.getOne(1);
        //assert
        Mockito.verify(postRepository, Mockito.times(1)).getOne(1);
    }

    @Test
    public void getOneShould_ThrowIfPostDoesNotExists() {
        //arrange

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.getOne(1));
    }

    @Test
    public void existsByIdShould_ReturnIfPostExists() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);

        //act
        mockPostService.existsById(1);
        //assert
        assertTrue(mockPostService.existsById(1));
    }

    @Test
    public void likePostShould_AddLike() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.likePost(1, principal);
        //assert
        assertEquals(1, post.getLikes().size());
    }

    @Test
    public void likePostShould_Throw_WhenPostDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.likePost(1, principal));
    }

    @Test
    public void likePostShould_Throw_WhenPostAlreadyLiked() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        User user = userService.getUserByUserName(principal.getName());
        post.getLikes().add(user);

        Mockito.when(postRepository.existsById(post.getPostId())).thenReturn(true);
        Mockito.when(postRepository.getOne(post.getPostId())).thenReturn(post);

        //Act, Assert
        Assert.assertThrows(DuplicateEntityException.class,
                () -> mockPostService.likePost(post.getPostId(), principal));
    }

    @Test
    public void dislikePostShould_RemoveLike() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        Principal principal = () -> "tedi";
        User user = userService.getUserByUserName(principal.getName());
        post.getLikes().add(user);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.dislikePost(1, principal);
        //assert
        assertEquals(0, post.getLikes().size());
    }

    @Test
    public void dislikePostShould_Throw_WhenPostDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.dislikePost(1, principal));
    }

    @Test
    public void dislikePostShould_Throw_WhenPostIsNotLiked() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.dislikePost(1, principal));
    }

    @Test
    public void isLikedShould_ReturnCorrect() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        User user = Factory.createUser();
        post.getLikes().add(user);

        Principal principal = () -> "tedi";
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        assertTrue(mockPostService.isLiked(post.getPostId(), principal));
    }

    @Test
    public void editPostShould_Throw_WhenPostDoesNotExists() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        PostDTO postDTO = new PostDTO();

        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.editPost(post.getPostId(), postDTO, principal));
    }

    @Test
    public void editPostShould_Edit() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setPicture(new String(new char[501]));
        Principal principal = () -> "tedi";

        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);

        //Act
        mockPostService.editPost(post.getPostId(), postDTO, principal);

        //Assert
        assertEquals(501, post.getPicture().length());
    }

    @Test
    public void editPostShould_Throw_WhenUserIsNotAllowed() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setPicture(new String(new char[501]));
        Principal principal = () -> "xxx";
        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.doThrow(new InvalidOperationException())
                .when(userService).ifNotProfileOrAdminOwnerThrow(principal.getName(), post.getUser());

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.editPost(post.getPostId(), postDTO, principal));
    }

    @Test
    public void deletePostShould_Delete() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(commentService.deleteCommentByPostPostId(post.getPostId())).thenReturn(1);

        //Act
        mockPostService.deletePost(1, principal);

        //Assert
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
    }

    @Test
    public void deletePostShould_Throw_WhenPostDoesNotExists() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.deletePost(post.getPostId(), principal));
    }

    @Test
    public void deletePostShould_Throw_WhenUserIsNotAllowed() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setPicture(new String(new char[501]));
        Principal principal = () -> "xxx";

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.doThrow(new InvalidOperationException())
                .when(userService).ifNotProfileOrAdminOwnerThrow(principal.getName(), post.getUser());
        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.deletePost(post.getPostId(), principal));
    }

    @Test
    public void showCommentsShould_Throw_WhenPostDoesNotExists() {
        //Arrange

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.showComments(1));
    }

    @Test
    public void showCommentsShould_ReturnComments() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        User user = Factory.createUser();

        Comment comment1 = new Comment();
        comment1.setUser(user);
        comment1.setPost(post);
        post.getComments().add(comment1);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        assertEquals(comment1, mockPostService.showComments(post.getPostId()).get(0));
    }
}
