package com.huawei.service.hutaf.idac.sloth.po.task;

/**
 * 请求参数：请求时间段内任务
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月20日
 */
public class ReqTask
{
    private String startTime;

    private String endTime;

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

}
