package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Mapper;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Models.dto.PostDTO2;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import io.swagger.annotations.ResponseHeader;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private PostService postService;
    private UserService userService;
    private CommentService commentService;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    //OLD
//    @GetMapping("/feed")
//    public String showFeed(Model model) {
//        model.addAttribute("posts", postService.findAll());
//        return "posts";
//    }

    @GetMapping("/page/{page}")
    public String showNewFeed(Model model, Sort sort, @PathVariable(name = "page") int page) {
        model.addAttribute("posts", postService.findAll(sort, page));
        model.addAttribute("postDTO2", new PostDTO2());
        model.addAttribute("postDTO3", new PostDTO2());
        return "allPosts";
    }

    @PostMapping("")
    public String likeDislikePost(@ModelAttribute("postDTO2") PostDTO2 postDTO2,
                                  Model model, Principal principal) {
        model.addAttribute("posts", postService.findAll());
        boolean isPostLiked = postService.isLiked(postDTO2.getPostId(), principal);

        if (isPostLiked) {
            try {
                postService.dislikePost(postDTO2.getPostId(), userService.getUserByUserName(principal.getName()));
            } catch (EntityNotFoundException e) {
                model.addAttribute("error", e.getMessage());
                return "allPosts";
            }
        } else {
            try {
                postService.likePost(postDTO2.getPostId(), userService.getUserByUserName(principal.getName()));
            } catch (DuplicateEntityException e) {
                model.addAttribute("error", e.getMessage());
                return "allPosts";
            }
        }
        return "allPosts";
    }

    @GetMapping("/post/{id}")
    public String showPost(Model model, @PathVariable(name = "id") int postId) {
        Post post01 = postService.getOne(postId);
        model.addAttribute("post", post01);
        model.addAttribute("comments", post01.getComments());
        model.addAttribute("comment", new CommentDTO());
        return "post-single";
    }

    @PostMapping("/post/{id}")
    public String leaveComment(@ModelAttribute("comment") CommentDTO commentDTO,
                               @PathVariable(name = "id") int postId, Principal principal) {
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setPost(postService.getOne(postId));
        newComment.setUser(userService.getUserByUserName(principal.getName()));
        commentService.save(newComment);
        return "redirect:/posts/post/" + postId + "#leaveComment";
    }

    @GetMapping("/newPost")
    public String newPostData(Model model) {
        PostDTO post = new PostDTO();
        model.addAttribute("post", post);
        return "newPost";
    }

    @GetMapping("/post/{id}/postImage")
    public void renderPostImageFormDB(@PathVariable int id, HttpServletResponse response) throws IOException {
        Post post = postService.getOne(id);

        if (post.getPicture() != null) {
            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(post.getPicture()));
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    @PostMapping("/newPost")
    public String newPost(Model model, @ModelAttribute("post") PostDTO post, BindingResult errors,
                          @RequestParam("imagefile") MultipartFile file, Principal principal) throws IOException {

        if (errors.hasErrors()) {
            return "newPost";
        }

        Post newPost = Mapper.createPostFromDTO(post);
        newPost.setUser(userService.getUserByUserName(principal.getName()));
        newPost.setPicture(Base64.getEncoder().encodeToString(file.getBytes()));

        try {
            postService.save(newPost);
        } catch (IllegalArgumentException | DuplicateEntityException e) {
            model.addAttribute("error", e.getMessage());
            return "newPost";
        }
        return "newPost";
    }

}

