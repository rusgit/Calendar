package com.diosoft.calendar.client;

import com.diosoft.calendar.server.exception.DateTimeFormatException;
import com.diosoft.calendar.server.exception.OrderOfArgumentsException;
import com.diosoft.calendar.server.exception.ValidationException;
import com.diosoft.calendar.server.service.CalendarService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class MainClient {

    public static void main(String[] args) throws IOException, DateTimeFormatException, OrderOfArgumentsException, ValidationException, JAXBException {
        ApplicationContext factory = new ClassPathXmlApplicationContext("app-context-client.xml");
        CalendarService calendarService = (CalendarService) factory.getBean("calendarService");

        CalendarClient calendarClient = new CalendarClient(calendarService);
        calendarClient.emulateClient();
    }
}
