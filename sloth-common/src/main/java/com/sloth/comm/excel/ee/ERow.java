package com.sloth.comm.excel.ee;

import java.util.HashMap;
import java.util.Map;

/**
 * 行对象
 * 
 * @author lWX306898
 * @version 1.0, 2017年3月1日
 */
public class ERow
{
    private int rowId;

    private Map<String, ECell> rValue;

    public ERow(int rowId)
    {
        rValue = new HashMap<String, ECell>();
        this.rowId = rowId;
    }

    public void put(String colId, ECell value)
    {
        rValue.put(colId, value);
    }

    public ECell get(String colId)
    {
        return rValue.get(colId);
    }

    public int getRowId()
    {
        return this.rowId;
    }

    public Map<String, ECell> getValue()
    {
        return rValue;
    }

    @Override
    public String toString()
    {
        return "ERow [rowId=" + rowId + ", rValue.size=" + rValue.size() + "]";
    }

}
