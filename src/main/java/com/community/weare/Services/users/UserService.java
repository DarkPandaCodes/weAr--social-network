package com.community.weare.Services.users;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UserService extends UserDetailsService {

    void registerUser(UserDTO userDTO);

    PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile);

    User getUserByUserName(String username);

    Optional<User> getUserById(int id);

    Collection<User> getAllUsers();

    void updateUser(User user);

    void deleteUser(int userId);

    List<Role> getUserRoles(User user);

    boolean checkIfUserExist(User user);

}
