package com.community.weare.Services.connections;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Connection;
import com.community.weare.Models.Request;
import com.community.weare.Repositories.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionServiceImpl implements ConnectionService{
    private final ConnectionRepository connectionRepository;

    @Autowired
    public ConnectionServiceImpl(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

//    @Override
//    public Connection createConnection(Request approvedRequest) {
//        if (approvedRequest.isApproved()){
//            Connection connection=new Connection();
//
//            return connectionRepository.saveAndFlush()
//        }else {
//            throw new InvalidOperationException();
//        }
//    }

    @Override
    public Connection createConnection(Request approvedRequest) {
        return null;
    }
}
