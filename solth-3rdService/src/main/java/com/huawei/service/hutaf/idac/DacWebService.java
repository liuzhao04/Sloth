package com.huawei.service.hutaf.idac;

import java.net.URL;
import java.text.MessageFormat;

import org.codehaus.xfire.client.Client;

/**
 * Dac基础服务调用（基于xfire 客户端）
 * 
 * @author lWX306898
 * @version 1.0, 2017年1月23日
 */
public class DacWebService
{
    // 服务器ip
    private final String DAC_SERVER_ENDPOINT;

    public DacWebService(String serverIp)
    {
        DAC_SERVER_ENDPOINT = MessageFormat.format("http://{0}:8094/dacService?wsdl", serverIp);
    }

    /**
     * 获取指定时间区内的所有任务信
     *
     * @param info
     * @return
     * @throws Exception
     */
    public String getTaskInfoByTimeLimit(String info) throws Exception
    {
        return invoke("getTaskInfoByTimeLimit", info);
    }

    /**
     * 查询测试套信息
     * 
     * @param info
     * @return
     * @throws Exception
     */
    public String getTestsuitsByTaskId(String info) throws Exception
    {
        return invoke("getTestsuitsByTaskId", info);
    }

    /**
     * Xfire 调用WebService函数
     *
     * @param op 函数名称
     * @param info 传递参数
     * @return 返回String类型
     * @throws Exception
     */
    private String invoke(String op, String info) throws Exception
    {
        Client client = new Client(new URL(DAC_SERVER_ENDPOINT));
        Object[] results = client.invoke(op, (new Object[]{info}));
        return results[0].toString();
    }
}
