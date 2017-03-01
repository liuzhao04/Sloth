package com.sloth.comm.excel.ee;

import java.util.List;

/**
 * 设置标题栏
 *
 * @author lWX306898
 * @version 1.0, 2017年3月1日
 */
public class EERowReaderWithTitles extends EEReader
{
    public EERowReaderWithTitles(String path,
                                 String[] sheetNames,
                                 IParseRow iParseRow,
                                 List<TitleMapper> tmaps) throws Exception
    {
        super(path, sheetNames, new ParserAdapter(iParseRow, tmaps));
    }

    /**
     * 单元处理接口与行处理接口的适配器
     *
     * @author lWX306898
     * @version 1.0, 2017年3月1日
     */
    private static class ParserAdapter implements IParseCell
    {
        private IParseRow iParseRow = null;

        private List<TitleMapper> tmaps = null;

        private ERow row = null;

        private ESheet eSheet = null;

        private int rowId = -1;

        private TitleMapper cTM = null;

        private boolean isInitMapper = false;

        public ParserAdapter(IParseRow iParseRow, List<TitleMapper> tmaps)
        {
            this.iParseRow = iParseRow;
            if (tmaps == null || tmaps.size() == 0 || isInvalid(tmaps))
            {
                throw new ExceptionInInitializerError("未指定表头映射信息");
            }
            this.tmaps = tmaps;
        }

        /**
         * 判断表头是否无效
         *
         * @param tmaps
         * @return
         */
        private boolean isInvalid(List<TitleMapper> tmaps)
        {
            for (TitleMapper tm : tmaps)
            {
                if (tm.isInvalid())
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void startSheet(ESheet eSheet)
        {
            iParseRow.startSheet(eSheet);
            this.eSheet = eSheet;
        }

        @Override
        public void startRow(int rowId)
        {
            row = new ERow(rowId);
            this.rowId = rowId;
            TitleMapper tmp = getTitleMap();
            // 当前行是表头行
            if (tmp != null)
            {
                this.cTM = tmp;
                this.isInitMapper = true; // 即将要初始化表头映射
            }
        }

        @Override
        public void endRow(int rownId)
        {
            // 初始化表头只在一行中完成，一行读完后重置改标识
            if (this.isInitMapper)
            {
                this.isInitMapper = false;
            }
            // 标题行不用调用此接口
            else
            {
                iParseRow.doRow(row);
            }
        }

        @Override
        public void doCell(int i, int j, ECell value)
        {
            if (isInitMapper) // 读取表头
            {
                String cName = value.getValue();
                int index = this.cTM.geteTitles().indexOf(cName);
                // 找到，写入映射
                if (index > -1)
                {
                    this.cTM.getIndexs()[index] = j;
                }
            }
            // 读取数据
            else
            {
                List<String> mt = this.cTM.getmTitles();
                if (mt == null || mt.size() == 0)
                {
                    mt = this.cTM.geteTitles();
                }
                for (int tIndex = 0; tIndex < this.cTM.getIndexs().length; tIndex++)
                {
                    // 找到映射则取值，否则不用取值
                    if (this.cTM.getIndexs()[tIndex] == j)
                    {
                        String mName = mt.get(tIndex);
                        row.put(mName, value);
                        break;
                    }
                }
            }
        }

        /**
         * 获取当前表头映射
         *
         * @return
         */
        private TitleMapper getTitleMap()
        {
            for (TitleMapper tm : this.tmaps)
            {
                // 如果sheet匹配，则判断行是否匹配
                if (tm.getSheet().isTheSame(eSheet.getName()) || tm.getSheet().getIndex() == eSheet.getIndex())
                {
                    if (tm.getTitleRow() == rowId)
                    {
                        return tm;
                    }
                }
            }
            return null;
        }
    }
}
