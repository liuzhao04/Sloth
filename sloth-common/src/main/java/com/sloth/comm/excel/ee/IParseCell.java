package com.sloth.comm.excel.ee;

/**
 * 单元处理接口
 *
 * @author lWX306898
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
     * @param rowNum 行id
     * @param sCol 列起点
     * @param eCol 列终点
     */
    public void startRow(int rowNum, int sCol, int eCol);

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
