package com.huawei.service.hutaf.idac.sloth.po.ftpinfo;

/**
 * 返回的ftp信息
 *
 * @author lWX306898
 * @version 1.0, 2017年1月22日
 */
public class ResFtpInfo
{
    private String logPath;

    private String blockId;

    public String getLogPath()
    {
        return logPath;
    }

    public void setLogPath(String logPath)
    {
        this.logPath = logPath;
    }

    public String getBlockId()
    {
        return blockId;
    }

    public void setBlockId(String blockId)
    {
        this.blockId = blockId;
    }

    @Override
    public String toString()
    {
        return "ResFtpInfo [logPath=" + logPath + ", blockId=" + blockId + "]";
    }

}
