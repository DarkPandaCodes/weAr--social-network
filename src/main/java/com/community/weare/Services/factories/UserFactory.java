package com.community.weare.Services.factories;

import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.RoleRepository;
import com.community.weare.Repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserFactory {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserFactory(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder, RoleRepository roleRepository,UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository=userRepository;
    }

    public User convertDTOtoUSER(UserDTO userDTO){
        User user=modelMapper.map(userDTO,User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAuthorities(new HashSet<>(roleRepository.findByAuthority(userDTO.getAuthorities().get(0))));
        user.setEnabled(1);
        return user;
    }



    public UserDTO convertUSERtoDTO(User user){
        UserDTO userDTO=modelMapper.map(user,UserDTO.class);
        userDTO.setAuthorities(Arrays.asList(user.getAuthorities().toString()));
        return userDTO;
    }
    public UserModel convertUSERtoModel(User user){
        UserModel userModel=modelMapper.map(user, UserModel.class);
        List<String>roles=new ArrayList<>();
        List<Role>roles1=roleRepository.findByUsersIs(user);
        PersonalProfile personalProfile=user.getPersonalProfile();
//        ExpertiseProfile expertiseProfile=user.getExpertiseProfile();
//
        for (Role role:roles1
             ) {
            roles.add(role.toString());
        }

       //TODO get roles
        userModel.setAuthorities(roles);
        userModel.setFirstName(personalProfile.getFirstName());
        userModel.setLastNAme(personalProfile.getLastName());
        return userModel;
    }
}
