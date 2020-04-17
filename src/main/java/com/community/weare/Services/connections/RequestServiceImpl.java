package com.community.weare.Services.connections;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;

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
    public Request getById(Integer id) {
        return requestRepository.getOne(id);
    }
}
