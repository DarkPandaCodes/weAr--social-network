package com.community.weare.Services.connections;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class RequestServiceImpl implements RequestService{
   private final RequestRepository requestRepository;

   @Autowired
    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional
    @Override
    public Request createRequest(User sender, User receiver) {
        Request request=new Request();
        request.setSender(sender);
        request.setReceiver(receiver);
        return requestRepository.saveAndFlush(request);
    }

    @Transactional
    @Override
    public Request approveRequest(int id) {
       Request requestApproved=requestRepository.getOne(id);
       requestApproved.setApproved(true);
       requestRepository.saveAndFlush(requestApproved);
        return requestApproved;
    }

    @Transactional
    @Override
    public void deleteRequest(Request request) {
    requestRepository.delete(request);
    }

    @Override
    public Collection<Request> getAllRequestsForUserUnSeen(User receiver) {
       Collection<Request> requests=requestRepository.findRequestsByReceiverIsAndSeenFalse(receiver);
       if (requests.isEmpty()){
           requests.forEach(r->r.setSeen(true) );
           requests.forEach(r->requestRepository.saveAndFlush(r));
       }
        return requests;
    }

    @Override
    public Collection<Request> getAllRequestsForUserSeen(User receiver) {
        return requestRepository.findRequestsByReceiverIsAndSeenTrue(receiver);
    }

    @Override
    public Collection<Request> getAllRequestsForUser(User receiver) {
        return requestRepository.findRequestsByReceiverIsAndApprovedIsFalse(receiver);
    }

    @Override
    public Request getById(Integer id) {
        return requestRepository.getOne(id);
    }

    @Override
    public Request getByUsers(User receiver,User sender) {
        return requestRepository.
                findRequestsByUsersUnApproved(receiver.getUserId(),sender.getUserId());
    }

    @Override
    public Request getByUsersApproved(User receiver, User sender) {
       Set<User> users=new HashSet<>();
       users.add(receiver);
       users.add(sender);
       if (requestRepository.findRequestByUsers(users).isApproved()){
           return requestRepository.findRequestByUsers(users);
       }
        throw new EntityNotFoundException("Request not found");
    }
}
