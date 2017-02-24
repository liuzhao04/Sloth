package com.sloth.comm.excel.ee.xls;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.sloth.comm.excel.ee.AbsExcelReader;
import com.sloth.comm.excel.ee.ECell;
import com.sloth.comm.excel.ee.ECell.ECellType;
import com.sloth.comm.excel.ee.ESheet;
import com.sloth.comm.excel.ee.IParseCell;

/**
 * Excel读取工具
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月9日
 */
public class XlsReader extends AbsExcelReader implements HSSFListener
{
    private InputStream din;

    private POIFSFileSystem poifs;

    private HSSFRequest req;

    private InputStream is;

    private SSTRecord sstrec;

    private IParseCell iParseCell;

    private List<String> cSheetNames;

    private int sheetIndex = -1;

    private int lastRowId = -1;

    private Map<Integer, Integer> colFlag;

    private boolean hasEndLastRow = true;

    public XlsReader(String path, String[] sheetNames) throws Exception
    {
        super(path, sheetNames);
        cSheetNames = new ArrayList<String>();
        colFlag = new HashMap<Integer, Integer>();
    }

    @Override
    protected void initSource(InputStream is) throws Exception
    {
        this.is = is;
        poifs = new POIFSFileSystem(is);
        // get the Workbook (excel part) stream in a InputStream
        din = poifs.createDocumentInputStream("Workbook");
        // construct out HSSFRequest object
        req = new HSSFRequest();
        // lazy listen for ALL records with the listener shown above
        req.addListenerForAllRecords(this);
        // poifs.close();
    }

    @Override
    public void start(IParseCell iParseCell) throws Exception
    {
        cSheetNames.clear();
        eSheet = null;
        din.reset();
        this.iParseCell = iParseCell;
        // create our event factory
        HSSFEventFactory factory = new HSSFEventFactory();
        // process our events based on the document input stream
        factory.processEvents(req, din);
    }

    @Override
    public void destory()
    {
        if (colFlag != null)
        {
            colFlag.clear();
            colFlag = null;
        }
        if (is != null)
        {
            try
            {
                is.close();
                is = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (din != null)
        {
            try
            {
                din.close();
                din = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (poifs != null)
        {
            try
            {
                poifs.close();
                poifs = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processRecord(Record record)
    {
        switch (record.getSid())
        {
            case BOFRecord.sid: // 即将进来一个新文件
                BOFRecord bof = (BOFRecord)record;
                if (bof.getType() == BOFRecord.TYPE_WORKBOOK)
                {
                    sheetIndex = -1;
                }
                else if (bof.getType() == BOFRecord.TYPE_WORKSHEET)
                {
                    sheetIndex++;
                    if (eSheet == null)
                    {
                        eSheet = new ESheet();
                        eSheet.setName(cSheetNames.get(sheetIndex));
                        eSheet.nextIndex();
                        if (needMatchSheetNames && !isMatchedSheet())
                        {
                            break;
                        }
                        iParseCell.startSheet(eSheet);
                        colFlag.clear();
                        hasEndLastRow = true;
                    }
                    else if (eSheet.isTheSame(cSheetNames.get(sheetIndex)))
                    {
                        // 不用处理
                    }
                    else
                    {
                        eSheet.setName(cSheetNames.get(sheetIndex));
                        eSheet.nextIndex();
                        if (needMatchSheetNames && !isMatchedSheet())
                        {
                            break;
                        }
                        iParseCell.startSheet(eSheet);
                        colFlag.clear();
                        hasEndLastRow = true;
                    }
                }
                break;
            case BoundSheetRecord.sid: // 新sheet到来
                BoundSheetRecord bsr = (BoundSheetRecord)record;
                String name = bsr.getSheetname();
                cSheetNames.add(name);
                break;
            case RowRecord.sid: // 新的行参数记录
                if (needMatchSheetNames && !isMatchedSheet())
                {
                    break;
                }
                RowRecord rowrec = (RowRecord)record;
                // 可获取列的ID
                // iParseCell.startRow(rowrec.getRowNumber());
                // lastRowId = rowrec.getLastCol();
                colFlag.put(rowrec.getRowNumber(), rowrec.getLastCol());
                break;
            case NumberRecord.sid: // 数字值
                if (needMatchSheetNames && !isMatchedSheet())
                {
                    break;
                }
                NumberRecord numrec = (NumberRecord)record;
                if (lastRowId != numrec.getRow())
                {
                    lastRowId = numrec.getRow();
                    // 如果上一行没有结束，发送结束信息
                    if (!hasEndLastRow)
                    {
                        iParseCell.endRow(numrec.getRow() - 1);
                    }
                    iParseCell.startRow(numrec.getRow());
                    hasEndLastRow = false;
                }
                ECell ec = new ECell(ECellType.XLS_NUMBER, Double.toString(numrec.getValue()));
                iParseCell.doCell(numrec.getRow(), numrec.getColumn(), ec);
                if (numrec.getColumn() == (colFlag.get(numrec.getRow()) - 1))
                {
                    iParseCell.endRow(numrec.getRow());
                    hasEndLastRow = true;
                }
                break;
            // SSTRecords store a array of unique strings used in Excel.
            case SSTRecord.sid: // 字符串表:一个workbook只有一个表
                // 缓存字符串表
                sstrec = (SSTRecord)record;
                break;
            case LabelSSTRecord.sid: // 字符串值
                if (needMatchSheetNames && !isMatchedSheet())
                {
                    break;
                }
                LabelSSTRecord lrec = (LabelSSTRecord)record;
                if (lastRowId != lrec.getRow())
                {
                    lastRowId = lrec.getRow();
                    // 如果上一行没有结束，发送结束信息
                    if (!hasEndLastRow)
                    {
                        iParseCell.endRow(lrec.getRow() - 1);
                    }
                    iParseCell.startRow(lrec.getRow());
                    hasEndLastRow = false;
                }
                ECell ecStr = new ECell(ECellType.XLS_STRING);
                ecStr.setValue(sstrec.getString(lrec.getSSTIndex()).getString());
                iParseCell.doCell(lrec.getRow(), lrec.getColumn(), ecStr);
                if (lrec.getColumn() == (colFlag.get(lrec.getRow()) - 1))
                {
                    iParseCell.endRow(lrec.getRow());
                    hasEndLastRow = true;
                }
                break;
        }
    }

}
