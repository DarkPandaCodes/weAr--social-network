package com.community.weare;

import com.community.weare.Models.Category;
import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;

import java.util.*;

public class Factory {

    public static User createUser(){
        User user=new User();
        user.setUsername("tedi");
        user.setPassword("123456");
        user.setEmail("tedi@abv.bg");
        user.setExpertiseProfile(new ExpertiseProfile());
        user.setPersonalProfile(new PersonalProfile());
        return user;
    }
    public static User createUser2(){
        User user=new User();
        user.setUsername("tedi1");
        user.setPassword("123456");
        user.setEmail("tedi@abv.bg");
        user.setExpertiseProfile(new ExpertiseProfile());
        user.setPersonalProfile(new PersonalProfile());
        return user;
    }
    public static UserModel createUserModel() {
        UserModel user = new UserModel();
        user.setUsername("tedi");
        user.setEmail("tedi@abv.bg");
        return user;
    }

    public ExpertiseProfileDTO createExpProfileDTO(){
        ExpertiseProfileDTO expertiseProfileDTO=new ExpertiseProfileDTO();
        List<String>skills=new ArrayList<>();
        skills.add("writing Article");
        expertiseProfileDTO.setSkills(skills);
        expertiseProfileDTO.setCategory(new Category("Marketing"));
        return expertiseProfileDTO;
    }

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        return users;
    }
}
