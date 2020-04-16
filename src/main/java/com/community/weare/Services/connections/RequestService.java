package com.community.weare.Services.connections;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;

import java.util.List;

public interface RequestService {

    Request createRequest(User sender,User receiver);
    Request updateRequest(User receiver);
    List<Request> getAllRequestsForUser(User receiver);

}
