package com.sloth.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sloth.exception.msg.EmptyMessageException;

/**
 * 消息构造器
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public class MessageBuilder
{
    private Message message;

    public MessageBuilder()
    {
        init();
    }

    private void init()
    {
        if (message == null)
        {
            message = new Message();
        }
    }

    /**
     * 构造消息头
     *
     * @param key
     * @param values
     * @return
     */
    public MessageBuilder addHead(String key, String values)
    {
        Map<String, String> head = message.getHead();
        if (head == null)
        {
            head = new HashMap<String, String>();
            message.setHead(head);
        }
        head.put(key, values);
        message.setHead(head);
        return this;
    }

    /**
     * 构造消息头
     *
     * @param head
     * @return
     */
    public MessageBuilder addHead(Map<String, String> head)
    {
        for (String key : head.keySet())
        {
            addHead(key, head.get(key));
        }
        return this;
    }

    /**
     * 添加消息体
     * 
     * @param value
     * @return
     */
    public MessageBuilder add(MessageUnit value)
    {
        List<MessageUnit> values = message.getBody();
        if (values == null)
        {
            values = new ArrayList<MessageUnit>();
        }
        values.add(value);
        message.setBody(values);
        return this;
    }

    /**
     * 添加消息体
     * 
     * @param value
     * @return
     */
    public MessageBuilder add(Object value)
    {
        List<MessageUnit> values = message.getBody();
        if (values == null)
        {
            values = new ArrayList<MessageUnit>();
        }
        values.add(new MessageUnit(value));
        message.setBody(values);
        return this;
    }

    /**
     * 返回构建的结果
     * 
     * @return
     */
    public Message build()
    {
        return message;
    }

    /**
     * 清空之前的构造缓存，清空之后可以再次构造消息
     */
    public void clear()
    {
        message = null;
        init();
    }

    /**
     * 返回头部当前正在构建的头部
     *
     * @return
     * @throws EmptyMessage
     */
    public Map<String, String> getHead() throws EmptyMessageException
    {
        if (null == message || message.getHead() == null)
        {
            throw new EmptyMessageException();
        }
        return message.getHead();
    }
}
