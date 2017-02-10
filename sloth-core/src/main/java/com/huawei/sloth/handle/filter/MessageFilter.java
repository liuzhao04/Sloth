package com.huawei.sloth.handle.filter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.handle.IHandle;
import com.huawei.sloth.msg.Message;
import com.huawei.sloth.msg.MessageBuilder;

/**
 * 消息过滤器定义
 *
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public class MessageFilter<T extends Serializable> implements IHandle<T>
{

    @Override
    public Message<T> handle(Message<? extends Serializable> msg) throws SlothException
    {
        @SuppressWarnings("unchecked")
        Message<T> tmpMsg = (Message<T>)msg;
        // 1. 过滤空数据
        if (tmpMsg.isEmpty())
        {
            return tmpMsg;
        }

        // 2. 按照头部过滤
        if (!filterHeader(tmpMsg.getHead()))
        {
            return Message.<T> empty();
        }

        // 3. 按照头部和内容过滤
        if (!filterBody(tmpMsg.getHead(), tmpMsg.getBody()))
        {
            return Message.<T> empty();
        }

        // 4. 按照头部和单元数据进行单行过滤
        MessageBuilder<T> mb = new MessageBuilder<T>();
        mb.addHead(tmpMsg.getHead());
        @SuppressWarnings("unchecked")
        List<T> body = (List<T>)msg.getBody();
        for (T unit : body)
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
    protected boolean filterHeader(final Map<String, byte[]> head)
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
    protected boolean filterUnit(final Map<String, byte[]> head, Serializable unit)
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
    protected boolean filterBody(final Map<String, byte[]> head, final List<? extends Serializable> list)
    {
        return true;
    }

    @Override
    public void destory()
    {
        // TODO Auto-generated method stub

    }
}
