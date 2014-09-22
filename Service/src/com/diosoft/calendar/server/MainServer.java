package com.diosoft.calendar.server;

import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.exception.ValidationException;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.rmi.RemoteException;

public class MainServer {
    private static final Logger LOG = Logger.getLogger(MainServer.class);

    public static void main(String[] args) throws DateTimeFormatException, RemoteException, OrderOfArgumentsException, ValidationException {
// Start server
        LOG.info("Server starting...");
        new ClassPathXmlApplicationContext("app-context-server.xml");
        LOG.info("Server started.");
    }
}
