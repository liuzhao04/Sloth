package com.sloth.handle.filter;

import java.util.List;
import java.util.Map;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;
import com.sloth.msg.MessageUnit;

/**
 * 消息过滤器定义
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class MessageFilter implements IHandle
{
    @Override
    public void init(ConfigureHelp hHelp) throws SlothException
    {

    }

    @Override
    public Message handle(Message msg) throws SlothException
    {
        Message tmpMsg = msg;
        // 1. 过滤空数据
        if (tmpMsg.isEmpty())
        {
            return tmpMsg;
        }

        // 2. 按照头部过滤
        if (!filterHeader(tmpMsg.getHead()))
        {
            return Message.empty();
        }

        // 3. 按照头部和内容过滤
        if (!filterBody(tmpMsg.getHead(), tmpMsg.getBody()))
        {
            return Message.empty();
        }

        // 4. 按照头部和单元数据进行单行过滤
        MessageBuilder mb = new MessageBuilder();
        mb.addHead(tmpMsg.getHead());
        List<MessageUnit> body = msg.getBody();
        for (MessageUnit unit : body)
        {
            if (filterUnit(mb.getHead(), unit))
            {
                mb.add(unit);
            }
        }
        return mb.build();
    }

    /**
     * Message过滤：根据头部信息整块过滤
     *
     * @param head
     * @return
     */
    protected boolean filterHeader(final Map<String, String> head)
    {
        return true;
    }

    /**
     * 单元过滤：根据头部和当前单元数据判断是否保留当前行<br>
     * 当然过滤器自己可以缓存整段数据作为判断依据
     * 
     * @param head
     * @param body
     * @return
     */
    protected boolean filterUnit(final Map<String, String> head, MessageUnit unit)
    {
        return true;
    }

    /**
     * Message过滤:根据消息的详细信息，判断整个Message是否需要保留
     *
     * @param head
     * @param list
     * @return
     */
    protected boolean filterBody(final Map<String, String> head, final List<MessageUnit> list)
    {
        return true;
    }

    @Override
    public void destory()
    {

    }

}
