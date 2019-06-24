package com.ted.fileService.service;

import com.evil.corp.filesystem.IFileSystem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.ted.fileService.logic.SearchAlgorithm;
import com.ted.fileService.logic.SearchDescriptor;
import com.ted.fileService.utils.CommonUtils;
import com.ted.fileService.utils.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

//<editor-fold desc="violations" defaultstate="collapsed">
/**
 *
 * Class lies about its collaborates.
 * Class violates SRP & LOD
 *
 */
//</editor-fold>
/**
 *
 * @author Ted
 */
@Singleton
public class LogFileArchiveService extends FileSystemService.SourceToTargetFileSystemService<IFileSystem, Path, SearchDescriptor<Path>>{

    private static Logger logger = Logger.getLogger(LogFileArchiveService.class.getName());

    private SessionManager sourceSessionManager;
    private SessionManager targetSessionManager;
    private CommonUtils commonUtils;
    private Configuration configuration;

    @Inject
    public LogFileArchiveService(SessionManager sourceSessionManager, SessionManager targetSessionManager, @Named("sourceFileSystem") IFileSystem source, @Named("targetFileSystem") IFileSystem target, SearchAlgorithm<IFileSystem, Path, SearchDescriptor<Path>> searchAlgorithm, CommonUtils commonUtils, Configuration configuration) {
        super(source, target, searchAlgorithm);
        this.sourceSessionManager = sourceSessionManager;
        this.targetSessionManager = targetSessionManager;
        this.commonUtils = commonUtils;
        this.configuration = configuration;
    }

    @Override
    public void execute(SearchDescriptor<Path> searchDescriptor) {
        sourceSessionManager.execute(() -> {
            Optional<Path> optionalPath = searchAlgorithm.search(sourceFileSystem, searchDescriptor);
            optionalPath.ifPresent(this::doAction);
        });
    }

    @Override
    protected void doAction(Path entry) {
        targetSessionManager.execute(() -> {
            Path copyTo = createTargetFile(entry);
            copyFromSourceToTarget(entry, copyTo);
            logger.info("successfully copied: " + entry + " to: " + copyTo);
            removeSourceFile(entry);
            logger.info("removed: " + entry);
        });
    }

    private void removeSourceFile(Path entry) {
        sourceFileSystem.removeFile(entry.toString());
    }

    private void copyFromSourceToTarget(Path entry, Path copyTo) {
        targetFileSystem.copy(entry, copyTo);
    }

    private Path createTargetFile(Path entry) {
        return targetFileSystem.createFile(calculateTargetPath(entry));
    }

    private String calculateTargetPath(Path entry){
        String targetFileName = commonUtils.addRandomSuffix(String.valueOf(entry.getFileName()), "_");
        return Paths.get(configuration.getTargetFolderPath(), targetFileName).toString();
    }

}
