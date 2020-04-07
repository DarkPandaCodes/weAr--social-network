package com.community.weare.Services.models;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;

public interface PersonalInfoService {

    PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile);

}
