package com.sloth.msg;

import java.io.Serializable;

/**
 * 消息单元
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class MessageUnit implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Object value;

    public MessageUnit(Object value)
    {
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "MessageUnit [value=" + value + "]";
    }
}
