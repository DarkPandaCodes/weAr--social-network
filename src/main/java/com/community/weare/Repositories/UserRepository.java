package com.community.weare.Repositories;

import com.community.weare.Models.Role;
import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.peer.LightweightPeer;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    @Query(value = "SELECT u,a FROM User as u join u.authorities as a where a.authority = ?1")
    List<User> findByAuthorities(String role);

    Optional<User> findByUsernameIs(String name);

}
