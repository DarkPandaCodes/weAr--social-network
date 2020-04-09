package com.community.weare.Services.factories;

import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.RoleRepository;
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


    @Autowired
    public UserFactory(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User convertDTOtoUSER(UserDTO userDTO){
        User user=modelMapper.map(userDTO,User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAuthorities(new HashSet<>(roleRepository.findByAuthority("ROLE_USER")));
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
        List<Role>roles1=roleRepository.findRoleByUsersIs(user);
        for (Role role:roles1
             ) {
            roles.add(role.toString());
        }
        userModel.setAuthorities(roles);
        return userModel;
    }
}
