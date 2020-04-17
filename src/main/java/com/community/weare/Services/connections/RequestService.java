package com.community.weare.Services.connections;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;

import java.util.Collection;
import java.util.List;

public interface RequestService {

    Request createRequest(User sender,User receiver);
    Request approveRequest(int id);
    Collection<Request> getAllRequestsForUserUnSeen(User receiver);
    Collection<Request> getAllRequestsForUserSeen(User receiver);

    Request getById(Integer id);
}
