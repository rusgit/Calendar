package com.diosoft.calendar.server;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainServer {
    private static final Logger LOG = Logger.getLogger(MainServer.class);

    public static void main(String[] args) {
// Start server
        LOG.info("Server starting...");
        new ClassPathXmlApplicationContext("app-context-server.xml");
        LOG.info("Server started.");
    }
}