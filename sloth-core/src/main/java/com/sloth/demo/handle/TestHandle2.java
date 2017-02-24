package com.sloth.demo.handle;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;

/**
 * 输出value的值 
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class TestHandle2 implements IHandle
{

    @Override
    public void init(ConfigureHelp hHelp) throws SlothException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Message handle(Message msg) throws SlothException
    {
        MessageBuilder mb = new MessageBuilder();
        mb.addHead("id", msg.getHead().get("id") + "_f2");
        mb.add((Integer)msg.getBody().get(0).getValue());
        return mb.build();
    }

    @Override
    public void destory()
    {

    }

}
