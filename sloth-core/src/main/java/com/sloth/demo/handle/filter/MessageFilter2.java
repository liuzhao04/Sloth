package com.sloth.demo.handle.filter;

import java.util.Map;

import com.sloth.handle.filter.MessageFilter;
import com.sloth.msg.MessageUnit;

/**
 * 过滤出ID 大于 5 且 body value小于11的数据
 * 
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class MessageFilter2 extends MessageFilter
{

    @Override
    protected boolean filterUnit(Map<String, String> head, MessageUnit unit)
    {
        if (Integer.parseInt(head.get("id")) < 6)
        {
            return false;
        }
        if (Integer.parseInt(head.get("id")) > 10)
        {
            return false;
        }
        return true;
    }

}
