package com.community.weare.Repositories;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RequestRepository extends JpaRepository<Request,Integer> {
    Collection<Request>findRequestsByReceiverIsAndSeenFalse(User receiver);
    Collection<Request>findRequestsByReceiverIsAndSeenTrue(User receiver);
}
