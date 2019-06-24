package com.ted.fileService.service;

import com.evil.corp.filesystem.IFileSystem;
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
public class LogFileArchiveService extends FileSystemService.SourceToTargetFileSystemService<IFileSystem, Path, SearchDescriptor<Path>>{

    private static Logger logger = Logger.getLogger(LogFileArchiveService.class.getName());

    private SessionManager sourceSessionManager;
    private SessionManager targetSessionManager;
    private CommonUtils commonUtils;
    private Configuration configuration;

    public LogFileArchiveService(SessionManager sourceSessionManager, SessionManager targetSessionManager, IFileSystem source, IFileSystem target, SearchAlgorithm<IFileSystem, Path, SearchDescriptor<Path>> searchAlgorithm, CommonUtils commonUtils, Configuration configuration) {
        super(source, target, searchAlgorithm);
        this.sourceSessionManager = sourceSessionManager;
        this.targetSessionManager = targetSessionManager;
        this.commonUtils = commonUtils;
        this.configuration = configuration;
    }


    /**
     * service instantiation
     * @throws SecurityException 
     */
//    private void initialize() throws SecurityException {
//        String workingDir = System.getProperty("user.dir");
//        String userDetailsConfPath = System.getProperty("userPath", Paths.get(workingDir).resolve("src/main/resources/userDetails.conf").toString());
//        File userDetailsFile = Paths.get(workingDir).resolve(Paths.get(userDetailsConfPath)).toFile();
//        IUser user = Configuration.getUser(userDetailsFile);
//        ILoginDetails loginDetails = Configuration.getLoginDetails(user);
//        this.securityManager = Configuration.getSecurityManager();
//        this.credentials = this.securityManager.createCredintials(user, loginDetails);
//        this.fileSystemProvider = Configuration.getFileSystemProvider();
//        this.sourceFileSystem = fileSystemProvider.get(Configuration.getSourceFileSystemIdentifier(), credentials);
//        this.targetFileSystem = fileSystemProvider.get(Configuration.getTargetFileSystemIdentifier(), credentials);
//    }

//    /**
//     * Find the file - do the job
//     */
//    @Override
//    public void execute() {
//        sourceSessionManager.execute(() -> {
//            searchAlgorithm.search(sourceFileSystem, s)
//        });
//        try {
//            switch (GetTimeOfDay()) {
//                case EVENING:
//                case NIGHT:
//                    String source = Configuration.getSourceFileName();
//                    logger.info("Attempting to find and copy: " + source + " to backup location");
//                    String suffix = String.valueOf(new Date().getTime());
//                    securityManager.validateCredintials(credentials);
//                    Files.list(sourceFileSystem.getPath(Configuration.getHomeFolderPath()))
//                            .filter(path -> path.getFileName().endsWith(source))
//                            .findFirst()
//                            .ifPresent(thePath -> {
//                                Path copyTo = targetFileSystem.createFile(Configuration.getTargetFolderPath() + "/" + thePath.getFileName() + "_" + suffix);
//                                targetFileSystem.copy(thePath, copyTo);
//                                logger.info("successfully copied: " + thePath + " to: " + copyTo);
//                                sourceFileSystem.removeFile(thePath.toString());
//                                logger.info("removed: " + thePath);
//                                Configuration.incProcessed();
//                            });
//                    break;
//                case MORNING:
//                case AFTERNOON:
//                    logger.info("Not executing - only execute during evening or night time");
//                    break;
//            }
//        }catch (SecurityException ex){
//          logger.log(Level.SEVERE, null, ex);
//          initialize();
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
//    }

    //<editor-fold desc="violations" defaultstate="collapsed">
    /**
     *
     * Private method reaching out to global state.
     * Private method effects the logical flow of its public counterpart without exposing for testing the ability to select
     * different execution scenarios.
     * Method uses hard coded constants.
     * Method violates SRP and LOD by reaching out of its immediate scope and by having
     * more then one reason to change,
     * i.e if working hours change 
     * or when system run on a machine at a different time zone 
     * or time frames need to be more precise like, adding DUSK or DAWN
     *
     */
    //</editor-fold>
    private TimeOfDay GetTimeOfDay() {
        LocalDateTime time = LocalDateTime.now();
        if (time.getHour() >= 0 && time.getHour() < 6) {
            return TimeOfDay.NIGHT;
        }
        if (time.getHour() >= 6 && time.getHour() < 12) {
            return TimeOfDay.MORNING;
        }
        if (time.getHour() >= 12 && time.getHour() < 18) {
            return TimeOfDay.AFTERNOON;
        }
        return TimeOfDay.EVENING;
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
        return Paths.get(configuration.getTargetFolderPath(),targetFileName).toString();
    }


    private enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }

}
