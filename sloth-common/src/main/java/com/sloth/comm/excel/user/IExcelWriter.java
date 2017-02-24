package com.sloth.comm.excel.user;

import java.io.FileNotFoundException;

/**
 * Excel导入工具类
 *
 * @author liuzhao04
 * @version 1.0, 2016年8月12日
 */
public interface IExcelWriter
{
    /**
     * 指定输出文件
     *
     * @param outPath
     */
    public void init(String outPath)throws FileNotFoundException;

    /**
     * 指定标签行
     * 
     * @param rowIndex
     * @param titiles
     */
    public void setTitle(int rowIndex, String[] titiles);

    /**
     * 根据坐标指定值
     * 
     * @param rowIndex
     * @param colIndex
     * @param value
     */
    public void setValue(int rowIndex, int colIndex, String value);

    /**
     * 根据列名指定值
     * 
     * @param rowIndex
     * @param colName
     * @param value
     */
    public void setValue(int rowIndex, String colName, String value);
    
    /**
     * 保存到文件
     * @throws FileNotFoundException 
     *
     */
    public void save() throws Exception;
}
