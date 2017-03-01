package com.sloth.exception.cache;

import com.sloth.exception.SlothException;

/**
 * 缓存读取异常
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class CacheReadException extends SlothException
{
    private static final long serialVersionUID = 7477467059383619061L;

    public CacheReadException(String msg)
    {
        super(msg);
    }

    public CacheReadException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
