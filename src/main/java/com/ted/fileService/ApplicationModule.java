package com.ted.fileService;

import com.evil.corp.filesystem.IFileSystem;
import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.*;
import com.google.common.jimfs.Jimfs;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        Properties defaults = new Properties();
        defaults.setProperty("user.first.name", "ted");
        defaults.setProperty("user.last.name", "ted");
        defaults.setProperty("user.nick.name", "teddy");
        defaults.setProperty("user.uuid", "123");
        Properties props = new Properties(defaults);
        try {
            String workingDir = System.getProperty("user.dir");
        String userDetailsConfPath = System.getProperty("propertiesPath", Paths.get(workingDir).resolve("src/main/resources/application.properties").toString());
        File userDetailsFile = Paths.get(workingDir).resolve(Paths.get(userDetailsConfPath)).toFile();
            props.load(new FileInputStream("my.properties"));
            Names.bindProperties(binder(), props);
        } catch (IOException e) {
            logger.error("Could not load config: ", e);
            System.exit(1);
        }
    }
    }

    @Provides
    public IUser getUser(){
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

    @Provides @Singleton
    public ISecurityManager getSecurityManager(){
        return new SecurityManagerImpl();
    }

    @Provides @Singleton
    public IFileSystemProvider getFileSystemProvider(){
        return new FileSystemProviderImpl();
    }

    @Provides
    public ILoginDetails getLoginDetails(){
        return new ILoginDetails() {
            @Override
            public byte[] getPassword() {
                return "teddyPassword".getBytes();
            }
        };
    }

    private class FileSystemProviderImpl implements IFileSystemProvider {

        private FileSystem fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());

        public FileSystemProviderImpl() {
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

    @Singleton
    private class SecurityManagerImpl implements ISecurityManager {
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
    }
}
