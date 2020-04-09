package com.community.weare.Services.users;

import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.User;

public interface ExpertiseProfileService {
    ExpertiseProfile upgradeProfile(User user, ExpertiseProfile expertiseProfile);
    ExpertiseProfile createProfile(ExpertiseProfile expertiseProfile);
}
