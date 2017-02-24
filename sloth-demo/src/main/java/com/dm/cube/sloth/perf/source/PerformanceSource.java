package com.dm.cube.sloth.perf.source;

import java.io.File;
import java.util.LinkedList;

import com.sloth.comm.excel.ee.ECell;
import com.sloth.comm.excel.ee.EEReader;
import com.sloth.comm.excel.ee.ESheet;
import com.sloth.comm.excel.ee.IParseCell;
import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.exception.init.InitSourceException;
import com.sloth.msg.Message;
import com.sloth.msg.MessageBuilder;
import com.sloth.msg.MessageUnit;
import com.sloth.source.ISource;

/**
 * 性能数据源
 * 
 * @author liuzhao04
 * @version 1.0, 2017年1月26日
 */
public class PerformanceSource implements ISource, IParseCell
{
    private String path = "C:\\Users\\liuzhao04\\Desktop\\性能分析\\B120_t1xlwt.xls";

    private EEReader eeReader = null;

    private String[] sheetNames = new String[]{"汇总M", "分区M"};

    @Override
    public void init(ConfigureHelp cfgHelp) throws SlothException
    {
        try
        {
            eeReader = new EEReader(this.path, sheetNames, this);
        }
        catch (Exception e)
        {
            throw new InitSourceException("Excel读取工具初始化失败", e);
        }
        try
        {
            // 数据源异步加载
            eeReader.start();
        }
        catch (Exception e)
        {
            throw new InitSourceException("Excel数据预读取失败", e);
        }
    }

    @Override
    public Message getMessage() throws SlothException
    {
        return cacheList.removeFirst();
    }

    @Override
    public void destory()
    {
        if (eeReader != null)
        {
            eeReader.destory();
        }
        eeReader.destory();
    }

    @Override
    public boolean hasNext()
    {
        if (cacheList.size() > 0)
        {
            return true;
        }
        return false;
    }

    private ESheet eSheet = null;

    private LinkedList<Message> cacheList = null;

    private MessageBuilder msgbd = null;

    @Override
    public void startSheet(ESheet eSheet)
    {
        this.eSheet = eSheet;
        if (cacheList == null)
        {
            cacheList = new LinkedList<Message>();
        }
        msgbd = new MessageBuilder();
        msgbd.addHead("fileName", new File(path).getName());
    }

    @Override
    public void startRow(int rowNum)
    {
        msgbd.clear();
        msgbd.addHead("sheetId", eSheet.getIndex() + "");
        msgbd.addHead("sheetName", eSheet.getName());
        msgbd.addHead("rowId", rowNum + "");
    }

    @Override
    public void doCell(int i, int j, ECell value)
    {
        // 将XLS_NUMBER的值转成整数
        if (value.getType().equals(ECell.ECellType.XLS_NUMBER))
        {
            String val = value.getValue().replaceAll(".0$", "");
            value.setValue(val);
        }
        MessageUnit mu = new ExcelMessageUnit(j, value);
        msgbd.add(mu);
    }

    @Override
    public void endRow(int rownId)
    {
        cacheList.addLast(msgbd.build());
    }

}
