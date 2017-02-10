package com.huawei.sloth.handle;

import java.io.Serializable;

import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.msg.Message;

/**
 * 数据处理器
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public interface IHandle<R extends Serializable>
{
    /**
     * 数据处理
     * 
     * @param msg 输入书参数
     * @return
     */
    public Message<R> handle(final Message<? extends Serializable> msg) throws SlothException;

    /**
     * 资源销毁
     */
    public void destory();
}
