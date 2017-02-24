package com.sloth.source;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;

/**
 * 数据源定义
 * 
 * @author liuzhao04
 * @version 1.0, 2017年1月23日
 */
public interface ISource
{
    /**
     * 初始化数据源
     *
     * @param cfgHelp
     * @throws SlothException
     */
    public void init(ConfigureHelp cfgHelp) throws SlothException;

    /**
     * 从数据源获取数据
     * 
     * @param n
     * @return
     * @throws SlothException
     */
    public Message getMessage() throws SlothException;

    /**
     * 资源销毁
     */
    public void destory();

    /**
     * 数据源是否有未处理的数据
     * 
     * @return
     */
    public boolean hasNext();

}
