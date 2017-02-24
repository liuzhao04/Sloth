package com.dm.cube.sloth.perf.handle;

import java.util.HashMap;
import java.util.Map;

import com.dm.cube.sloth.perf.source.ExcelMessageUnit;
import com.sloth.comm.excel.ee.ECell;
import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.handle.IHandle;
import com.sloth.msg.Message;
import com.sloth.msg.MessageUnit;

/**
 * 汇总表处理
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月17日
 */
public class GatherMessageHandle implements IHandle
{
    private String sheetName = null;

    private int sheetId = 0;

    private String rowId = null;

    private Map<Integer, String> colNameCache = null;

    private boolean hasInitFileInfor = false;

    private String fileName = null;

    @Override
    public void init(ConfigureHelp hHelp) throws SlothException
    {
        colNameCache = new HashMap<Integer, String>();
    }

    @Override
    public Message handle(Message msg) throws SlothException
    {
        if (sheetName == null)
        {
            sheetName = msg.getHead().get("sheetName");
        }
        String rowId_ = msg.getHead().get("rowId");
        if (!rowId_.equals(this.rowId))
        {
            this.rowId = rowId_;
        }

        // 初始化文件信息
        if (!hasInitFileInfor)
        {
            fileName = msg.getHead().get("fileName");
            // TODO: 文件信息初始化

            hasInitFileInfor = true;
        }

        int iRowId = Integer.parseInt(rowId_);
        for (MessageUnit mu : msg.getBody())
        {
            ExcelMessageUnit emu = (ExcelMessageUnit)mu;
            ECell eVal = (ECell)emu.getValue();
            int iColId = emu.getColId();
            // a. 标题栏，生成映射表
            if (iRowId == 0)
            {
                colNameCache.put(iColId, eVal.getValue());
            }
            // b. 非标题栏
            else
            {
            }
        }

        if (iRowId == 0)
        {
            // TODO: 提交文件初始化信息:sheet 映射；列名映射；
        }
        else
        {
            // TODO: 所有数据
        }

        return msg;
    }

    @Override
    public void destory()
    {
        // TODO Auto-generated method stub

    }

}
