package com.community.weare.Repositories;



import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

User findByUsername(String username);


}
