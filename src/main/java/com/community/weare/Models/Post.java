package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "posts_table")
public class Post {

    @Id
    @Column(name = "postId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @Column(name = "isPublic")
    private boolean isPublic;

    @Column(name = "content")
    private String content;

    public Post() {
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //postId	isPublic	content	picture	userId	int likes	date:hour
}
