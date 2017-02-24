package com.sloth.demo.sink;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;
import com.sloth.sink.ISink;

public class TestSink implements ISink
{

    @Override
    public void writeMessage(Message message) throws SlothException
    {
        System.out.println("id:" + message.getHead().get("id") + " value:" + message.getBody().get(0).getValue());
    }

    @Override
    public void destory()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(ConfigureHelp sHelp) throws SlothException
    {
        // TODO Auto-generated method stub
        
    }

}
