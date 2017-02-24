package com.sloth.demo.handle;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;

/**
 * 统计(id x 3)的和 
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class TestHandle1 implements IHandle
{
    private int total = 0;

    @Override
    public void init(ConfigureHelp hHelp) throws SlothException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Message handle(Message msg) throws SlothException
    {
        MessageBuilder mb = new MessageBuilder();
        mb.addHead("id", msg.getHead().get("id") + "_f1");
        total += Integer.parseInt(msg.getHead().get("idx3"));
        mb.add(total);
        return mb.build();
    }

    @Override
    public void destory()
    {
        // TODO Auto-generated method stub

    }

}
