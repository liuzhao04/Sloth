package com.huawei.service.hutaf.idac.sloth.po.testsuit;

/**
 * 请求参数：根据任务请求测试套列表
 *
 * @author lWX306898
 * @version 1.0, 2017年1月20日
 */
public class ReqTestsuit
{
    private String taskId;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    @Override
    public String toString()
    {
        return "ReqTestsuit [taskId=" + taskId + "]";
    }

}
