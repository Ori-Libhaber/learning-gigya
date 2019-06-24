package com.ted.fileService;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ted.fileService.logic.SearchDescriptor;
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
        Injector injector = Guice.createInjector(new ApplicationModule());
        LogFileArchiveService instance = injector.getInstance(LogFileArchiveService.class);
        SearchDescriptor searchDescriptor = injector.getInstance(SearchDescriptor.class);
        final ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(() -> instance.execute(searchDescriptor), 5L, 10L, TimeUnit.SECONDS);
    }
}
