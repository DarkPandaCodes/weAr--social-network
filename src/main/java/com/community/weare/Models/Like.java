//package com.community.weare.Models;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "post_likes_table")
//public class Like {
//
//    @Id
//    @Column(name = "like_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int likeId;
//
//    @ManyToOne
//    @JoinColumn(name = "post_Id", referencedColumnName = "post_Id")
//    @JsonBackReference
//    private Post post;
//
//    @ManyToOne
//    @JoinColumn(name = "user_Id", referencedColumnName = "user_Id")
//    @JsonBackReference
//    private User user;
//
//    public Like() {
//    }
//
//    public int getLikeId() {
//        return likeId;
//    }
//
//    public void setLikeId(int likeId) {
//        this.likeId = likeId;
//    }
//
//    public Post getPost() {
//        return post;
//    }
//
//    public void setPost(Post post) {
//        this.post = post;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//}
