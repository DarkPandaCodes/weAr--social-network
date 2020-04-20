package com.community.weare.Controllers;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.dto.CommentDTO;
import com.community.weare.Models.dto.PostDTO2;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private CommentService commentService;
    private UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/edit/{id}")
    public String editCommentData(Model model, Principal principal, @PathVariable(name = "id") int commentId) {
        Comment commentToEdit = commentService.getOne(commentId);
        //Bug! If you remove boolean principalAdmin the Authority Admin will not work. Admin won't be able to edit comments
        boolean principalAdmin = userService.getUserByUserName
                (principal.getName()).getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!(commentToEdit.getUser().getUsername().equals(principal.getName()) ||
                userService.getUserByUserName(principal.getName()).getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("error", "You can only edit your posts");
            return "redirect:/posts/" + commentToEdit.getPost().getPostId() + "#leaveComment";
        }
        model.addAttribute("commentDTO", new CommentDTO());
        return "commentEdit";
    }

    @PostMapping("/edit/{id}")
    public String editComment(Model model, Principal principal, @PathVariable(name = "id") int commentId,
                              @ModelAttribute("commentDTO") CommentDTO commentDTO) {
        Comment commentToEdit = commentService.getOne(commentId);
        try {
            commentService.editComment(commentId, commentDTO.getContent(), principal);
        } catch (IllegalArgumentException | DuplicateEntityException e) {
            model.addAttribute("error", e.getMessage());
            return "commentEdit";
        }

        if (principal != null) {
            model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
        }
        return "redirect:/posts/" + commentToEdit.getPost().getPostId() + "#leaveComment";
    }

    @GetMapping("/delete/{id}")
    public String deleteCommentData(Model model, Principal principal, @PathVariable(name = "id") int commentId) {
        //Bug! If you remove boolean principalAdmin the Authority Admin will not work. Admin won't be able to edit comments
        boolean principalAdmin = userService.getUserByUserName
                (principal.getName()).getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        Comment commentToDelete = commentService.getOne(commentId);
        if (principal == null) {
            model.addAttribute("error", "You can only delete your comments");
            return "redirect:/posts/" + commentToDelete.getPost().getPostId() + "#leaveComment";
        }
        if (!(commentToDelete.getUser().getUsername().equals(principal.getName()) ||
                userService.getUserByUserName(principal.getName()).getAuthorities().stream()
                        .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))) {
            model.addAttribute("error", "You can only delete your posts");
            return "redirect:/posts/" + commentToDelete.getPost().getPostId() + "#leaveComment";
        }
        model.addAttribute("comment", commentToDelete);
        model.addAttribute("commentDTO", new CommentDTO());
        return "commentDelete";
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(Model model, Principal principal, @PathVariable(name = "id") int commentId,
                                @ModelAttribute("commentDTO") CommentDTO commentDTO) {
        Comment commentToDelete = commentService.getOne(commentId);
        if (!commentDTO.isDeletedConfirmed()) {
            if (principal != null) {
                model.addAttribute("UserPrincipal", userService.getUserByUserName(principal.getName()));
            }
            return "redirect:/posts/" + commentToDelete.getPost().getPostId() + "#leaveComment";
        }
        try {
            commentService.deleteComment(commentId, principal);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/posts/" + commentToDelete.getPost().getPostId() + "#leaveComment";
        }
        return "commentDeleteConfirmation";
    }
}
