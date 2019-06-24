package com.ted.fileService;

import com.evil.corp.filesystem.IFileSystem;
import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.*;
import com.google.common.jimfs.Jimfs;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.ted.fileService.logic.SearchDescriptor;

import java.io.File;
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
import java.util.logging.Logger;

public class ApplicationModule extends AbstractModule {

    private static Logger logger = Logger.getLogger(ApplicationModule.class.getName());

    @Override
    protected void configure() {
        Properties defaults = new Properties();
        defaults.setProperty("user.first.name", "ted");
        defaults.setProperty("user.last.name", "ted");
        defaults.setProperty("user.nick.name", "teddy");
        defaults.setProperty("user.uuid", "123");
        defaults.setProperty("log.file.name", "logFile.txt");
        defaults.setProperty("filesystem.source.id", "source");
        defaults.setProperty("filesystem.target.id", "target");
        defaults.setProperty("filesystem.source.path", "ya/ya/yeah");
        defaults.setProperty("filesystem.target.path", "koko/jumbo");
        Properties props = new Properties(defaults);

        try {
            String workingDir = System.getProperty("user.dir");
            String userDetailsConfPath = System.getProperty("propertiesPath", Paths.get(workingDir).resolve("src/main/resources/application.properties").toString());
            File applicationPropertiesFile = Paths.get(workingDir).resolve(Paths.get(userDetailsConfPath)).toFile();
            props.load(new FileInputStream(applicationPropertiesFile));
            Names.bindProperties(binder(), props);
        } catch (IOException e) {
            logger.severe("Could not load config: " + e.getMessage());
            System.exit(1);
        }
    }

    @Provides @Named("LogFileSearchDescriptor")
    public SearchDescriptor<Path> getDefaultSearchDescriptor(){
        return new SearchDescriptor<Path>() {
            @Override
            public boolean match(Path entry) {
                return false;
            }
        };
    }

    @Provides
    public ILoginDetails getLoginDetails() {
        return new ILoginDetails() {
            @Override
            public byte[] getPassword() {
                return "teddyPassword".getBytes();
            }
        };
    }

    @Singleton
    private static class FileSystemProviderImpl implements IFileSystemProvider {

        private FileSystem fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());

        public FileSystemProviderImpl(@Named("filesystem.source.path") String source, @Named("filesystem.target.path") String target) {
            try {
                Path targetPath = fileSystem.getPath(target);
                Path homePath = fileSystem.getPath(source);
                Files.createDirectories(homePath);
                Files.createDirectories(targetPath);
                PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Files.createFile(homePath.resolve("logFile.txt"))));
                writer.println("test test 1 2 3");
                writer.close();
            } catch (IOException ex) {
                // ignore
            }
        }

        @Override
        public IFileSystem get(String identifier, ICredintials credentials) {
            if (!isDemoParams(identifier, credentials)) {
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

        private boolean isDemoParams(String identifier, ICredintials credentials) {
            return ("source".equals(identifier) || "target".equals(identifier))
                    && "teddy".equals(credentials.getUser().getNickName())
                    && new String(credentials.getAuthenticationBean().getToken()).compareTo("123") == 0
                    && 123L == credentials.getAuthorizationBean().getAuthorizationToken();
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
    private static class SecurityManagerImpl implements ISecurityManager {
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
        public void validateCredintials(ICredintials credentials) throws SecurityException {
            if (new Random().nextInt() % 2 == 0) {
                throw new SecurityException("Credentials not valid!");
            }
        }
    }

    @Singleton
    private static class UserImpl implements IUser {
        private final String firstName;
        private final String lastName;
        private final String nickName;
        private final String uuid;

        @Inject
        public UserImpl(@Named("user.first.name") String firstName, @Named("user.last.name") String lastName, @Named("user.nick.name") String nickName, @Named("user.uuid") String uuid) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.nickName = nickName;
            this.uuid = uuid;
        }

        @Override
        public String getFirstName() {
            return firstName;
        }

        @Override
        public String getLastName() {
            return lastName;
        }

        @Override
        public String getNickName() {
            return nickName;
        }

        @Override
        public long getUUID() {
            return Long.parseLong(uuid);
        }

        @Override
        public String toString() {
            return String.join(", ", getFirstName(), getLastName(), getNickName(), "" + getUUID());
        }

    }
}
