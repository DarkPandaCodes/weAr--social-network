package com.community.weare.Services.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.RoleRepository;
import com.community.weare.Repositories.SkillCategoryRepository;
import com.community.weare.Repositories.SkillRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class UserFactory {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SkillCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final PersonalProfileFactory personalProfileFactory;

    public UserFactory(ModelMapper modelMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository, SkillCategoryRepository categoryRepository, SkillRepository skillRepository,
                       ExpertiseProfileFactory expertiseProfileFactory, PersonalProfileFactory personalProfileFactory) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.personalProfileFactory = personalProfileFactory;
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
        PersonalProfile personalProfile=user.getPersonalProfile();
        ExpertiseProfile expertiseProfile=user.getExpertiseProfile();
        userModel.setFirstName(personalProfile.getFirstName());
        userModel.setLastNAme(personalProfile.getLastName());
        userModel.setBirthYear(personalProfile.getBirthYear());
        userModel.setGender(personalProfile.getSex());
        userModel.setCity(personalProfile.getLocation().getCity().getCity());
        userModel.setExpertise(expertiseProfile.getCategory().getCategory());

        for (Skill skill:expertiseProfile.getSkills()
             ) {
            userModel.setSkill(skillRepository.getOne(skill.getSkillId()).getSkill());
        }
        userModel.setStartTime(expertiseProfile.getStartTime());
        userModel.setEndTime(expertiseProfile.getEndTime());
        return userModel;
    }
    @Transactional
    public User convertModelToUser(UserModel userModel) {
        User user=userRepository.getOne(userModel.getId());
        user.setEmail(userModel.getEmail());
        user.getPersonalProfile().setBirthYear(userModel.getBirthYear());
        Location location=personalProfileFactory.createLocation(userModel.getCity());
        user.getPersonalProfile().setLocation(location);
        user.getPersonalProfile().setSex(userModel.getGender());
        user.getPersonalProfile().setPersonalReview(userModel.getPersonalReview());
        user.getPersonalProfile().setFirstName(userModel.getFirstName());
        user.getPersonalProfile().setLastName(userModel.getLastNAme());
        List<Skill>skills=new ArrayList<>();
        for (String skill:userModel.getSkills()
        ) {
            if (!skillRepository.findSkillBySkill(skill).isPresent()){
                skills.add(new Skill(skill));
            }
        }
        user.getExpertiseProfile().setSkills(skills);
        user.getExpertiseProfile().setCategory(categoryRepository.findByCategory(userModel.getExpertise()).get());
//        userModel.getSkills()
//                .forEach(s-> {user.getExpertiseProfile().setSkill(skillRepository.findSkillBySkill(s).get());});
//        user.getExpertiseProfile().setCategory(categoryRepository.findByCategory(userModel.getExpertise()).get());
        return user;

    }
    public UserModel mergeUserModels(UserModel model, UserModel userModel) {
        model.setEmail(getNotNull(model.getEmail(),userModel.getEmail()));
        model.setGender(getNotNull(model.getGender(),userModel.getGender()));
        model.setFirstName(getNotNull(model.getFirstName(),userModel.getFirstName()));
        model.setLastNAme(getNotNull(model.getLastNAme(),userModel.getLastNAme()));
        model.setBirthYear(getNotNull(model.getBirthYear(),userModel.getBirthYear()));
        model.setCity(getNotNull(model.getCity(),userModel.getCity()));
        model.setExpertise(getNotNull(model.getCity(),userModel.getCity()));
        model.setSkills(getNotNull(model.getSkills(),userModel.getSkills()));
         return model;

    }

    public <T> T getNotNull(T n, T l) {

        if (n==null || n.hashCode()==0 ){
            return l;
        }
        if (n!=null || n.hashCode()!=0 ){
            return n;
        }

        return n != null && l != null && !n.equals(l) ? n : l;

    }
}
