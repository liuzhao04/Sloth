package com.sloth.comm.excel.event;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * 大型Excel Sheet页面内容处理句柄类
 *
 * @author liuzhao04
 * @version 1.0, 2016年8月4日
 */
public abstract class SheetHandler implements SheetContentsHandler
{
    private Pattern CELL_ID_SPLIT_REGEX = Pattern.compile("(\\w+?)(\\d+)");

    private int titleRowId = 1;

    private Map<String, String> titleMap = null;

    private Map<String, String> resultRow = null;

    public SheetHandler(int titleRowId)
    {
        this.titleRowId = titleRowId;
        titleMap = new HashMap<String, String>();
        resultRow = new HashMap<String, String>();
    }

    private String[] parserId(String cellId)
    {
        Matcher m = CELL_ID_SPLIT_REGEX.matcher(cellId);
        String[] ids = new String[2];
        if (m.find())
        {
            ids[0] = m.group(1);
            ids[1] = m.group(2);
        }
        return ids;
    }

    @Override
    public void cell(String cellId, String value, XSSFComment content)
    {
        String[] ids = parserId(cellId);
        String colId = ids[0];
        int rowId = Integer.parseInt(ids[1]);

        if (rowId == titleRowId)
        {
            if (value != null && !"".equals(value.trim()))
            {
                titleMap.put(colId, value.trim());
            }
        }

        if (rowId > titleRowId)
        {
            resultRow.put(titleMap.get(colId), value.trim());
        }
    }

    @Override
    public void endRow(int num)
    {
        if ((num + 1) > this.titleRowId)
        {
            doRow(num + 1, resultRow);
            resultRow.clear();
        }
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName)
    {
        System.out.println(isHeader ? "Header" : "Footer" + " ," + text + "," + tagName);
    }

    @Override
    public void startRow(int num)
    {
    }

    public abstract void doRow(int i, Map<String, String> resultRow);

}
