package com.sloth.configure;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 配置获取帮助工具
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月24日
 */
public class ConfigureHelp
{
    private Map<String, String> cfgMap; // 配置文件

    private String compName; // 组件名称

    public ConfigureHelp(Map<String, String> cfgMap, String compName)
    {
        this.cfgMap = cfgMap;
        this.compName = compName;
    }

    public Map<String, String> getCfgMap()
    {
        return cfgMap;
    }

    public void setCfgMap(Map<String, String> cfgMap)
    {
        this.cfgMap = cfgMap;
    }

    public String getCompName()
    {
        return compName;
    }

    public void setCompName(String compName)
    {
        this.compName = compName;
    }

    public String getValue(String key)
    {
        String value = cfgMap.get(compName + "." + key);
        if (StringUtils.isEmpty(value))
        {
            return value;
        }
        return value.trim();
    }
}
