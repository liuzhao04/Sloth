package com.sloth.comm.excel.ee;

import org.apache.commons.lang3.StringUtils;

/**
 * Sheet基础信息
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月9日
 */
public class ESheet
{
    private String name;

    private int index = -1;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    @Override
    public String toString()
    {
        return "ESheet [name=" + name + ", index=" + index + "]";
    }

    public void nextIndex()
    {
        index++;
    }

    public boolean isTheSame(String name2)
    {
        if (name == null)
        {
            return false;
        }
        if (!name.equals(name2))
        {
            return false;
        }
        return true;
    }

    public boolean isTheSameByIndex(int index)
    {
        return this.index == index;
    }

    public boolean isEmpty()
    {
        return index == -1 && StringUtils.isEmpty(name);
    }
}
