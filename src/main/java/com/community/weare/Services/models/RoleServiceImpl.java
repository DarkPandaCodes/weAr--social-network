package com.community.weare.Services.models;

import com.community.weare.Models.Role;
import com.community.weare.Repositories.RoleRepository;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

//    @Override
//    public void saveRoleUserInDb() {
//     roleRepository.saveAndFlush(new Role("USER"));
//    }

}
