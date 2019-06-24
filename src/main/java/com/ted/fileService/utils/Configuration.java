package com.ted.fileService.utils;

import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.ILoginDetails;
import com.evil.corp.security.ISecurityManager;
import com.evil.corp.security.IUser;
import com.ted.fileService.ApplicationModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Ted
 */
public class Configuration {
    
    private static Logger logger = Logger.getLogger(Configuration.class.getName());
    
    
//    static {
//        Runtime.getRuntime().addShutdownHook(new Thread("shutDownHook"){
//            @Override
//            public void run() {
//                getFileSystemProvider().closeAllFileSystems();
//                logger.info("Number of processed log files: " + getProcessedCounter());
//            }
//
//        });
//    }

    public Configuration(String homeFolderPath, String targetFolderPath, String sourceFileSystemIdentifier, String targetFileSystemIdentifier, String sourceFileName, Path configFilePath) {
        this.homeFolderPath = homeFolderPath;
        this.targetFolderPath = targetFolderPath;
        this.sourceFileSystemIdentifier = sourceFileSystemIdentifier;
        this.targetFileSystemIdentifier = targetFileSystemIdentifier;
        this.sourceFileName = sourceFileName;
    }

    private String homeFolderPath;
    private String targetFolderPath;
    private String sourceFileSystemIdentifier;
    private String targetFileSystemIdentifier;
    private String sourceFileName;

    public String getHomeFolderPath() {
        return homeFolderPath;
    }

    public String getTargetFolderPath() {
        return targetFolderPath;
    }
    
    public  String getSourceFileName(){
        return sourceFileName;
    }

}
