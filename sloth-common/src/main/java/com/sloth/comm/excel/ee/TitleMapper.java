package com.sloth.comm.excel.ee;

import java.util.Arrays;
import java.util.List;

/**
 * 标题映射定义（所有未指定映射关系的列，在读取数据时被丢掉）
 *
 * @author lWX306898
 * @version 1.0, 2017年3月1日
 */
public class TitleMapper
{
    private ESheet sheet;

    private int titleRow;

    private List<String> eTitles;

    private List<String> mTitles;

    private int[] indexs;

    protected TitleMapper()
    {
    }

    public ESheet getSheet()
    {
        return sheet;
    }

    public void setSheet(ESheet sheet)
    {
        this.sheet = sheet;
    }

    public int getTitleRow()
    {
        return titleRow;
    }

    public void setTitleRow(int titleRow)
    {
        this.titleRow = titleRow;
    }

    public List<String> geteTitles()
    {
        return eTitles;
    }

    public void seteTitles(List<String> eTitles)
    {
        this.eTitles = eTitles;
    }

    public List<String> getmTitles()
    {
        return mTitles;
    }

    public void setmTitles(List<String> mTitles)
    {
        this.mTitles = mTitles;
    }

    public int[] getIndexs()
    {
        return indexs;
    }

    public void setIndexs(int[] indexs)
    {
        this.indexs = indexs;
    }

    @Override
    public String toString()
    {
        return "TitleMapper [sheet="
               + sheet
               + ", titleRow="
               + titleRow
               + ", eTitles="
               + eTitles
               + ", mTitles="
               + mTitles
               + ", indexs="
               + Arrays.toString(indexs)
               + "]";
    }

    /**
     * 判断一个TitleMapper是否无效
     *
     * @return
     */
    public boolean isInvalid()
    {
        if (sheet == null || sheet.isEmpty())
        {
            return true;
        }

        if (titleRow < 0)
        {
            return true;
        }

        if (eTitles == null || eTitles.size() == 0)
        {
            return true;
        }

        // mTitles 可以为空，为空时，采用原标题作为输出
        return false;
    }

}
