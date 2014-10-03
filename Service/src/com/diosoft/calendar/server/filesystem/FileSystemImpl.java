package com.diosoft.calendar.server.filesystem;

import com.diosoft.calendar.server.common.Event;
import com.diosoft.calendar.server.exception.DateTimeFormatException;

import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileSystemImpl implements FileSystem{

    final private JAXBHelperImpl jaxbHelper;
    final private String pathToEvents;

    public FileSystemImpl(JAXBHelperImpl jaxbHelper, String pathToEvents) {
        this.jaxbHelper = jaxbHelper;
        this.pathToEvents = pathToEvents;
    }

    @Override
    public void write(Event event) throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append(pathToEvents).append(event.getId()).append(".xml");
        Path filePath = Paths.get(sb.toString());
        Charset charset = Charset.forName("UTF-8");
        BufferedWriter writer = Files.newBufferedWriter(filePath, charset);
        jaxbHelper.write(event, writer);
        writer.close();
    }

    @Override
    public Event read(UUID id) throws DateTimeFormatException, IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        sb.append(pathToEvents).append(id).append(".xml");
        return read(Paths.get(sb.toString()));
    }

    @Override
    public Event read(Path pathToFile) throws DateTimeFormatException, IOException, JAXBException {
        return jaxbHelper.read(Files.newBufferedReader(pathToFile));
    }

    @Override
    public boolean delete(UUID id) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(pathToEvents).append(id).append(".xml");
        Path path = Paths.get(sb.toString());

        Files.delete(path);
        return true;
    }

    @Override
    public List<Event> readAllEventsFromXMLResources() throws IOException, DateTimeFormatException {
        EventFileVisitor eventFileVisitor = new EventFileVisitor();
        Files.walkFileTree(Paths.get(pathToEvents), eventFileVisitor);
        return eventFileVisitor.getEventList();
    }
}
