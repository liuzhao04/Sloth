package com.sloth.comm.excel.ee;

import com.sloth.comm.excel.ee.xls.XlsReader;
import com.sloth.comm.excel.ee.xlsx.XlsxReader;

/**
 * 事件驱动Excel读取工具（兼容03/07版Excel）
 * 
 * @author liuzhao04
 * @version 1.0, 2017年2月9日
 */
public class EEReader
{
    private AbsExcelReader reader = null;

    private IParseCell iParseCell = null;

    public EEReader(String path, String[] sheetNames, IParseCell iParseCell) throws Exception
    {
        this.iParseCell = iParseCell;
        if (path.toLowerCase().endsWith(".xls"))
        {
            reader = new XlsReader(path, sheetNames);
        }
        else
        {
            reader = new XlsxReader(path, sheetNames);
        }
    }

    /**
     * 开始处理
     * 
     * @throws Exception
     */
    public void start() throws Exception
    {
        reader.start(this.iParseCell);
    }

    /**
     * 销毁资源
     *
     */
    public void destory()
    {
        reader.destory();
    }
}
