package com.sloth.handle;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;

/**
 * 数据处理器
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public interface IHandle
{
    /**
     * 初始化
     * 
     * @param hHelp
     * @throws SlothException
     */
    public void init(ConfigureHelp hHelp) throws SlothException;

    /**
     * 数据处理
     * 
     * @param msg 输入书参数
     * @return
     */
    public Message handle(final Message msg) throws SlothException;

    /**
     * 资源销毁
     */
    public void destory();

}
