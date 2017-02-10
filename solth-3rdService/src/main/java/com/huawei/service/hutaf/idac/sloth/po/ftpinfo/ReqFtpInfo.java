package com.huawei.service.hutaf.idac.sloth.po.ftpinfo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * FTP请求参数
 *
 * @author lWX306898
 * @version 1.0, 2017年1月22日
 */
public class ReqFtpInfo
{
    private String taskinfoid;

    private List<String> testsuits;

    public String getTaskinfoid()
    {
        return taskinfoid;
    }

    public void setTaskinfoid(String taskinfoid)
    {
        this.taskinfoid = taskinfoid;
    }

    public List<String> getTestsuits()
    {
        return testsuits;
    }

    public void setTestsuits(List<String> testsuits)
    {
        this.testsuits = testsuits;
    }

    @Override
    public String toString()
    {
        return "ReqFtpInfo [taskinfoid=" + taskinfoid + ", testsuits=" + testsuits + "]";
    }

    /**
     * 转成 json string
     * 
     * @return
     */
    public String toJsonString()
    {
        return "{\"param\":" + JSON.toJSONString(this) + "}";
    }

    /**
     * 对当前请求进行分组
     *
     * @param groupSize
     * @return
     */
    public List<ReqFtpInfo> group(int groupSize)
    {
        // 最小分组为1
        if (groupSize < 1)
        {
            groupSize = 1;
        }

        int index = 0;
        List<ReqFtpInfo> groups = new ArrayList<ReqFtpInfo>();
        for (int i = 0; i < testsuits.size(); i++)
        {
            ReqFtpInfo rfi = null;
            if (index == 0)
            {
                rfi = new ReqFtpInfo();
                rfi.setTaskinfoid(taskinfoid);
                rfi.setTestsuits(new ArrayList<String>());
                groups.add(rfi);
            }
            else
            {
                rfi = groups.get(groups.size() - 1);
            }

            rfi.getTestsuits().add(testsuits.get(i));
            if (index == groupSize - 1)
            {
                index = -1;
            }
            index++;
        }
        return groups;
    }
}
