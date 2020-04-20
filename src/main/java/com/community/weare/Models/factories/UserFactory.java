package com.community.weare.Models.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Repositories.RoleRepository;
import com.community.weare.Repositories.SkillCategoryRepository;
import com.community.weare.Repositories.SkillRepository;
import com.community.weare.Repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.community.weare.Models.factories.FactoryUtils.getNotNull;

@Component
public class UserFactory {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PersonalProfileFactory personalProfileFactory;

    @Autowired
    public UserFactory(ModelMapper modelMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       UserRepository userRepository, SkillRepository skillRepository, PersonalProfileFactory personalProfileFactory) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.personalProfileFactory = personalProfileFactory;
    }

    public User convertDTOtoUSER(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAuthorities(new HashSet<>(roleRepository.findByAuthority("ROLE_USER")));
        user.setEnabled(1);
        return user;
    }


//    public UserDTO convertUSERtoDTO(User user) {
//        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
//        userDTO.setAuthorities(Arrays.asList(user.getAuthorities().toString()));
//        return userDTO;
//    }

    public UserModel convertUSERtoModel(User user) {
        UserModel userModel = modelMapper.map(user, UserModel.class);

        PersonalProfile personalProfile = user.getPersonalProfile();
        ExpertiseProfile expertiseProfile = user.getExpertiseProfile();

        userModel.setFirstName(personalProfile.getFirstName());
        userModel.setLastNAme(personalProfile.getLastName());
        userModel.setBirthYear(personalProfile.getBirthYear());
        userModel.setGender(personalProfile.getSex());

        if (personalProfile.getLocation() != null) {
            userModel.setCity(personalProfile.getLocation().getCity().getCity());
        }
        if (expertiseProfile.getCategory() != null) {
            userModel.setExpertise(expertiseProfile.getCategory().getName());
            for (Skill skill : expertiseProfile.getSkills()
            ) {
                userModel.setSkill(skillRepository.getOne(skill.getSkillId()).getSkill());
            }
            userModel.setStartTime(expertiseProfile.getStartTime());
            userModel.setEndTime(expertiseProfile.getEndTime());
        }


        return userModel;
    }

//    @Transactional
//    public User convertModelToUser(UserModel userModel) {
//        User user = userRepository.getOne(userModel.getId());
//        user.setEmail(userModel.getEmail());
//        user.getPersonalProfile().setBirthYear(userModel.getBirthYear());
//        Location location = personalProfileFactory.createLocation(userModel.getCity());
//        user.getPersonalProfile().setLocation(location);
//        user.getPersonalProfile().setSex(userModel.getGender());
//        user.getPersonalProfile().setPersonalReview(userModel.getPersonalReview());
//        user.getPersonalProfile().setFirstName(userModel.getFirstName());
//        user.getPersonalProfile().setLastName(userModel.getLastNAme());
//
//
//        return user;
//
//    }

//    public UserModel mergeUserModels(UserModel model, UserModel userModel) {
//        model.setEmail(getNotNull(model.getEmail(), userModel.getEmail()));
//        model.setGender(getNotNull(model.getGender(), userModel.getGender()));
//        model.setFirstName(getNotNull(model.getFirstName(), userModel.getFirstName()));
//        model.setLastNAme(getNotNull(model.getLastNAme(), userModel.getLastNAme()));
//        model.setBirthYear(getNotNull(model.getBirthYear(), userModel.getBirthYear()));
//        model.setCity(getNotNull(model.getCity(), userModel.getCity()));
//        model.setExpertise(getNotNull(model.getCity(), userModel.getCity()));
//        model.setSkills(getNotNull(model.getSkills(), userModel.getSkills()));
//        return model;
//
//    }

    @Transactional
    public UserDtoRequest convertUserToRequestDto(User user) {
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        userDtoRequest.setId(user.getUserId());
        userDtoRequest.setUsername(user.getUsername());
        return userDtoRequest;
    }

    public User mergeUserAndModel(User userToCheck, UserModel userModel) {

        User user = userRepository.getOne(userToCheck.getUserId());
        user.getPersonalProfile().setPicture(userToCheck.getPersonalProfile().getPicture());
        user.setEmail(getNotNull(userModel.getEmail(), userToCheck.getEmail()));

        user.getPersonalProfile().setBirthYear(getNotNull(userModel.getBirthYear(), userToCheck.getPersonalProfile().getBirthYear()));
        Location location = personalProfileFactory.
                createLocation(getNotNull(userModel.getCity(),
                        userToCheck.getPersonalProfile().getLocation().getCity().getCity()));

        user.getPersonalProfile().setLocation(location);

        user.getPersonalProfile().setSex(getNotNull(userModel.getGender(), userToCheck.getPersonalProfile().getSex()));

        user.getPersonalProfile().setPersonalReview(getNotNull(userModel.getPersonalReview()
                , userToCheck.getPersonalProfile().getPersonalReview()));

        user.getPersonalProfile().setFirstName(getNotNull(userModel.getFirstName(),
                userToCheck.getPersonalProfile().getFirstName()));

        user.getPersonalProfile().setLastName
                (getNotNull(userModel.getLastNAme(),userToCheck.getPersonalProfile().getLastName()));
        return user;
    }
}
