package com.sloth.demo.handle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;
import com.sloth.msg.MessageUnit;

/**
 * 将字符串转成整数，head增加 (id x 3)
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class TestHandle implements IHandle
{
    private Pattern valPattern = Pattern.compile("(\\d+)$");

    @Override
    public void init(ConfigureHelp hHelp) throws SlothException
    {
    }

    @Override
    public Message handle(Message msg) throws SlothException
    {
        MessageBuilder mb = new MessageBuilder();
        int id = Integer.parseInt(msg.getHead().get("id"));
        mb.addHead("id", id + "");
        mb.addHead("idx3", id * 3 + "");
        for (MessageUnit mu : msg.getBody())
        {
            Matcher m = valPattern.matcher((String)mu.getValue());
            int vid = -1;
            if (m.find())
            {
                vid = Integer.parseInt(m.group(1));
            }
            mb.add(vid);
        }
        return mb.build();
    }

    @Override
    public void destory()
    {

    }

}
