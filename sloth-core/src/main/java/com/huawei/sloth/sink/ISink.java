package com.huawei.sloth.sink;

import java.io.Serializable;

import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.msg.Message;

/**
 * 输出定义
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public interface ISink<T extends Serializable>
{
    /**
     * 数据输出接口定义
     * 
     * @param message
     * @throws SlothException
     */
    public void writeMessage(Message<T> message) throws SlothException;

    /**
     * 资源销毁
     */
    public void destory();
}
