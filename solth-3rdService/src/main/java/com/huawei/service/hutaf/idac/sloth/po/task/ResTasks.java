package com.huawei.service.hutaf.idac.sloth.po.task;

import java.util.List;

/**
 * 返回的任务列表对象
 *
 * @author lWX306898
 * @version 1.0, 2017年1月20日
 */
public class ResTasks
{
    private List<ResTask> tasks;

    public List<ResTask> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<ResTask> tasks)
    {
        this.tasks = tasks;
    }

    @Override
    public String toString()
    {
        return "ResTasks [tasks=" + tasks + "]";
    }

}
