package com.sloth.comm.excel.ee;

/**
 * 单元处理接口
 *
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public interface IParseCell
{
    /**
     * 开始处理某个Sheet页面
     *
     * @param eSheet
     */
    public void startSheet(ESheet eSheet);

    /**
     * 开始处理某行
     *
     * @param rownId 行id
     */
    public void startRow(int rownId);
    
    
    /**
     * 结束处理某行
     *
     * @param rownId 行id
     */
    public void endRow(int rownId);

    /**
     * 单元处理实现
     *
     * @param i
     * @param j
     * @param value
     * @throws Exception
     */
    public void doCell(int i, int j, ECell value);

}
