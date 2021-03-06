package com.diosoft.calendar.server.filesystem;

import com.diosoft.calendar.server.common.Event;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventFileVisitor extends SimpleFileVisitor<Path> {
    private final static String EVENT_FILE_EXTENSION_GLOB_MATCHER_PATTERN = "glob:*.xml";
    private final List<Event> eventList = new ArrayList<>();
    private final JAXBHelper jaxbHelper = new JAXBHelperImpl();
    private final List<Future<Event>> futures = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
    {
        final Path fileEvent = file;
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(EVENT_FILE_EXTENSION_GLOB_MATCHER_PATTERN);
        if (attrs.isRegularFile() && matcher.matches(fileEvent.getFileName())) {
           futures.add(executorService.submit(() -> jaxbHelper.read(Files.newBufferedReader(fileEvent))));
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Event> getEventList() throws ExecutionException, InterruptedException {
        for (Future<Event> f : futures) eventList.add(f.get());
        executorService.shutdown();
        return eventList;
    }
}