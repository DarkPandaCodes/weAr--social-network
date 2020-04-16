package com.community.weare.Services.users;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UserService extends UserDetailsService {

    int registerUser(UserDTO userDTO);

    PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile);

    User getUserByUserName(String username);

    User getUserById(int id);

    Collection<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(int userId);

    List<Role> getUserRoles(User user);

    boolean checkIfUserExist(User user);

    void updateUserModel(User userToCheck,UserModel user);

    UserModel getUserModelById(int id);

    void addToFriendList(Request request);

    void updateExpertise(User user, ExpertiseProfile expertiseProfileNew, ExpertiseProfile expertiseProfileOld);
}
