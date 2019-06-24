package com.ted.fileService.service;

import com.evil.corp.security.ICredintials;
import com.evil.corp.security.ILoginDetails;
import com.evil.corp.security.ISecurityManager;
import com.evil.corp.security.IUser;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.logging.Logger;

@Singleton
public class SessionManager {

    private static Logger logger =  Logger.getLogger(SessionManager.class.getName());

    private IUser user;
    private ILoginDetails loginDetails;
    private ISecurityManager securityManager;
    private ICredintials credentials;

    @Inject
    public SessionManager(IUser user, ILoginDetails loginDetails, ISecurityManager securityManager) {
        this.user = user;
        this.loginDetails = loginDetails;
        this.securityManager = securityManager;
    }

    public void execute(Operation operation){
        if(!isValidSession()){ // checking for valid session
            try {
                // renewing session
                this.credentials = securityManager.createCredintials(user, loginDetails);
            }catch (SecurityException securityEx){
                throw new SessionManagerException.BrokenConnection("Broken connection", securityEx);
            }
        }
        operation.operate(); // executing action
    }

    private boolean isValidSession(){
        try{
            if(credentials != null){
                securityManager.validateCredintials(credentials);
                return true;
            }
        }catch (SecurityException securityEx){
            logger.warning(securityEx.getMessage());
        }
        return false;
    }

    public static class SessionManagerException extends RuntimeException{
        public static class BrokenConnection extends RuntimeException {
            public BrokenConnection(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }

    @FunctionalInterface
    public static interface Operation {
        public void operate();
    }

}
