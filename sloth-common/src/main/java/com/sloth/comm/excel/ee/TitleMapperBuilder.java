package com.sloth.comm.excel.ee;

import java.util.ArrayList;
import java.util.List;

/**
 * TitleMapper对象构造器(构造者模式)
 * 
 * @author lWX306898
 * @version 1.0, 2017年3月1日
 */
public class TitleMapperBuilder
{
    private TitleMapper tm = null;

    public TitleMapperBuilder()
    {
        reset();
    }

    private void reset()
    {
        tm = new TitleMapper();
    }

    public TitleMapperBuilder addSheet(String sheetName)
    {
        ESheet es = tm.getSheet();
        if (es == null)
        {
            es = new ESheet();
            tm.setSheet(es);
        }
        es.setName(sheetName);
        return this;
    }

    public TitleMapperBuilder addSheet(int sheetId)
    {
        ESheet es = tm.getSheet();
        if (es == null)
        {
            es = new ESheet();
            tm.setSheet(es);
        }
        es.setIndex(sheetId);
        return this;
    }

    public TitleMapperBuilder addETitle(String etitle)
    {
        List<String> ets = tm.geteTitles();
        if (ets == null)
        {
            ets = new ArrayList<String>();
            tm.seteTitles(ets);
        }
        ets.add(etitle);
        return this;
    }

    public TitleMapperBuilder addETitles(List<String> etitles)
    {
        List<String> ets = tm.geteTitles();
        if (ets == null)
        {
            ets = new ArrayList<String>();
            tm.seteTitles(ets);
        }
        ets.addAll(etitles);
        return this;
    }

    public TitleMapperBuilder addMTitle(String mtitle)
    {
        List<String> mts = tm.getmTitles();
        if (mts == null)
        {
            mts = new ArrayList<String>();
            tm.setmTitles(mts);
        }
        mts.add(mtitle);
        return this;
    }

    public TitleMapperBuilder addMTitles(List<String> mtitles)
    {
        List<String> mts = tm.getmTitles();
        if (mts == null)
        {
            mts = new ArrayList<String>();
            tm.setmTitles(mts);
        }
        mts.addAll(mtitles);
        return this;
    }

    public TitleMapper build()
    {
        if (tm == null || tm.isInvalid())
        {
            throw new ExceptionInInitializerError("表头映射信息不全");
        }
        int[] indexs = new int[tm.geteTitles().size()];
        tm.setIndexs(indexs);
        TitleMapper tmp = tm;
        reset();
        return tmp;
    }
}
