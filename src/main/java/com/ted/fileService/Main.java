package com.ted.fileService;

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
        Configuration.setHomeFolderPath("koko/jumbo");
        Configuration.setTargetFolderPath("ya/ya/yeah");
        Configuration.setSourceFileSystemIdentifier("source");
        Configuration.setTargetFileSystemIdentifier("target");
        Configuration.setSourceFileName("report.txt");
        FileSystemService fileService = LogFileArchiveService.getInstance();
        final ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(fileService::findFile, 5L, 10L, TimeUnit.SECONDS);
        new SearchAlgorithmMultipleResults();
    }
}
