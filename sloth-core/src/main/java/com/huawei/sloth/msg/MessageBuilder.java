package com.huawei.sloth.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sloth.exception.msg.EmptyMessageException;

/**
 * 消息构造器
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public class MessageBuilder<T extends Serializable>
{
    private Message<T> message;

    public MessageBuilder()
    {
        init();
    }

    private void init()
    {
        if (message == null)
        {
            message = new Message<T>();
        }
    }

    /**
     * 构造消息头
     *
     * @param key
     * @param values
     * @return
     */
    public MessageBuilder<T> addHead(String key, byte[] values)
    {
        Map<String, byte[]> head = message.getHead();
        if (head == null)
        {
            head = new HashMap<String, byte[]>();
            message.setHead(head);
        }
        head.put(key, values);
        return this;
    }

    /**
     * 构造消息头
     *
     * @param head
     * @return
     */
    public MessageBuilder<T> addHead(Map<String, byte[]> head)
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
    public MessageBuilder<T> add(T value)
    {
        List<T> values = message.getBody();
        if (values == null)
        {
            values = new ArrayList<T>();
        }
        values.add(value);
        return this;
    }

    /**
     * 返回构建的结果
     * 
     * @return
     */
    public Message<T> build()
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
    public Map<String, byte[]> getHead() throws EmptyMessageException
    {
        if (null == message || message.isEmpty())
        {
            throw new EmptyMessageException();
        }
        return message.getHead();
    }
}
