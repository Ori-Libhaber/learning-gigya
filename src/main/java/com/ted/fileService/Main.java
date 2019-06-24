package com.ted.fileService;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ted.fileService.logic.SearchDescriptor;
import com.ted.fileService.service.ExecutionDecider;
import com.ted.fileService.service.FileSystemService;
import com.ted.fileService.service.LogFileArchiveService;
import com.ted.fileService.utils.Configuration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Ted
 */
public class Main {
    public static void main(String[] args) {

        // getting logical units
        Injector injector = Guice.createInjector(new ApplicationModule());
        LogFileArchiveService fileArchiveService = injector.getInstance(LogFileArchiveService.class);
        SearchDescriptor searchDescriptor = injector.getInstance(SearchDescriptor.class);
        ExecutionDecider executionDecider = injector.getInstance(ExecutionDecider.class);

        // executing main task
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(() -> {
            if(executionDecider.canExecute()) {
                fileArchiveService.execute(searchDescriptor);
            }
        }, 5L, 10L, TimeUnit.SECONDS);
    }
}
