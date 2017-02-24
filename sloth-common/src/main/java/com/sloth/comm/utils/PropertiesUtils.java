package com.sloth.comm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件加载工具
 *
 * @author liuzhao04
 * @version 1.0, 2016年12月22日
 */
public class PropertiesUtils
{
    /**
     * 从资源文件中加载数据流
     *
     * @param path
     * @return
     */
    private static InputStream getInputStreamFromResource(String resourcePath)
    {
        return PropertiesUtils.class.getResourceAsStream(resourcePath);
    }

    /**
     * 从本地文件中加载数据流
     * 
     * @param path
     * @return
     */
    private static InputStream getInputStreamFromPath(String path)
    {
        String tmpPath = path;
        File file = new File(tmpPath);
        if (file.exists())
        {
            try
            {
                return new FileInputStream(tmpPath);
            }
            catch (FileNotFoundException e)
            {
                return null;
            }
        }
        return null;
    }

    /**
     * 只取conf中的配置文件
     * 
     * @param path
     * @return
     */
    private static String getPathString(String path)
    {
        return "conf/" + new File(path).getName();
    }

    /**
     * 只取根下的资源文件
     * 
     * @param path
     * @return
     */
    private static String getResourceString(String path)
    {
        return "/" + new File(path).getName();
    }

    /**
     * 加载配置文件
     *
     * @param path
     * @param lOrder
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String path, LoadOrder lOrder) throws IOException
    {
        InputStream is = null;
        switch (lOrder)
        {
            case FIRST_PATH:
                String tmp = getPathString(path);
                is = getInputStreamFromPath(tmp);
                if (is == null)
                {
                    tmp = getResourceString(path);
                    is = getInputStreamFromResource(tmp);
                }
                break;
            case FIRST_RESOURCE:
                tmp = getResourceString(path);
                is = getInputStreamFromResource(tmp);
                if (is == null)
                {
                    tmp = getPathString(path);
                    is = getInputStreamFromPath(tmp);
                }
                break;
            case PATH:
                is = getInputStreamFromPath(path);
                break;
            case RESOURCE:
                is = getInputStreamFromResource(path);
                break;
            default:
                break;
        }

        Properties p = new Properties();
        p.load(is);
        return p;
    }

    /**
     * 将配置文件映射为Map<String,String>对象，便于取值
     *
     * @param p
     * @return
     */
    public static Map<String, String> mapProperties(Properties p)
    {
        Map<String, String> map = new HashMap<String, String>();
        p.forEach((a, b) -> map.put((String)a, (String)b));
        return map;
    }

    /**
     * 用类加载器加载资源配置文件，并转成Map<String,String>
     * 
     * @param resourcePath
     * @return
     * @throws IOException
     */
    public static Map<String, String> mapProperties(String path, LoadOrder lOrder) throws IOException
    {
        return mapProperties(loadProperties(path, lOrder));
    }

    /**
     * 资源文件加载顺序定义
     * 
     * @author liuzhao04
     * @version 1.0, 2016年12月23日
     */
    public enum LoadOrder
    {
     /** 优先Path加载 */
        FIRST_PATH,
     /** 优先资源加载 */
        FIRST_RESOURCE,
     /** Path加载 */
        PATH,
     /** 资源加载 */
        RESOURCE
    }

}
