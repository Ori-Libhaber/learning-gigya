package com.ted.fileService;

import com.evil.corp.filesystem.IFileSystem;
import com.evil.corp.filesystem.IFileSystemProvider;
import com.evil.corp.security.ICredintials;
import com.evil.corp.security.ILoginDetails;
import com.evil.corp.security.ISecurityManager;
import com.evil.corp.security.IUser;
import com.ted.fileService.utils.ConfigManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
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
public class FileService {

    private static Logger logger = Logger.getLogger(FileService.class.getName());
    
    // <editor-fold desc="violations" defaultstate="collapsed">
    /**
     * The majority of the instance members is only used for class instantiation,
     * the rest of the business logic depends solely on sourceFileSystem and targetFileSystem.
     * This is a SRP violation.
     * 
     * Furthermore, we are maintaining a static instance member which couples our
     * implementation to global state - unpredictable code
     * 
     */
    //</editor-fold>
    private static volatile FileService instance;
    private ISecurityManager securityManager;
    private IFileSystem sourceFileSystem;
    private IFileSystem targetFileSystem;
    private IFileSystemProvider fileSystemProvider;
    private ICredintials credintials;

    //<editor-fold desc="violations" defaultstate="collapsed">
    /**
     *
     * A private constructor prevents inheritance and by effect prevents mocking, making it hard to test.
     *
     */
    //</editor-fold>
    private FileService() {
        initilize();
    }

    //<editor-fold desc="violations" defaultstate="collapsed">
    /**
     *
     * This external initialization method does little to ease testability or maintainability.
     * It mixes object instantiation with dependency graph creation and wiring.
     * This private method reaches out to static methods of ConfigManager class, making is brittle and likely to break
     * in the event of changes being made to the later.
     * 
     * private methods should contain only implementation details that are closed to modifications.
     * private methods should not reach out to external collaborators unless they are provided as parameters.
     * private methods should remain as small and simple as possible, always try to estimate how difficult it might be
     * to test various logical branching of the private through one of it's respective collaborating public method.
     * If a private method is too complex, consider delegating it to a method object instead.
     * 
     * Remember to keep your privates to yourself!
     *
     */
    //</editor-fold>
    /**
     * service instantiation
     * @throws SecurityException 
     */
    private void initilize() throws SecurityException {
        String workingDir = System.getProperty("user.dir");
        String userDetailsConfPath = System.getProperty("userPath", Paths.get(workingDir).resolve("src/main/resources/userDetails.conf").toString());
        File userDetailsFile = Paths.get(workingDir).resolve(Paths.get(userDetailsConfPath)).toFile();
        IUser user = ConfigManager.getUser(userDetailsFile);
        ILoginDetails loginDetails = ConfigManager.getLoginDetails(user);
        this.securityManager = ConfigManager.getSecurityManager();
        this.credintials = this.securityManager.createCredintials(user, loginDetails);
        this.fileSystemProvider = ConfigManager.getFileSystemProvider();
        this.sourceFileSystem = fileSystemProvider.get(ConfigManager.getSourceFileSystemIdentifier(), credintials);
        this.targetFileSystem = fileSystemProvider.get(ConfigManager.getTargetFileSystemIdentifier(), credintials);
    }

    //<editor-fold desc="violations" defaultstate="collapsed">
    /**
     *
     * This is a hand made singleton pattern implementation (GO4) It is
     * considered an ant-pattern and should be avoided by all costs! GO4
     * singletons are discouraged for various reasons:
     * {@link https://dzone.com/articles/singleton-anti-pattern}
     *
     * Singletons are hard to test because: 1. they enforce a single
     * implementation of the Class, thus preventing mocking by inheritance 2.
     * they couple logic to global state 3. they are change preventers -
     * Couplers the statically couple consuming class to its implementation 4.
     * they introduce synchronization hardships
     *
     * A correct way to implement a singleton pattern is to relay upon usage and
     * not enforce is singularity by structure. DI frameworks such as
     * Spring/Guice makes creating, using and testing singletons a breeze by
     * delegating singularity constraint enforcement to DI framework, freeing
     * developer from this concern.
     *
     */
    //</editor-fold>
    public static FileService getInstance() {
        FileService currentInstance = FileService.instance;
        if (currentInstance == null) {
            synchronized (FileService.class) {
                currentInstance = FileService.instance;
                if (currentInstance == null) {
                    FileService.instance = new FileService();
                    currentInstance = FileService.instance;
                }
                FileService.class.notifyAll();
            }
        }
        return currentInstance;
    }

    //<editor-fold desc="violations" defaultstate="collapsed">
    /**
     *
     * Method have a deceiving name, it actually does more then to find a file - violates SRP - has numerous reasons to change.
     * Method relays on global state (calling static methods, relaying on current time)
     * Method logic is non-deterministic, dependent of the time configuration of the hosting machine.
     * Method is structure is confusing, abusing streaming API.
     * Method has side-effects, it generates a random name for the target file, its hard to test the successful path when the resulting file name is unknown
     * and also changes global state of ConfigManager
     * Method lies about it's collaborators & dependencies - the only way to find out is to read the code!
     * Method violates LOD - ask for stuff, don't look for it.
     *
     */
    //</editor-fold>
    /**
     * Find the file - do the job
     */
    public synchronized void findFile() {
        try {
            switch (GetTimeOfDay()) {
                case EVENING:
                case NIGHT:
                    String source = ConfigManager.getSourceFileName();
                    logger.info("Attempting to find and copy: " + source + " to backup location");
                    String suffix = String.valueOf(new Date().getTime());
                    securityManager.validateCredintials(credintials);
                    Files.list(sourceFileSystem.getPath(ConfigManager.getHomeFolderPath()))
                            .filter(path -> path.getFileName().endsWith(source))
                            .findFirst()
                            .ifPresent(thePath -> {
                                Path copyTo = targetFileSystem.createFile(ConfigManager.getTargetFolderPath() + "/" + thePath.getFileName() + "_" + suffix);
                                targetFileSystem.copy(thePath, copyTo);
                                logger.info("successfully copied: " + thePath + " to: " + copyTo);
                                sourceFileSystem.removeFile(thePath.toString());
                                logger.info("removed: " + thePath);
                                ConfigManager.incProcessed();
                            });
                    break;
                case MORNING:
                case AFTERNOON:
                    logger.info("Not executing - only execute during evening or night time");
                    break;
            }
        }catch (SecurityException ex){
          logger.log(Level.SEVERE, null, ex);
          initilize();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

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

    private enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }

}
