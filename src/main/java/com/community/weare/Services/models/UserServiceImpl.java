package com.community.weare.Services.models;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.RoleRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.models.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PersonalInfoRepository personalInfoRepository,
                           ModelMapper modelMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public void registerUser(UserDTO userDTO) {
        User user=modelMapper.map(userDTO,User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAuthorities(new HashSet<>(roleRepository.findByAuthority("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }

    @Override
    public PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile) {
        user.setPersonalProfile(personalProfile);
        userRepository.saveAndFlush(user);
        return null;
    }
}
