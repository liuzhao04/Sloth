package com.dm.cube.sloth.perf.filter;

import java.util.Map;

import com.sloth.handle.filter.MessageFilter;

/**
 * 汇总信息过滤器
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月17日
 */
public class GatherMessageFilter extends MessageFilter
{
    @Override
    protected boolean filterHeader(Map<String, String> head)
    {
        String sname = head.get("sheetName");
        if ("汇总M".equals(sname))
        {
            return true;
        }
        return false;
    }

}
