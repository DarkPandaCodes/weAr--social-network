package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "comments_table")
public class Comment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    @ManyToOne
    @JoinColumn(name = "post_Id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    @Column(name = "content")
    private String content;

    @Column(name = "likes")
    private int likes = 0;
    //commentId	postId	userId	content	int likes??


    public Comment() {
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
