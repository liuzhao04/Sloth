package com.huawei.service.hutaf.idac.sloth.po.task;

/**
 * 返回的任务信息
 *
 * @author lWX306898
 * @version 1.0, 2017年1月20日
 */
public class ResTask
{
    private String id;

    private String name;

    private String startTimeStr;

    private String versionname;

    private String versionUri;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStartTimeStr()
    {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr)
    {
        this.startTimeStr = startTimeStr;
    }

    public String getVersionname()
    {
        return versionname;
    }

    public void setVersionname(String versionname)
    {
        this.versionname = versionname;
    }

    public String getVersionUri()
    {
        return versionUri;
    }

    public void setVersionUri(String versionUri)
    {
        this.versionUri = versionUri;
    }

    @Override
    public String toString()
    {
        return "ResTask [id="
               + id
               + ", name="
               + name
               + ", startTimeStr="
               + startTimeStr
               + ", versionname="
               + versionname
               + ", versionUri="
               + versionUri
               + "]";
    }

}
