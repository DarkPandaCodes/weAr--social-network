package com.community.weare.Services.users;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;


public interface UserService extends UserDetailsService {

    int registerUser(UserDTO userDTO);

    void upgradeProfile(User user, PersonalProfile personalProfile);

    User getUserByUserName(String username);

    User getUserById(int id);

    Collection<User> getAllUsers();


    void deleteUser(int userId);


    boolean isUserDuplicate(User user);

    void updateUserModel(User userToCheck, UserModel user);

    UserModel getUserModelById(int id);

    void addToFriendList(Request request);

    void updateExpertise(User user, ExpertiseProfile expertiseProfileNew, ExpertiseProfile expertiseProfileOld);

    void isProfileOwner(String principal, User user);

    boolean isOwner(String principal, User user);


    UserDtoRequest getUserRequestFromUser(User user);

    List<User> findByAuthorities(String role);

    void removeFromFriendsList(Request request);

    List<User> getUserByFirstNameLastName(String param);

    List<User> getUsersByExpertise(String expertise);
}
