package com.community.weare.Repositories;

import com.community.weare.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role,Integer> {

Set<Role>findByAuthority(String authority);
}
