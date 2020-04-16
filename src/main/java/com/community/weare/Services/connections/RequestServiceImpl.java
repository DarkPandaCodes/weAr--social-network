package com.community.weare.Services.connections;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService{
   private final RequestRepository requestRepository;

   @Autowired
    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Request createRequest(User sender, User receiver) {
        Request request=new Request();
        request.setSender(sender);
        request.setReceiver(receiver);

        return requestRepository.saveAndFlush(request);
    }

    @Override
    public Request updateRequest(User receiver) {
        return null;
    }

    @Override
    public List<Request> getAllRequestsForUser(User receiver) {
        return null;
    }
}
