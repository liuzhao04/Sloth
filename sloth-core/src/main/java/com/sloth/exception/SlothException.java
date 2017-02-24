package com.sloth.exception;

/**
 * 内部异常定义
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月23日
 */
public class SlothException extends Exception
{
    private static final long serialVersionUID = 1205212599663040048L;

    public SlothException()
    {
        super();
    }

    public SlothException(String cause)
    {
        super(cause);
    }

    public SlothException(String cause, Throwable e)
    {
        super(cause, e);
    }

    public SlothException(Throwable e)
    {
        super(e);
    }
}
