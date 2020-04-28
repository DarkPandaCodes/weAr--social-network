package com.community.weare.Services.users;


import com.community.weare.Exceptions.*;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Repositories.ExpertiseRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.connections.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final String TYPE = "USER";
    private final UserRepository userRepository;
    private final UserFactory mapperHelper;
    private final ExpertiseRepository expertiseRepository;
    private final PersonalInfoRepository personalInfoRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserFactory mapperHelper,
                           ExpertiseRepository expertiseRepository, PersonalInfoRepository personalInfoRepository) {
        this.userRepository = userRepository;
        this.mapperHelper = mapperHelper;
        this.expertiseRepository = expertiseRepository;
        this.personalInfoRepository = personalInfoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(new EntityNotFoundException());
    }

    @Transactional
    @Override
    public int registerUser(User user, Category category) {

        if (isUserDuplicate(user)) {
            throw new DuplicateEntityException("User with this username already exist");
        }
        PersonalProfile personalProfile
                = personalInfoRepository.saveAndFlush(new PersonalProfile());
        ExpertiseProfile expertiseProfile
                = expertiseRepository.saveAndFlush(new ExpertiseProfile());
        if (expertiseProfile != null) {
            expertiseProfile.setCategory(category);
        }
        user.setExpertiseProfile(expertiseProfile);
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
        return user.getUserId();

    }

    @Override
    public User getUserByUserName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(new EntityNotFoundException("User", "username", username));
        return user;
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
        return userRepository.findById(id).orElseThrow(new EntityNotFoundException("User not found!"));
    }

    @Override
    public Collection<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User disableEnableUser(String principal, int userId) {
        User user = userRepository.getOne(userId);
        ifNotAdminThrow(principal, user);
        if (!user.isEnabled()) {
            user.setEnabled(1);
        } else {
            user.setEnabled(0);
        }
        return userRepository.saveAndFlush(user);
    }

    @Override
    public boolean isUserDuplicate(User user) {
        return userRepository.findByUsernameIs(user.getUsername()).isPresent();
    }

    @Transactional
    @Override
    public User updateUser(User user, String principal, User userToCheck) {
        ifNotProfileOrAdminOwnerThrow(principal, userToCheck);
        return userRepository.saveAndFlush(user);
    }

    @Transactional
    @Override
    public ExpertiseProfile updateExpertise(User user,
                                ExpertiseProfile expertiseProfileMerged, String principal, User userToCheck) {
        ifNotProfileOrAdminOwnerThrow(principal, userToCheck);
        ExpertiseProfile profileDB = expertiseRepository.
                findById(user.getExpertiseProfile().getId()).orElseThrow(new EntityNotFoundException());
        expertiseProfileMerged.setId(profileDB.getId());
        return expertiseRepository.saveAndFlush(expertiseProfileMerged);
    }

    @Override
    public UserModel getUserModelById(int id) {
        User user = userRepository.getOne(id);
        UserModel model = mapperHelper.convertUSERtoModel(user);
        return model;
    }

    @Transactional
    @Override
    public void addToFriendList(Request approvedRequest) {
        if (approvedRequest.isApproved()) {
//            Request approvedRequest = requestService.getById(request.getId());
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
//        Request requestToDelete = requestService.getById(request.getId());
        User receiver = userRepository.getOne(request.getReceiver().getUserId());
        User sender = userRepository.getOne(request.getSender().getUserId());
        receiver.removeFromFriendList(sender);
        sender.removeFromFriendList(receiver);
        userRepository.saveAndFlush(receiver);
        userRepository.saveAndFlush(sender);
    }

    @Override
    public void ifNotProfileOwnerThrow(String principal, User user) {
        if (!principal.equals(user.getUsername())) {
            throw new InvalidOperationException("User isn't authorised");
        }
    }

    @Override
    public void ifNotProfileOrAdminOwnerThrow(String principal, User user) {
        User admin = userRepository.findByUsername(principal)
                .orElseThrow(new EntityNotFoundException("User not found"));
        if (!(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                user.getUsername().equals(principal))) {
            throw new InvalidOperationException("User isn't authorised");
        }
    }

    @Override
    public void ifNotAdminThrow(String name, User user) {
        User admin = userRepository.findByUsername(name)
                .orElseThrow(new EntityNotFoundException("User not found"));
        if (admin.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new InvalidOperationException("User isn't authorised");
        }
    }

    @Override
    public boolean isOwner(String principal, User user) {
        return principal.equals(user.getUsername());
    }

    @Override
    public boolean isAdmin(Principal principal) {
        User admin = userRepository.findByUsername(principal
                .getName()).orElseThrow(new EntityNotFoundException("This user doesn't exist"));
        return admin.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public List<User> findByAuthorities(String role) {
        return userRepository.findByAuthorities(role);
    }

    @Override
    public List<User> getPublicUsersByCriteria(String name, String expertise) {
        if (name != null && expertise == null) {
            return getUserByFirstNameLastName(name).
                    stream().filter(user -> user.getPersonalProfile().isPicturePrivacy()).
                    collect(Collectors.toList());
        } else if (name == null && expertise != null) {
            return getUsersByExpertise(expertise).
                    stream().filter(user -> user.getPersonalProfile().isPicturePrivacy()).
                    collect(Collectors.toList());
        } else {
            List<User> users = getUserByFirstNameLastName(name).
                    stream().filter(user -> user.getPersonalProfile().isPicturePrivacy()).
                    collect(Collectors.toList());
            return users.stream().
                    filter(u -> u.getExpertiseProfile()
                            .getCategory().getName().equals(expertise)).
                    collect(Collectors.toList());
        }
    }
}

