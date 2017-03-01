package com.sloth.comm.excel.ee;

/**
 * 行处理接口
 *
 * @author lWX306898
 * @version 1.0, 2017年3月1日
 */
public interface IParseRow
{

    /**
     * 开始处理某个页面
     *
     * @param eSheet
     */
    public void startSheet(ESheet eSheet);

    /**
     * 处理某个行
     *
     * @param row
     */
    public void doRow(ERow row);
}
