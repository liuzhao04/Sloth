package com.sloth.exception.cache;

import com.sloth.exception.SlothException;

/**
 * 缓存写入失败
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class CacheWriteException extends SlothException
{
    private static final long serialVersionUID = 7477467059383619061L;

    public CacheWriteException(String msg)
    {
        super(msg);
    }

    public CacheWriteException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
