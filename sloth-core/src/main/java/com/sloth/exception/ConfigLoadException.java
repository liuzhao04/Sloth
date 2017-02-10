package com.sloth.exception;

/**
 * 配置文件加载异常
 *
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
public class ConfigLoadException extends SlothException
{
    private static final long serialVersionUID = 2046430172994897843L;

    public ConfigLoadException(String file)
    {
        super("配置文件加载异常");
    }

    public ConfigLoadException(String file, Throwable e)
    {
        super("配置文件加载异常", e);
    }
}
