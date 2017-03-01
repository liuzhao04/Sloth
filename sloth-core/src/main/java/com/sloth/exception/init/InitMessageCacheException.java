package com.sloth.exception.init;

import com.sloth.exception.SlothException;

/**
 * 处理初始化失败
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class InitMessageCacheException extends SlothException
{
    private static final long serialVersionUID = -5268239967699599088L;

    public InitMessageCacheException(String msg)
    {
        super(msg);
    }

    public InitMessageCacheException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
