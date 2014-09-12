package com.diosoft.calendar.server;

import com.diosoft.calendar.server.exception.DateTimeFormatException;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainServer {
    private static final Logger LOG = Logger.getLogger(MainServer.class);

    public static void main(String[] args) throws DateTimeFormatException {
// Start server
        LOG.info("Server starting...");
        new ClassPathXmlApplicationContext("app-context-server.xml");
        LOG.info("Server started.");
    }

}