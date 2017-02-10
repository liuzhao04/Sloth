package com.sloth.comm.excel.event;

import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 大型Excel读取类
 *
 * @author lWX306898
 * @version 1.0, 2016年8月4日
 */
public class BigExcelReader
{
    private XSSFReader xssfReader;

    private ReadOnlySharedStringsTable strings;

    private StylesTable styles;

    public BigExcelReader(String path) throws Exception
    {
        OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
        strings = new ReadOnlySharedStringsTable(p);
        xssfReader = new XSSFReader(p);
        styles = xssfReader.getStylesTable();
    }

    public void parse(String sheetId, SheetHandler sheetHandler) throws Exception
    {
        InputStream iStream = xssfReader.getSheet(sheetId);
        InputSource iSource = new InputSource(iStream);
        XMLReader sheetParser = SAXHelper.newXMLReader();
        DataFormatter formatter = new DataFormatter();
        ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(iSource);
        iStream.close();
    }
}
