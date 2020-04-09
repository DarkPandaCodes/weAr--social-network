package com.community.weare.Services.users;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;

public interface PersonalInfoService {

    PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile);
    PersonalProfile createProfile(PersonalProfile personalProfile);
}
