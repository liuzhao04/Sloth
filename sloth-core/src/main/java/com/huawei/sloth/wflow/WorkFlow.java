package com.huawei.sloth.wflow;

import java.io.Serializable;
import java.util.List;

import com.huawei.sloth.configure.ConfigureHelp;
import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.handle.IHandle;
import com.huawei.sloth.msg.Message;
import com.huawei.sloth.sink.ISink;

/**
 * 工作流定义
 *
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public class WorkFlow<T extends Serializable>
{
    private ISink<T> iSink; // 输出

    private List<IHandle<? extends Serializable>> handles; // 处理器数组

    private ConfigureHelp cfgHelp;

    public WorkFlow(ConfigureHelp ch) throws SlothException
    {
        cfgHelp = ch;
        init();
    }

    private void init() throws SlothException
    {
        cfgHelp.getValue("baidu");
    }

    @SuppressWarnings("unchecked")
    public void handle(Message<? extends Serializable> message) throws SlothException
    {
        Message<? extends Serializable> rMsg = message;
        for (IHandle<? extends Serializable> h : handles)
        {
            rMsg = h.handle(rMsg);
            if (rMsg.isEmpty())
            {
                return;
            }
        }
        iSink.writeMessage((Message<T>)rMsg);
    }

    /**
     * 资源销毁
     */
    public void destory()
    {
        if (iSink != null)
        {
            iSink.destory();
            iSink = null;
        }
        if (handles != null)
        {
            for (IHandle<? extends Serializable> ih : handles)
            {
                ih.destory();
            }
            handles.clear();
            handles = null;
        }
    }
}
