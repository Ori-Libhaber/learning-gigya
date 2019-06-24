package com.ted.fileService.utils;

import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.ILoginDetails;
import com.evil.corp.security.ISecurityManager;
import com.evil.corp.security.IUser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
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

    @Inject
    public Configuration(@Named("filesystem.source.path") String homeFolderPath, @Named("filesystem.target.path") String targetFolderPath, @Named("log.file.name") String sourceFileName) {
        this.homeFolderPath = homeFolderPath;
        this.targetFolderPath = targetFolderPath;
        this.sourceFileName = sourceFileName;
    }

    private String homeFolderPath;
    private String targetFolderPath;
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
