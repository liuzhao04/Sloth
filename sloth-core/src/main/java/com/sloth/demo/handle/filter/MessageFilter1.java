package com.sloth.demo.handle.filter;

import java.util.Map;

import com.sloth.handle.filter.MessageFilter;

/**
 * 过滤出ID 为偶数的数据
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月26日
 */
public class MessageFilter1 extends MessageFilter
{

    @Override
    protected boolean filterHeader(Map<String, String> head)
    {
        int id = Integer.parseInt(head.get("id"));
        if (id % 2 == 1)
        {
            return true;
        }
        return false;
    }

}
