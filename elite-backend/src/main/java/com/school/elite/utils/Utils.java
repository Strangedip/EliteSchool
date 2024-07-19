package com.school.elite.utils;

import java.util.UUID;

public class Utils {
    public static String createUUID(){
        return UUID.randomUUID().toString();
    }
    public static String logSeparator(){
        return "+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+";
    }

}
