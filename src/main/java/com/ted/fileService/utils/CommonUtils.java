package com.ted.fileService.utils;

import java.util.Date;

public class CommonUtils {

    public String generateRandomString(){
        return String.valueOf(new Date().getTime());
    }

    public String addRandomSuffix(String original, String separator){
        return original.concat(separator).concat(generateRandomString());
    }

}
