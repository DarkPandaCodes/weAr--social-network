package com.community.weare;

import com.community.weare.Models.Post;

public class FactoryPostComment {
    static int id = 1;
    public FactoryPostComment() {
    }

    public static Post createPost() {
        Post post = new Post();
        post.setPostId(id);
        id++;
        post.setUser(Factory.createUser());
        StringBuilder sb = new StringBuilder("TestContent");
        post.setContent(sb.toString());
        sb.append("a");
        post.setPublic(true);
        return post;
    }
}
