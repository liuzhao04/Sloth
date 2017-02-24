package com.sloth.sink;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;

/**
 * 输出定义
 * 
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public interface ISink
{
    /**
     * sink初始化
     * 
     * @param sHelp
     * @throws SlothException
     */
    public void init(ConfigureHelp sHelp) throws SlothException;

    /**
     * 数据输出接口定义
     * 
     * @param message
     * @throws SlothException
     */
    public void writeMessage(Message message) throws SlothException;

    /**
     * 资源销毁
     */
    public void destory();
}
