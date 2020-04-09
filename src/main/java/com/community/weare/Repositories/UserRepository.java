package com.community.weare.Repositories;



import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

User findByUsername(String username);

Optional<User>findByUsernameIs(String name);

}
