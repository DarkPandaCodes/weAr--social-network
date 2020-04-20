package com.community.weare.Services.users;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Exceptions.ValidationEntityException;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.connections.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String TYPE = "USER";
    private final UserRepository userRepository;
    private final RequestService requestService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final UserFactory mapperHelper;
    private final PersonalInfoService personalInfoService;
    private final ExpertiseProfileService expertiseProfileService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RequestService requestService, ExpertiseProfileFactory expertiseProfileFactory, UserFactory mapperHelper,
                           PersonalInfoService personalInfoService, ExpertiseProfileService expertiseProfileService) {
        this.userRepository = userRepository;
        this.requestService = requestService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.mapperHelper = mapperHelper;
        this.personalInfoService = personalInfoService;
        this.expertiseProfileService = expertiseProfileService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public int registerUser(UserDTO userDTO) {

        User user = mapperHelper.convertDTOtoUSER(userDTO);
        if (isUserDuplicate(user)) {
            throw new DuplicateEntityException("User with this username already exist");
        }
        PersonalProfile personalProfile
                = personalInfoService.createProfile(new PersonalProfile());
        ExpertiseProfile expertiseProfile
                = expertiseProfileService.createProfile(new ExpertiseProfile());
        expertiseProfile.setCategory(userDTO.getCategory());
        user.setExpertiseProfile(expertiseProfile);
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
        return user.getUserId();

    }

    @Override
    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<User> getUserByFirstNameLastName(String name) {
        String param[] = name.split(" ");
        if (param.length == 2) {
            return userRepository.getByFirstNameLastName(param[0], param[1]);
        } else {
            return userRepository.getByFirstName(param[0]);
        }

    }

    @Override
    public List<User> getUsersByExpertise(String expertise) {
        return userRepository.getAllByExpertise(expertise);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void upgradeProfile(User user, PersonalProfile personalProfile) {
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public void deleteUser(int userId) {
        User user = userRepository.getOne(userId);
        user.setEnabled(0);
        userRepository.saveAndFlush(user);
    }

    @Override
    public boolean isUserDuplicate(User user) {
        if (userRepository.findByUsernameIs(user.getUsername()).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    @Override
    public void updateUserModel(User userToCheck, UserModel userModel) {
        User user = mapperHelper.mergeUserAndModel(userToCheck, userModel);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    @Override
    public void updateExpertise(User user,
                                ExpertiseProfile expertiseProfileNew, ExpertiseProfile expertiseProfileOld) {
        ExpertiseProfile expertiseProfileMerged =
                expertiseProfileFactory.mergeExpertProfile(expertiseProfileNew, expertiseProfileOld);
        expertiseProfileService.upgradeProfile(user, expertiseProfileMerged);
        userRepository.saveAndFlush(user);
    }

    @Override
    public UserModel getUserModelById(int id) {
        User user = userRepository.getOne(id);
        UserModel model = mapperHelper.convertUSERtoModel(user);
        return model;
    }

    @Transactional
    @Override
    public void addToFriendList(Request request) {
        if (request.isApproved()) {
            Request approvedRequest = requestService.getById(request.getId());
            User receiver = userRepository.getOne(approvedRequest.getReceiver().getUserId());
            receiver.addToFriendList(approvedRequest.getSender());
            userRepository.saveAndFlush(receiver);
            User sender = userRepository.getOne(approvedRequest.getSender().getUserId());
            sender.addToFriendList(approvedRequest.getReceiver());
            userRepository.saveAndFlush(sender);
        }
    }

    @Transactional
    @Override
    public void removeFromFriendsList(Request request) {
        Request requestToDelete = requestService.getById(request.getId());
        User receiver = userRepository.getOne(requestToDelete.getReceiver().getUserId());
        User sender = userRepository.getOne(requestToDelete.getSender().getUserId());
        receiver.removeFromFriendList(sender);
        sender.removeFromFriendList(receiver);
        userRepository.saveAndFlush(receiver);
        userRepository.saveAndFlush(sender);
    }

    @Override
    public void isProfileOwner(String principal, User user) {
        if (principal.equals(user.getUsername())) {
        } else {
            throw new InvalidOperationException("User isn't authorised");
        }
    }

    @Override
    public boolean isOwner(String principal, User user) {
        if (principal.equals(user.getUsername())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserDtoRequest getUserRequestFromUser(User user) {
        UserDtoRequest userDtoRequest = mapperHelper.convertUserToRequestDto(user);
        return userDtoRequest;
    }

    @Override
    public List<User> findByAuthorities(String role) {
        return userRepository.findByAuthorities(role);
    }
}

