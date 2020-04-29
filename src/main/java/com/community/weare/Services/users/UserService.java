package com.community.weare.Services.users;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;


public interface UserService extends UserDetailsService {

    int registerUser(User user,Category category);

//    void upgradeProfile(User user, PersonalProfile personalProfile);

    User getUserByUserName(String username);

    User getUserById(int id);

    Collection<User> getAllUsers();

    User disableEnableUser(String principal,int userId);

    boolean isUserDuplicate(User user);

    User updateUser(User user,String principal, User userToCheck);

    UserModel getUserModelById(int id);

    void addToFriendList(Request request);

    ExpertiseProfile updateExpertise(User user, ExpertiseProfile expertiseProfileMerged,String principal, User userToCheck);

    void ifNotProfileOwnerThrow(String principal, User user);

    void ifNotProfileOrAdminOwnerThrow(String principal, User user);

    void ifNotAdminThrow(String name, User user);

    boolean isOwner(String principal, User user);

    boolean isAdmin(Principal principal);

    List<User> getPublicUsersByCriteria( String name,String expertise);

    List<User> getAllUsersByCriteria( String name,String expertise);

    List<User> findByAuthorities(String role);

    void removeFromFriendsList(Request request);

    List<User> getUserByFirstNameLastName(String param);

    List<User> getUsersByExpertise(String expertise);


    Slice<User> findSliceWithUsers(int index, int size, String username, String name, User user);
}
