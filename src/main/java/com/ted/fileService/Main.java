package com.ted.fileService;

import com.ted.fileService.utils.ConfigManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Ted
 */
public class Main {
    public static void main(String[] args) {
        ConfigManager.setHomeFolderPath("koko/jumbo");
        ConfigManager.setTargetFolderPath("ya/ya/yeah");
        ConfigManager.setSourceFileSystemIdentifier("source");
        ConfigManager.setTargetFileSystemIdentifier("target");
        ConfigManager.setSourceFileName("report.txt");
        FileService fileService = FileService.getInstance();
        final ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(fileService::findFile, 5L, 10L, TimeUnit.SECONDS);
    }
}
