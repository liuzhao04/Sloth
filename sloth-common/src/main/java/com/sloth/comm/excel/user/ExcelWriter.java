package com.sloth.comm.excel.user;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * Excel导入工具类
 * @author yWX366685
 *@version 1.0, 2016年8月18日
 */
public class ExcelWriter implements IExcelWriter
{
    private XSSFWorkbook wb;// 创建一个webbook对象，对应excel表

    private String path;// 导出路径

    private Sheet sheet; // excel页面对象

    private Map<String, Integer> titleMap; // 映射列名与相对应下标

    @Override
    public void init(String outPath)
    {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
        path = outPath;
    }

    @Override
    public void setTitle(int rowIndex, String[] titiles)
    {
        if (titleMap == null)
        {
            titleMap = new HashMap<String, Integer>();
        }
        else
        {
            titleMap.clear();
        }
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < titiles.length; i++)
        {
            Cell cell = row.createCell(i);
            cell.setCellValue(titiles[i]);
            titleMap.put(titiles[i], i);
        }

    }

    @Override
    public void setValue(int rowIndex, int colIndex, String value)
    {
        Row row = sheet.getRow(rowIndex);
        if (row == null)
        {
            row = sheet.createRow(rowIndex);
        }
        row.createCell(colIndex).setCellValue(value);
    }

    @Override
    public void setValue(int rowIndex, String colName, String value)
    {
        Integer index = titleMap.get(colName);
        Row row = sheet.getRow(rowIndex);
        if (row == null)
        {
            row = sheet.createRow(rowIndex);
        }
        row.createCell(index).setCellValue(value);
    }

    @Override
    public void save() throws Exception
    {
        FileOutputStream fos = new FileOutputStream(path);
        wb.write(fos);
        fos.close();
    }

    public static void main(String[] args)
    {
        ExcelWriter eWriter = new ExcelWriter();
        String path = "test/aab.xlsx";
        eWriter.init(path);
        String[] titles = new String[]{"A", "B", "C", "D"};
        eWriter.setTitle(0, titles);
        eWriter.setValue(2, 1, "ABC");
        eWriter.setValue(2, "C", "SUCCESSED");
        try
        {
            eWriter.save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
