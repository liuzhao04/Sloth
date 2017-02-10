package com.sloth.exception.init;

import com.sloth.exception.SlothException;

/**
 * 工作流初始化错误
 *
 * @author lWX306898
 * @version 1.0, 2017年1月26日
 */
public class InitWorkFlowException extends SlothException
{
    private static final long serialVersionUID = 7477467059383619061L;

    public InitWorkFlowException(String msg)
    {
        super(msg);
    }

    public InitWorkFlowException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
