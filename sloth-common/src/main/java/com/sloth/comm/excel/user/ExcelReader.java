package com.sloth.comm.excel.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * Excel读取工具类<br>
 * 对指定的excel进行读取，可以通过两种方式访问cell的字符串值：<br>
 * 1.指定行列坐标<br>
 * 2.指定行坐标，和列名（此方法需要指定标题所在的行，在一个sheet页的读取过程中，标题行可以随时切换）
 *
 * @author lWX306898
 * @version 1.0, 2016年8月1日
 */
public class ExcelReader
{
    private Workbook workbook; // excel操作对象

    private Sheet sheet; // excel页面对象

    private RowReader rowReader; // 行数据读取工具

    private static final Pattern FORMAT_REGEX = Pattern.compile("HYPERLINK\\((\".*\"),\"(.*?)\\s*\"\\)",
                                                                Pattern.DOTALL);

    public ExcelReader(String path) throws EncryptedDocumentException, InvalidFormatException, IOException
    {
        InputStream inp = new FileInputStream(path);
        workbook = WorkbookFactory.create(inp);
        changeSheet(0);
    }

    /**
     * 设置RowReader标题行，设置后，通过colName读取cell时，将以此行为标准
     *
     * @param tRowIndex
     */
    public void changeTitleRow(int tRowIndex)
    {
        if (this.rowReader == null)
        {
            this.rowReader = new RowReader(sheet.getRow(tRowIndex));
        }
        else
        {
            this.rowReader.changeTileRow(sheet.getRow(tRowIndex));
        }
    }

    /**
     * sheet页面切换<br>
     * 注意：每次切换都会将标题行重置为第0行
     *
     * @param sheetIndex sheet页序号
     * @return
     */
    public boolean changeSheet(int sheetIndex)
    {
        int sheetSize = workbook.getNumberOfSheets();
        if (sheetIndex < 0 || sheetIndex >= sheetSize)
        {
            return false;
        }
        sheet = workbook.getSheetAt(sheetIndex);
        changeTitleRow(0);
        return true;
    }

    /**
     * sheet页面切换<br>
     * 注意：每次切换都会将标题行重置为第0行
     *
     * @param sheetName sheet页面名称
     * @return
     */
    public boolean changeSheet(String sheetName)
    {
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (-1 == sheetIndex)
        {
            workbook.createSheet(sheetName);
        }
        sheetIndex = workbook.getSheetIndex(sheetName);
        int sheetSize = workbook.getNumberOfSheets();
        if (sheetIndex < 0 || sheetIndex >= sheetSize)
        {
            return false;
        }
        sheet = workbook.getSheetAt(sheetIndex);
        changeTitleRow(0);
        return true;
    }

