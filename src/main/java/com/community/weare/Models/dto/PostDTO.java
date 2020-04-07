package com.community.weare.Models.dto;

import com.community.weare.Models.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class PostDTO {

    private boolean isPublic;
    private String content;
    private String picture;
    private int userId;

    public PostDTO(boolean isPublic, String content, String picture, int userId) {
        this.isPublic = isPublic;
        this.content = content;
        this.picture = picture;
        this.userId = userId;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
