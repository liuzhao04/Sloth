package com.huawei.sloth.comm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * 常用工具箱
 *
 * @author lWX306898
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
     * 邮件发送函数
     * 
     * @param to
     * @param subject
     * @param msg
     * @throws EmailException
     */
    public static void sendEmail(String to, String subject, String msg) throws EmailException
    {
        String smtpHost = "smtp.huawei.com";
        Email email = new HtmlEmail();
        email.setHostName(smtpHost);
        email.setAuthenticator(getEmailAuthenticator());
        email.setFrom("liuzhao5@huawei.com", "巡检云对接工具");
        email.setSubject(subject);
        email.setContent(msg, "text/html;charset=gb2312");
        email.addTo(to);
        email.send();
    }

    /**
     * 邮件发送函数
     * 
     * @param to
     * @param subject
     * @param msg
     * @throws EmailException
     */
    public static void sendEmail(String[] tos, String subject, String msg) throws EmailException
    {
        String smtpHost = "smtp.huawei.com";
        Email email = new HtmlEmail();
        email.setHostName(smtpHost);
        email.setAuthenticator(getEmailAuthenticator());
        email.setFrom("liuzhao5@huawei.com", "巡检云对接工具");
        email.setSubject(subject);
        email.setContent(msg, "text/html;charset=gb2312");
        email.addTo(tos);
        email.send();
    }

    /**
     * 邮箱账号
     * 
     * @return
     */
    private static DefaultAuthenticator getEmailAuthenticator()
    {
        return new DefaultAuthenticator(decode("108 87 88 51 48 54 56 57 56"), decode("108 122 37 49 50 56 49 50 56"));
    }

    /**
     * 数据解密
     *
     * @param cipherText
     * @return
     */
    private static String decode(String cipherText)
    {
        String[] sBytes = cipherText.split(" ");
        byte[] bytes = new byte[sBytes.length];
        int index = 0;
        for (String sb : sBytes)
        {
            bytes[index++] = Byte.valueOf(sb);
        }
        return new String(bytes);
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
