package com.sloth.msg;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 消息定义
 *
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public class Message implements Serializable
{
    private static final long serialVersionUID = -6728022487251897425L;

    private Map<String, String> head; // 消息头

    private List<MessageUnit> body; // 消息主体

    public Map<String, String> getHead()
    {
        return head;
    }

    public void setHead(Map<String, String> head)
    {
        this.head = head;
    }

    public List<MessageUnit> getBody()
    {
        return body;
    }

    public void setBody(List<MessageUnit> body)
    {
        this.body = body;
    }

    @Override
    public String toString()
    {
        return "Message [head=" + head + ", body=" + body + "]";
    }

    /**
     * 判断是否为空
     *
     * @return
     */
    public boolean isEmpty()
    {
        return body == null;
    }

    /**
     * 创建空对象
     *
     * @return
     */
    public static Message empty()
    {
        return new Message();
    }
}
