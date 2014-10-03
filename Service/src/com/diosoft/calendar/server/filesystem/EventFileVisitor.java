package com.diosoft.calendar.server.filesystem;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.exception.DateTimeFormatException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class EventFileVisitor extends SimpleFileVisitor<Path> {

    private final List<Event> eventList = new ArrayList<Event>();
    private final JAXBHelper jaxbHelper = new JAXBHelperImpl();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
    {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.xml");
        if (attrs.isRegularFile() && matcher.matches(file.getFileName())) {
            try {
                eventList.add(jaxbHelper.read(Files.newBufferedReader(file)));
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (DateTimeFormatException e) {
                e.printStackTrace();
            }
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Event> getEventList() {
        return eventList;
    }
}