package com.sloth.comm.excel.ee;

/**
 * Cell定义
 *
 * @author lWX306898
 * @version 1.0, 2017年1月26日
 */
public class ECell
{
    private ECellType type;

    private String value;

    public ECell(String value)
    {
        this.type = ECellType.XLSX_STRING;
        this.value = value;
    }
    
    public ECell()
    {
        this.type = ECellType.XLSX_STRING;
    }

    public ECell(ECellType type, String value)
    {
        this.type = type;
        this.value = value;
    }
    
    public ECell(ECellType type)
    {
        this.type = type;
    }

    public ECellType getType()
    {
        return type;
    }

    public void setType(ECellType type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ECell [type=" + type + ", value=" + value + "]";
    }

    /**
     * 单元格类型
     *
     * @author lWX306898
     * @version 1.0, 2017年2月10日
     */
    public enum ECellType
    {
     XLS_NUMBER, XLS_STRING, XLSX_STRING
    }
}
