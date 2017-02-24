package com.sloth.demo.source;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;
import com.sloth.source.ISource;

/**
 * 测试数据源
 * 
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class TestSource implements ISource
{
    private int index;

    private int dataCount;

    @Override
    public void init(ConfigureHelp cfgHelp) throws SlothException
    {
        dataCount = Integer.parseInt(cfgHelp.getValue("count"));
        index = 1;
    }

    @Override
    public Message getMessage() throws SlothException
    {
        MessageBuilder sb = new MessageBuilder();
        sb.addHead("id", (index + ""));
        sb.add("this is test message " + index * 2);
        index++;
        return sb.build();
    }

    @Override
    public void destory()
    {
        dataCount = -1;
        index = -1;
    }

    @Override
    public boolean hasNext()
    {
        if (index >= dataCount)
        {
            return false;
        }
        return true;
    }

}
