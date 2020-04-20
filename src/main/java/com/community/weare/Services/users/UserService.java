package com.community.weare.Services.users;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;


public interface UserService extends UserDetailsService {

    int registerUser(User user,Category category);

//    void upgradeProfile(User user, PersonalProfile personalProfile);

    User getUserByUserName(String username);

    User getUserById(int id);

    Collection<User> getAllUsers();


    User disableUser(int userId);


    boolean isUserDuplicate(User user);

    void updateUser(User user);

    UserModel getUserModelById(int id);

    void addToFriendList(Request request);

    void updateExpertise(User user, ExpertiseProfile expertiseProfileMerged);

    void isProfileOwner(String principal, User user);

    boolean isOwner(String principal, User user);


//    UserDtoRequest getUserRequestFromUser(User user);

    List<User> findByAuthorities(String role);

    void removeFromFriendsList(Request request);

    List<User> getUserByFirstNameLastName(String param);

    List<User> getUsersByExpertise(String expertise);
}
