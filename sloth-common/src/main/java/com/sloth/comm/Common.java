package com.sloth.comm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 常用工具箱
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月20日
 */
public class Common
{
    private static String today;

    private static final SimpleDateFormat SDF_DATA = new SimpleDateFormat("yyyy-MM-dd");

    private static boolean isWindows = false;

    static
    {
        // 初始化当天字符串
        today = SDF_DATA.format(new Date());

        // 设置网络环境
        System.setProperty("java.net.preferIPv4Stack", "true");

        // 获取操作系统环境
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (!os.startsWith("win") && !os.startsWith("Win"))
        {
            isWindows = true;
        }
    }

    /**
     * 返回当前日期字符串
     *
     * @return
     */
    public static String getTodayString()
    {
        isDateChanged();
        return today;
    }

    /**
     * 获取几天前的日期字符串
     *
     * @param n
     * @return
     */
    public static String getDateStringBeforeN(int n)
    {
        if (n < 1)
        {
            n = 0;
        }
        long times = System.currentTimeMillis() - n * 3600 * 1000 * 24;
        return SDF_DATA.format(times);
    }

    /**
     * 获取昨天的日期字符串
     * 
     * @return
     */
    public static String getYestodayString()
    {
        return getDateStringBeforeN(1);
    }

    /**
     * 判断日志是否改变
     * 
     * @return
     */
    public static boolean isDateChanged()
    {
        String tToday = SDF_DATA.format(new Date());
        if (today.equals(tToday))
        {
            return false;
        }
        today = tToday;
        return true;
    }

    /**
     * 判断当前环境为Window或者Linux
     * 
     * @return
     */
    public static boolean isWindows()
    {
        return isWindows;
    }

}
