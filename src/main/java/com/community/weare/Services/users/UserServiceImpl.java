package com.community.weare.Services.users;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.ValidationEntityException;
import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.RoleRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.factories.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final String TYPE = "USER";
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final UserFactory mapperHelper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonalInfoService personalInfoService;
    private final ExpertiseProfileService expertiseProfileService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PersonalInfoRepository personalInfoRepository, UserFactory mapperHelper, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           PersonalInfoService personalInfoService, ExpertiseProfileService expertiseProfileService) {
        this.userRepository = userRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.mapperHelper = mapperHelper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.personalInfoService = personalInfoService;
        this.expertiseProfileService = expertiseProfileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(UserDTO userDTO) {
        try {
            User user = mapperHelper.convertDTOtoUSER(userDTO);
            checkIfUserExist(user);
            PersonalProfile personalProfile
                    = personalInfoService.createProfile(new PersonalProfile());
            ExpertiseProfile expertiseProfile
                    = expertiseProfileService.createProfile(new ExpertiseProfile());
            user.setExpertiseProfile(expertiseProfile);
            user.setPersonalProfile(personalProfile);
            userRepository.saveAndFlush(user);
        } catch (ValidationException e) {
            throw new ValidationEntityException(e.getMessage());
        }

    }

    @Override
    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile) {
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
        return null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(int userId) {

    }

    @Override
    public List<Role> getUserRoles(User user) {
        return null;
    }

    @Override
    public boolean checkIfUserExist(User user) {
        if (userRepository.findByUsernameIs(user.getUsername()).isPresent()) {
            throw new DuplicateEntityException(TYPE, "name", user.getUsername());
        } else {
            return true;
        }
    }
}