    public String getDateVlaue(int rowIndex, int colIndex)
    {
        Row row = sheet.getRow(rowIndex);
        if (row == null)
        {
            return null;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null)
        {
            return null;
        }
        Date date = cell.getDateCellValue();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 获取文件链接
     * 
     *
     * @param rowIndex
     * @param colName
     * @return
     */
    public String getFileLinkValue(int rowIndex, String colName)
    {
        return rowReader.getHyperValue(sheet.getRow(rowIndex), colName);
    }

    /**
     * 获取单元格值-指定行列坐标模式
     *
     * @param rowIndex 行索引
     * @param colIndex 列索引
     * @return
     */
    public String getValue(int rowIndex, int colIndex)
    {
        return rowReader.getValue(sheet.getRow(rowIndex), colIndex);
    }

    /**
     * 获取单元格值-指定行索引，列名 <br>
     * 与changeTitleRow搭配使用，在读取页面数据时可以灵活切换"标题行"
     * 
     * @param rowIndex 行索引
     * @param colName 列名
     * @return
     */
    public String getValue(int rowIndex, String colName)
    {
        return rowReader.getValue(sheet.getRow(rowIndex), colName);
    }

    public String getValue(int rowIndex, String colName, int cellType)
    {
        return rowReader.getValue(sheet.getRow(rowIndex), colName, cellType);
    }

    /**
     * 获取当前sheet页面的行数
     *
     * @return
     */
    public int getRowSize()
    {
        return this.sheet.getLastRowNum() + 1;
    }

    /**
     * 获取当前title行中，有效列名(不为空)的个数
     * 
     * @return
     */
    public int getTitleSize()
    {
        return rowReader.titleMap.size();
    }

    /**
     * 获取标题列表
     *
     * @return
     */
    public Set<String> getTitleList()
    {
        return rowReader.titleMap.keySet();
    }

    /**
     * 行读取工具
     *
     * @author lWX306898
     * @version 1.0, 2016年8月1日
     */
    private class RowReader
    {
        private Map<String, Integer> titleMap; // 标题行的列名与列索引的映射关系表

        public RowReader(Row row)
        {
            changeTileRow(row);
        }

        /**
         * 读取标题行信息，生成映射表
         *
         * @param row
         */
        @SuppressWarnings("deprecation")
        public void changeTileRow(Row row)
        {
            if (titleMap == null)
            {
                titleMap = new HashMap<String, Integer>();
            }
            else
            {
                titleMap.clear();
            }
            if (row == null)
            {
                return;
            }
            int size = row.getPhysicalNumberOfCells();
            for (int i = 0; i < size; i++)
            {
                Cell cell = row.getCell(i);
                if (cell == null)
                {
                    continue;
                }
                String value = getFormatCellValue(cell.getCellType(), cell);
                if (null == value || "".equals(value))
                {
                    continue;
                }
                titleMap.put(value, i);
            }
        }

        /**
         * 格式化单元格的值
         *
         * @param cell
         * @return
         */
        @SuppressWarnings("deprecation")
        public String getFormatCellValue(int type, Cell cell)
        {
            String value = null;
            switch (type)
            {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                    {
                        Date date = cell.getDateCellValue();
                        if (date != null)
                        {
                            value = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        }
                    }
                    else
                    {
                        value = new DecimalFormat("###.###").format(cell.getNumericCellValue());
                    }
                    break;
                case XSSFCell.CELL_TYPE_FORMULA:
                    Matcher m = FORMAT_REGEX.matcher(cell.getCellFormula());
                    if (m.find())
                    {
                        value = m.group(2);
                    }
                    else
                    {
                        value = getFormatCellValue(cell.getCachedFormulaResultType(), cell);
                    }
                    break;
                case XSSFCell.CELL_TYPE_BLANK:
                case XSSFCell.CELL_TYPE_ERROR:
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    value = (cell.getBooleanCellValue() == true ? "true" : "false");
                    break;
                default:
            }
            return value;
        }

        /**
         * 通过索引访问cell值
         *
         * @param row
         * @param index
         * @return
         */
        @SuppressWarnings("deprecation")
        public String getValue(Row row, int index)
        {
            if (row == null)
            {
                return null;
            }
            Cell cell = row.getCell(index);
            if (cell == null)
            {
                return null;
            }
            return getFormatCellValue(cell.getCellType(), cell);
        }

        /**
         * 通过列名访问cell值
         *
         * @param row
         * @param title
         * @return
         */
        @SuppressWarnings("deprecation")
        public String getValue(Row row, String title)
        {
            if (row == null)
            {
                return null;
            }
            Integer index = titleMap.get(title);
            if (index == null)
            {
                return null;
            }
            Cell cell = row.getCell(index);
            if (cell == null)
            {
                return null;
            }
            return getFormatCellValue(cell.getCellType(), cell);
        }

        @SuppressWarnings("deprecation")
        public String getValue(Row row, String title, int cellType)
        {
            if (row == null)
            {
                return null;
            }
            Integer index = titleMap.get(title);
            if (index == null)
            {
                return null;
            }
            Cell cell = row.getCell(index);
            if (cell == null)
            {
                return null;
            }
            switch (cellType)
            {
                case Cell.CELL_TYPE_STRING:
                    return cell.toString();
                default:
                    break;
            }
            return getFormatCellValue(cellType, cell);
        }

        /**
         * 获取特定的检查项中的链接
         * 
         *
         * @param row
         * @param title
         * @return
         */
        @SuppressWarnings("deprecation")
        public String getHyperValue(Row row, String title)
        {
            String value = null;
            if (row == null)
            {
                return null;
            }
            Integer index = titleMap.get(title);
            if (index == null)
            {
                return null;
            }
            Cell cell = row.getCell(index);
            if (cell == null)
            {
                return null;
            }
            // String a =cell.getCellFormula();
            if (cell.getCellType() == 2)
            {
                Matcher m = FORMAT_REGEX.matcher(cell.getCellFormula());
                if (m.find())
                {
                    value = m.group(1);
                }
                if (!StringUtils.isBlank(value))
                {
                    return value.substring(1, value.length() - 1);
                }
            }
            return null;
        }
    }

    /**
     * 将null转成""
     *
     * @param value
     * @return
     */
    public static String formatNullValue(String value)
    {
        if (value == null)
        {
            return "";
        }
        return value;
    }

}
