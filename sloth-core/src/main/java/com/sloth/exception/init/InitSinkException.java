package com.sloth.exception.init;

import com.sloth.exception.SlothException;

/**
 * 输出初始化失败
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class InitSinkException extends SlothException
{
    private static final long serialVersionUID = -5268239967699599088L;

    public InitSinkException(String msg)
    {
        super(msg);
    }

    public InitSinkException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
