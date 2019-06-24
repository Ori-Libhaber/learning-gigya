package com.ted.fileService.utils;

import com.evil.corp.filesystem.IFileSystem;
import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.IAuthenticationBean;
import com.evil.corp.security.IAuthorizationBean;
import com.evil.corp.security.ICredintials;
import com.evil.corp.security.ILoginDetails;
import com.evil.corp.security.ISecurityManager;
import com.evil.corp.security.IUser;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ted
 */
public class ConfigManager {
    
    private static Logger logger = Logger.getLogger(ConfigManager.class.getName());
    
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread("shutDownHook"){
            @Override
            public void run() {
                getFileSystemProvider().closeAllFileSystems();
                logger.info("Number of processed log files: " + getProcessedCounter());
            }
            
        });
    }
    
    private static String homeFolderPath;
    private static String targetFolderPath;
    private static String sourceFileSystemIdentifier;
    private static String targetFileSystemIdentifier;
    private static String sourceFileName;
    private static volatile int processCounter = 0;

    public static IUser getUser(File configFile) {
        try {
            List<String> lines = Files.readAllLines(configFile.toPath());
            return new IUser() {
                @Override
                public String getFirstName() {
                    return lines.get(0);
                }
                
                @Override
                public String getLastName() {
                    return lines.get(1);
                }
                
                @Override
                public String getNickName() {
                    return lines.get(2);
                }
                
                @Override
                public long getUUID() {
                    return Long.parseLong(lines.get(3));
                }

                @Override
                public String toString() {
                    return String.join(", ", getFirstName(), getLastName(), getNickName(), "" + getUUID());
                }
                
            };
        } catch (IOException ex) {
            // ignore
        }
        return null;
    }

    public static ILoginDetails getLoginDetails(IUser user) {
        switch (user.getNickName()) {
            case "teddy":
                return new ILoginDetails() {
                    @Override
                    public byte[] getPassword() {
                        return "teddyPassword".getBytes();
                    }
                };
                default:
                    return new ILoginDetails() {
                        @Override
                        public byte[] getPassword() {
                            return "otherPassword".getBytes();
                        }
                    };
        }
    }
    
    public static void incProcessed(){
        processCounter++;
    }
    
    public static int getProcessedCounter(){
        return processCounter;
    }
    
    public static ISecurityManager getSecurityManager(){
        return getDefaultISecurityManager();
    }

    public static String getHomeFolderPath() {
        return homeFolderPath;
    }

    public static String getTargetFolderPath() {
        return targetFolderPath;
    }

    public static void setTargetFolderPath(String targetFolderPath) {
        ConfigManager.targetFolderPath = targetFolderPath;
    }

    public static void setHomeFolderPath(String homeFolderPath) {
        ConfigManager.homeFolderPath = homeFolderPath;
    }

    public static String getSourceFileSystemIdentifier() {
        return sourceFileSystemIdentifier;
    }

    public static void setSourceFileSystemIdentifier(String sourceFileSystemIdentifier) {
        ConfigManager.sourceFileSystemIdentifier = sourceFileSystemIdentifier;
    }

    public static String getTargetFileSystemIdentifier() {
        return targetFileSystemIdentifier;
    }

    public static void setTargetFileSystemIdentifier(String targetFileSystemIdentifier) {
        ConfigManager.targetFileSystemIdentifier = targetFileSystemIdentifier;
    }
    
    public static IFileSystemProvider getFileSystemProvider() {
        return new FileSystemProvider();
    }
    
    public static String getSourceFileName(){
        return sourceFileName;
    }

    public static void setSourceFileName(String sourceFileName) {
        ConfigManager.sourceFileName = sourceFileName;
    }
    
    private static ISecurityManager getDefaultISecurityManager() {
        return new ISecurityManager() {
            @Override
            public ICredintials createCredintials(IUser user, ILoginDetails loginDetails) throws SecurityException {
                if ("teddy".equals(user.getNickName()) && new String(loginDetails.getPassword()).compareTo("teddyPassword") == 0) {
                    return new ICredintials() {
                        @Override
                        public IAuthenticationBean getAuthenticationBean() {
                            return new IAuthenticationBean() {
                                @Override
                                public byte[] getToken() {
                                    return "123".getBytes();
                                }
                            };
                        }

                        @Override
                        public IAuthorizationBean getAuthorizationBean() {
                            return new IAuthorizationBean() {
                                @Override
                                public long getAuthorizationToken() {
                                    return 123L;
                                }
                            };
                        }

                        @Override
                        public IUser getUser() {
                            return user;
                        }
                    };
                }

                throw new SecurityException("Ho no! something bad happened!");
            }

            @Override
            public void validateCredintials(ICredintials credintials) throws SecurityException {
                if(new Random().nextInt() % 2 == 0){
                    throw new SecurityException("Credintials not valid!");
                }
            }
        };
    }

    private static class FileSystemProvider implements IFileSystemProvider {

        private static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

        public FileSystemProvider() {
            try {
                Path targetPath = fileSystem.getPath("ya", "ya", "yeah");
                Path homePath = fileSystem.getPath("koko", "jumbo");
                Files.createDirectories(homePath);
                Files.createDirectories(targetPath);
                PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Files.createFile(homePath.resolve("report.txt"))));
                writer.println("test test 1 2 3");
                writer.close();
            } catch (IOException ex) {
                // ignore
            }
        }

        @Override
        public IFileSystem get(String identifier, ICredintials credintials) {
            if (!isDemoParams(identifier, credintials)) {
                return null;
            }

            return new IFileSystem() {
                @Override
                public Path getPath(String path) {
                    Path thePath = fileSystem.getPath(path);
                    if (Files.exists(thePath)) {
                        return thePath;
                    }
                    return null;
                }

                @Override
                public boolean copy(Path from, Path to) {
                    if (Files.exists(from) && Files.exists(to)) {
                        try {
                            Files.copy(from, to);
                            return true;
                        } catch (IOException ex) {
                            // ignore
                        }
                        return false;
                    }
                    return false;
                }

                @Override
                public Path createFile(String path) {
                    Path target = fileSystem.getPath(path);
                    if (!Files.exists(target.getParent())) {
                        return null;
                    }
                    try {
                        return Files.createFile(target);
                    } catch (IOException ex) {
                        // ignore
                    }
                    return null;
                }

                @Override
                public Path createFolder(String path) {
                    Path target = fileSystem.getPath(path);
                    if (!Files.exists(target.getParent())) {
                        return null;
                    }
                    try {
                        return Files.createDirectory(target);
                    } catch (IOException ex) {
                        // ignore
                    }
                    return null;
                }

                @Override
                public boolean removeFile(String path) {
                    Path target = fileSystem.getPath(path);
                    if (!Files.exists(target) || !Files.isRegularFile(target)) {
                        return false;
                    }
                    try {
                        return Files.deleteIfExists(target);
                    } catch (IOException ex) {
                        // ignore
                    }
                    return false;
                }
            };
        }

        private boolean isDemoParams(String identifier, ICredintials credintials) {
            return ("source".equals(identifier) || "target".equals(identifier))
                    && "teddy".equals(credintials.getUser().getNickName())
                    && new String(credintials.getAuthenticationBean().getToken()).compareTo("123") == 0
                    && 123L == credintials.getAuthorizationBean().getAuthorizationToken();
        }

        @Override
        public void closeAllFileSystems() {
            try {
                fileSystem.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }
    
}
