package com.sloth.comm.excel.ee;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Excel读取抽象类
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月9日
 */
public abstract class AbsExcelReader
{
    protected String path; // 文件路径

    protected String[] sheetNames; // 需要读取的页面列表（null表示读取所有的）

    protected ESheet eSheet;

    protected boolean needMatchSheetNames = true;

    /**
     * 实例化，读取指定的sheetNames页面的数据
     * 
     * @param path
     * @param sheetNames null表示读取所有的页面
     * @throws Exception
     */
    public AbsExcelReader(String path, String[] sheetNames) throws Exception
    {
        this.path = path;
        this.sheetNames = sheetNames;
        // 如果sheetNames为null，处理所有sheet页面
        if (ArrayUtils.isEmpty(sheetNames))
        {
            needMatchSheetNames = false;
        }
        init();
    }

    private void init() throws Exception
    {
        FileInputStream fis = new FileInputStream(this.path);
        initSource(fis);
    }

    /**
     * 初始化数据源
     *
     * @param is
     * @throws Exception
     */
    protected abstract void initSource(InputStream is) throws Exception;

    /**
     * 启动读取任务
     * 
     * @param iParseCell 转换接口
     * @throws Exception
     */
    public abstract void start(IParseCell iParseCell) throws Exception;

    /**
     * 销毁读取工具
     * 
     */
    public abstract void destory();

    /**
     * 当前页面是否满足sheet过滤条件
     *
     * @return
     */
    protected boolean isMatchedSheet()
    {
        // 异常调用，返回false
        if (ArrayUtils.isEmpty(sheetNames))
        {
            return false;
        }
        if (eSheet == null)
        {
            return false;
        }
        if (StringUtils.isEmpty(eSheet.getName()))
        {
            return false;
        }
        return ArrayUtils.contains(sheetNames, eSheet.getName());
    }

}
