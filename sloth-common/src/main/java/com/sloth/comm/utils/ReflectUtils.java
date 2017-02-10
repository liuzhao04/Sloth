package com.sloth.comm.utils;

/**
 * Java反射工具箱
 *
 * @author lWX306898
 * @version 1.0, 2016年12月23日
 */
public class ReflectUtils
{
    /**
     * 由类名创建实例，调用默认构造函数
     *
     * @param className 类名
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T createInstance(String className) throws ClassNotFoundException,
                                                         InstantiationException,
                                                         IllegalAccessException
    {
        @SuppressWarnings("unchecked")
        Class<T> cls = (Class<T>)Class.forName(className);
        return cls.newInstance();
    }
}
