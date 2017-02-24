package com.dm.cube.sloth.perf.source;

import com.sloth.msg.MessageUnit;

/**
 * 消息单元
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月17日
 */
public class ExcelMessageUnit extends MessageUnit
{
    private static final long serialVersionUID = -8311929699400925631L;

    public ExcelMessageUnit(int colId, Object value)
    {
        super(value);
        this.colId = colId;
    }

    private int colId;

    public int getColId()
    {
        return colId;
    }

    public void setColId(int colId)
    {
        this.colId = colId;
    }

}
