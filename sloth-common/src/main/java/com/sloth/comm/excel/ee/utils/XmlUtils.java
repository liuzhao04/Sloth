package com.sloth.comm.excel.ee.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * XSSF Xml工具
 * 
 * @author liuzhao04
 * @version 1.0, 2017年2月10日
 */
public class XmlUtils
{
    /**
     * 由数据流转成Document
     * 
     * @param is
     * @return
     * @throws Exception
     */
    public static Document document(InputStream is) throws Exception
    {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    }

    /**
     * 获取所有的Sheet名称
     * 
     * @param xssfReader
     * @return
     * @throws XPathExpressionException
     * @throws InvalidFormatException
     * @throws IOException
     * @throws Exception
     */
    public static List<String> getSheetNames(XSSFReader xssfReader) throws XPathExpressionException,
                                                                    InvalidFormatException,
                                                                    IOException,
                                                                    Exception
    {
        NodeList nl = searchForNodeList(document(xssfReader.getWorkbookData()), "/workbook/sheets/sheet");
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < nl.getLength(); i++)
        {
            String name = nl.item(i).getAttributes().getNamedItem("name").getNodeValue();
            names.add(name);
        }
        return names;
    }

    /**
     * 查找节点
     * 
     * @param document
     * @param xpath
     * @return
     * @throws XPathExpressionException
     */
    public static NodeList searchForNodeList(Document document, String xpath) throws XPathExpressionException
    {
        return (NodeList)XPathFactory.newInstance().newXPath().compile(xpath).evaluate(document,
                                                                                       XPathConstants.NODESET);
    }

    /**
     * 查找指定的sheet
     * 
     * @param reader
     * @param sheetName
     * @return
     * @throws XPathExpressionException
     * @throws Exception
     */
    public static InputStream findSheet(XSSFReader reader, String sheetName) throws XPathExpressionException, Exception
    {
        int index = -1;
        if (sheetName != null)
        {
            // This file is separate from the worksheet data, and should be
            // fairly small
            NodeList nl = searchForNodeList(document(reader.getWorkbookData()), "/workbook/sheets/sheet");
            for (int i = 0; i < nl.getLength(); i++)
            {
                if (Objects.equals(nl.item(i).getAttributes().getNamedItem("name").getNodeValue(), sheetName))
                {
                    index = i;
                }
            }
            if (index < 0)
            {
                return null;
            }
        }
        Iterator<InputStream> iter = reader.getSheetsData();
        InputStream sheet = null;

        int i = 0;
        while (iter.hasNext())
        {
            InputStream is = iter.next();
            if (i++ == index)
            {
                sheet = is;
                break;
            }
        }
        return sheet;
    }
}
