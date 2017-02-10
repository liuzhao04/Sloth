package com.sloth.comm.excel.ee.xlsx;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sloth.comm.excel.ee.AbsExcelReader;
import com.sloth.comm.excel.ee.ECell;
import com.sloth.comm.excel.ee.ESheet;
import com.sloth.comm.excel.ee.IParseCell;
import com.sloth.comm.excel.ee.utils.XmlUtils;

public class XlsxReader extends AbsExcelReader implements SheetContentsHandler
{
    private XSSFReader xssfReader;

    private ReadOnlySharedStringsTable strings;

    private StylesTable styles;

    private IParseCell iParseCell = null;
    
    private int cRowId = 0;
    private int cColIndex = 0;

    public XlsxReader(String path, String[] sheetNames) throws Exception
    {
        super(path, sheetNames);
    }

    @Override
    protected void initSource(InputStream is) throws Exception
    {
        OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
        strings = new ReadOnlySharedStringsTable(p);
        xssfReader = new XSSFReader(p);
        styles = xssfReader.getStylesTable();
    }

    @Override
    public void destory()
    {

    }

    @Override
    public void start(IParseCell iParseCell) throws Exception
    {
        this.iParseCell = iParseCell;
        eSheet = null;
        List<String> cSheetNames = XmlUtils.getSheetNames(xssfReader);
        int index = 0;
        for(int i = 0; i < cSheetNames.size(); i++)
        {
            String name = cSheetNames.get(index++);
            if (eSheet == null)
            {
                eSheet = new ESheet();
            }
            eSheet.setName(name);
            eSheet.nextIndex();

            if (needMatchSheetNames && !isMatchedSheet())
            {
                continue;
            }
            iParseCell.startSheet(eSheet);
            InputStream iStream = XmlUtils.findSheet(xssfReader, name);
            if(iStream == null)
            {
                continue;
            }
            InputSource iSource = new InputSource(iStream);
            XMLReader sheetParser = SAXHelper.newXMLReader();
            DataFormatter formatter = new DataFormatter();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, this, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(iSource);
            iStream.close();
        }

    }

    @Override
    public void cell(String cellId, String value, XSSFComment content)
    {
        ECell eCell = new ECell();
        eCell.setValue(value);
        iParseCell.doCell(cRowId, this.cColIndex++, eCell);
    }

    @Override
    public void endRow(int rowNum)
    {

    }

    @Override
    public void headerFooter(String arg0, boolean arg1, String arg2)
    {

    }

    @Override
    public void startRow(int rowId)
    {
        this.cRowId = rowId;
        this.cColIndex = 0;
        iParseCell.startRow(rowId, -1, -1);
    }

}
