package com.community.weare.Controllers.REST;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.ValidationEntityException;
import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.PersonalProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.PersonalProfileFactory;
import com.community.weare.Services.users.ExpertiseProfileService;
import com.community.weare.Services.users.PersonalInfoService;
import com.community.weare.Services.users.UserService;
import com.community.weare.Models.factories.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

import static com.community.weare.utils.HttpMessages.ERROR_NOT_FOUND_MESSAGE_FORMAT;

@RestController
@RequestMapping("/api/users")
public class RESTUserController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final PersonalInfoService personalInfoService;
    private final ExpertiseProfileService expertiseProfileService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final PersonalProfileFactory personalProfileFactory;
    private final UserFactory mapperHelper;

    @Autowired
    public RESTUserController(UserService userService, PersonalInfoService personalInfoService, ExpertiseProfileService expertiseProfileService,
                              ExpertiseProfileFactory expertiseProfileFactory, PersonalProfileFactory personalProfileFactory, UserFactory mapperHelper) {
        this.userService = userService;
        this.personalInfoService = personalInfoService;
        this.expertiseProfileService = expertiseProfileService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.personalProfileFactory = personalProfileFactory;
        this.mapperHelper = mapperHelper;
    }

    @PostMapping("/")
    public String registerUser(@RequestBody @Valid UserDTO userDTO) {

        try {
            userService.registerUser(userDTO);
            User user = userService.getUserByUserName(userDTO.getUsername());
            return String.format("User with name %s and id %d was created",
                    user.getUsername(), user.getUserId());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PostMapping("/{id}/personal")
    public PersonalProfile upgradeUserPersonalProfile(@PathVariable(name = "id") int id,
                                                      @RequestBody @Valid PersonalProfileDTO personalProfileDTO) {
        try {
            User user = userService.getUserById(id);

            PersonalProfile personalProfile = personalProfileFactory.covertDTOtoPersonalProfile(personalProfileDTO);
            return personalInfoService.upgradeProfile(user, personalProfile);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,("User not found"));
        }

    }
    @PostMapping("/{id}/expertise")
    public ExpertiseProfile upgradeUserExpertiseProfile(@PathVariable(name = "id") int id,
                                                       @RequestBody @Valid ExpertiseProfileDTO expertiseProfileDTO) {
        try {
            User user = userService.getUserById(id);

            ExpertiseProfile expertiseProfileNEW= expertiseProfileFactory.convertDTOtoExpertiseProfile(expertiseProfileDTO);
            ExpertiseProfile expertiseProfile= expertiseProfileFactory.mergeExpertProfile(expertiseProfileNEW,user.getExpertiseProfile());
            return expertiseProfileService.upgradeProfile(user,expertiseProfile);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ValidationEntityException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,("User not found"));
        }

    }

    @GetMapping("/")
    public UserModel getUserById(@RequestHeader(name = "id") int id) {
       try {
           User user = userService.getUserById(id);
           return mapperHelper.convertUSERtoModel(user);
        }catch (EntityNotFoundException e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
    }}

}
