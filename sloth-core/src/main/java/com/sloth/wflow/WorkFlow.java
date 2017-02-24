package com.sloth.wflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sloth.comm.utils.ReflectUtils;
import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.exception.init.InitSinkException;
import com.sloth.exception.init.InitWorkFlowException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.sink.ISink;

/**
 * 工作流定义
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class WorkFlow
{
    private ISink iSink; // 输出

    private List<IHandle> handles; // 处理器数组

    private ConfigureHelp cfgHelp;

    public WorkFlow(ConfigureHelp ch) throws SlothException
    {
        cfgHelp = ch;
        init();
    }

    /**
     * 初始化工作流
     *
     * @throws SlothException
     */
    private void init() throws SlothException
    {
        String[] handles = StringUtils.split(cfgHelp.getValue("handles"), ",");
        this.handles = new ArrayList<IHandle>();
        for (int i = 0; i < handles.length; i++)
        {
            // 处理器的配置帮组对象
            ConfigureHelp hHelp = new ConfigureHelp(cfgHelp.getCfgMap(), cfgHelp.getCompName() + "." + handles[i]);
            String hClass = hHelp.getValue("class");
            try
            {
                IHandle iHd = ReflectUtils.<IHandle> createInstance(hClass);
                iHd.init(hHelp);
                this.handles.add(iHd);
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
            {
                throw new InitWorkFlowException("Handle对象创建失败", e);
            }
        }

        String sk = cfgHelp.getValue("sink");
        ConfigureHelp sHelp = new ConfigureHelp(cfgHelp.getCfgMap(), cfgHelp.getCompName() + "." + sk);
        String sClass = sHelp.getValue("class");
        try
        {
            iSink = ReflectUtils.<ISink> createInstance(sClass);
            iSink.init(sHelp);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            throw new InitSinkException("Sink对象创建失败", e);
        }
    }

    /**
     * 工作流处理一条消息
     * 
     * @param message
     * @throws SlothException
     */
    public void handle(Message message) throws SlothException
    {
        Message rMsg = message;
        for (IHandle h : handles)
        {
            rMsg = h.handle(rMsg);
            if (rMsg.isEmpty())
            {
                return;
            }
        }
        iSink.writeMessage(rMsg);
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
            for (IHandle ih : handles)
            {
                ih.destory();
            }
            handles.clear();
            handles = null;
        }
    }
}
