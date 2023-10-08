package com.zzx.robot.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author zzx
 * @date 2023/3/31
 */
public class DateUtils {

    public static Date todayInitialDate() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 8) {
            // 还没初始化，用昨天的日期
            calendar.add(Calendar.DATE, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

}
