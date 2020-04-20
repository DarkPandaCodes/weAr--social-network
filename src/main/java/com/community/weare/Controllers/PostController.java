package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Category;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Models.dto.PostDTO2;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.PostFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
    private PostFactory postFactory;
    private SkillCategoryService skillCategoryService;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService,
                          PostFactory postFactory, SkillCategoryService skillCategoryService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.postFactory = postFactory;
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping("")
    public String showFeed(Model model, Sort sort, Principal principal) {
        model.addAttribute("posts", postService.findPostsByAlgorithm(sort, principal));
        model.addAttribute("postDTO2", new PostDTO2());
        model.addAttribute("postDTO", new PostDTO());
        model.addAttribute("category", new Category());
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "allPosts";
    }

    @ModelAttribute("allCategories")
    public List<Category> populateCategories() {
        return skillCategoryService.getAll();
    }

    @PostMapping("")
    public String likeDislikeFilterPost(@ModelAttribute("postDTO2") PostDTO2 postDTO2,
                                        @ModelAttribute("postDTO") PostDTO postDTO,
                                        @ModelAttribute("category") Category category,
                                        @ModelAttribute("allCategories") List<Category> allCategories,
                                        @ModelAttribute("UserPrincipal") User userPrincipal,
                                        Model model, Principal principal, Sort sort) {
        if (postDTO2.getPostId() != 0) {
            model.addAttribute("posts", postService.findPostsByAlgorithm(sort, principal));
            boolean isPostLiked = postService.getOne(postDTO2.getPostId()).isLiked(principal.getName());
            User user = userService.getUserByUserName(principal.getName());

            if (isPostLiked) {
                try {
                    postService.dislikePost(postDTO2.getPostId(), user);
                } catch (EntityNotFoundException e) {
                    model.addAttribute("error", e.getMessage());
                    if (principal != null) {
                        model.addAttribute("UserPrincipal",
                                userService.getUserByUserName(principal.getName()));
                    }
                    return "allPosts";
                }
            } else {
                try {
                    postService.likePost(postDTO2.getPostId(), user);
                } catch (DuplicateEntityException | EntityNotFoundException e) {
                    model.addAttribute("error", e.getMessage());
                    if (principal != null) {
                        model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
                    }
                    return "allPosts";
                }
            }
        }

        //filter isPublic
        if (postDTO.getContent() != null) {
            if (!postDTO.getContent().equals("all")) {
                boolean isPublic = Boolean.parseBoolean(postDTO.getContent());
                List<Post> filteredPosts = postService.filterPostsByPublicity
                        (postService.findPostsByAlgorithm(sort, principal), isPublic);
                model.addAttribute("posts", filteredPosts);
            } else {
                model.addAttribute("posts", postService.findPostsByAlgorithm(sort, principal));
            }
        }

        //filter Category
        if (category.getName() != null) {
            if (!category.getName().equals("All")) {
                List<Post> filteredPosts = postService.filterPostsByCategory
                        (postService.findPostsByAlgorithm(sort, principal), category.getName());
                model.addAttribute("posts", filteredPosts);
            } else {
                model.addAttribute("posts", postService.findPostsByAlgorithm(sort, principal));
            }
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "allPosts";
    }

    @GetMapping("/{id}")
    public String showPost(Model model, @PathVariable(name = "id") int postId, Principal principal) {
        Post post01 = postService.getOne(postId);
        model.addAttribute("post", post01);
        model.addAttribute("comment", new CommentDTO());
        model.addAttribute("User", new UserDtoRequest());
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            model.addAttribute("isAdmin", userService.getUserByUserName
                    (principal.getName()).getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")));
        }
        return "post-single";
    }

    @PostMapping("/{id}")
    public String leaveCommentAndShowPosts(@ModelAttribute("comment") CommentDTO commentDTO,
                                           @ModelAttribute("User") UserDtoRequest user,
                                           @ModelAttribute("postDTO") PostDTO postDTO,
                                           @ModelAttribute("postDTO2") PostDTO2 postDTO2,
                                           @ModelAttribute("category") Category category,
                                           @ModelAttribute("UserPrincipal") User userPrincipal,
                                           @PathVariable(name = "id") int postId, Principal principal, Model model) {
        if (commentDTO.getContent() != null) {
            Comment newComment = new Comment();
            newComment.setContent(commentDTO.getContent());
            newComment.setPost(postService.getOne(postId));
            newComment.setUser(userService.getUserByUserName(principal.getName()));
            commentService.save(newComment);
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            return "redirect:/posts/" + postId + "#leaveComment";
        }

        if (user.getId() != 0) {
            List<Post> postsOfUser = postService.findAllByUser
                    (userService.getUserById(user.getId()).getUsername());
            model.addAttribute("posts", postsOfUser);
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "allPosts";
        }
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/newPost")
    public String newPostData(Model model) {
        PostDTO post = new PostDTO();
        model.addAttribute("post", post);
        return "newPost";
    }

    @GetMapping("/{id}/postImage")
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
        Post newPost = postFactory.createPostFromDTO(post);
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

    @GetMapping("/edit/{id}")
    public String editPostData(Model model, @PathVariable(name = "id") int postId, Principal principal) {
        //Bug! If you remove boolean principalAdmin the Authority Admin will not work. Admin won't be able to edit posts
        boolean principalAdmin = userService.getUserByUserName
                (principal.getName()).getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        Post postToEdit = postService.getOne(postId);
        model.addAttribute("post", postToEdit);
        model.addAttribute("postDTO", new PostDTO());
        if (principal == null) {
            model.addAttribute("error", "You can only edit your posts");
            return "redirect:/posts/" + postId;
        }
        if (!(postToEdit.getUser().getUsername().equals(principal.getName()) ||
                userService.getUserByUserName(principal.getName()).getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("error", "You can only edit your posts");
            return "redirect:/posts/" + postId;
        }
        return "postEdit";
    }

    @PostMapping("/edit/{id}")
    public String editPost(Model model, @PathVariable(name = "id") int postId,
                           @ModelAttribute("post") Post postToEdit,
                           @ModelAttribute("postDTO") PostDTO postDTO, Principal principal,
                           @RequestParam("imagefile") MultipartFile file) throws IOException {
        postDTO.setPicture(Base64.getEncoder().encodeToString(file.getBytes()));
        try {
            postService.editPost(postId, postDTO, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/posts/edit/" + postId;
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/delete/{id}")
    public String deletePostData(Model model, Principal principal, @PathVariable(name = "id") int postId) {
        //Bug! If you remove boolean principalAdmin the Authority Admin will not work. Admin won't be able to edit posts
        boolean principalAdmin = userService.getUserByUserName
                (principal.getName()).getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        Post postToDelete = postService.getOne(postId);
        model.addAttribute("post", postToDelete);
        if (principal == null) {
            model.addAttribute("error", "You can only delete your posts");
            return "redirect:/posts/" + postId;
        }
        if (!(postToDelete.getUser().getUsername().equals(principal.getName()) ||
                userService.getUserByUserName(principal.getName()).getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("error", "You can only delete your posts");
            return "redirect:/posts/" + postId;
        }
        boolean isConfirmed = false;
//        model.addAttribute("sourceText", "");
        model.addAttribute("postDTO2", new PostDTO2());
        return "postDelete";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(Model model, Principal principal, @PathVariable(name = "id") int postId,
                             @ModelAttribute("postDTO2") PostDTO2 postDTO2,
                             @ModelAttribute("post") Post postToDelete) {
        if (!postDTO2.isDeletedConfirmed()) {
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "redirect:/posts/" + postId;
        }
        try {
            postService.deletePost(postId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/posts/edit/" + postId;
        }
        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "postDeleteConfirmation";
    }

}

