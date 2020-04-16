package com.community.weare.Services.connections;

import com.community.weare.Models.Connection;
import com.community.weare.Models.Request;

public interface ConnectionService {
    Connection createConnection(Request approvedRequest);
}
