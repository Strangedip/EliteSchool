package com.school.elite.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtil {

//    public static void main(String[] args) {
//        System.out.println(getTime());
//        System.out.println(getCurrentTimeOfIndia());
//    }

    public static LocalDateTime getCurrentTimeOfIndia() {
        LocalDateTime utcDateTime = LocalDateTime.now();

        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");

        return utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(indiaZone).toLocalDateTime();
    }

    public static LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
