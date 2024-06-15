package com.school.elite.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {
    public static LocalDateTime getCurrentTimeOfIndia(){
        LocalDateTime utcDateTime = LocalDateTime.now();

        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");

        return utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(indiaZone).toLocalDateTime();
    }
}
