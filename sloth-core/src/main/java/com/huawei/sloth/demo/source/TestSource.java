package com.huawei.sloth.demo.source;

import com.huawei.sloth.configure.ConfigureHelp;
import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.msg.Message;
import com.huawei.sloth.msg.MessageBuilder;
import com.huawei.sloth.source.ISource;

/**
 * 测试数据源
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public class TestSource implements ISource<String>
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
    public Message<String> getMessage() throws SlothException
    {
        MessageBuilder<String> sb = new MessageBuilder<String>();
        sb.addHead("id", (index + "").getBytes());
        sb.add("this is test message " + index);
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
        if (index == dataCount)
        {
            return false;
        }
        return true;
    }

}
