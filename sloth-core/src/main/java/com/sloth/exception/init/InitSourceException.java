package com.sloth.exception.init;

import com.sloth.exception.SlothException;

/**
 * 数据源初始化失败
 *
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public class InitSourceException extends SlothException
{
    private static final long serialVersionUID = -5268239967699599088L;

    public InitSourceException(String msg)
    {
        super(msg);
    }

    public InitSourceException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
