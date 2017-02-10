package com.huawei.service.hutaf.idac.sloth.po.testsuit;

/**
 * 返回的测试套对象
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月22日
 */
public class ResTestsuit
{
    private String tsId;

    private String tsName;

    public String getTsId()
    {
        return tsId;
    }

    public void setTsId(String tsId)
    {
        this.tsId = tsId;
    }

    public String getTsName()
    {
        return tsName;
    }

    public void setTsName(String tsName)
    {
        this.tsName = tsName;
    }

    @Override
    public String toString()
    {
        return "ResTestsuit [tsId=" + tsId + ", tsName=" + tsName + "]";
    }

}
