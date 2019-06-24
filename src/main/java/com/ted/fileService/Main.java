package com.ted.fileService;

import com.google.inject.Guice;
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
        LogFileArchiveService instance = Guice.createInjector(new ApplicationModule()).getInstance(LogFileArchiveService.class);
        final ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(() -> instance.execute(), 5L, 10L, TimeUnit.SECONDS);
        new SearchAlgorithmMultipleResults();
    }
}
