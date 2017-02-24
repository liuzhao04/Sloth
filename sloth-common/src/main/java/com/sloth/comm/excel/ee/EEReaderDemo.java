package com.sloth.comm.excel.ee;

public class EEReaderDemo implements IParseCell
{
    public static void main(String[] args)
    {
        try
        {
            EEReader eer = new EEReader("C:\\Users\\liuzhao04\\Desktop\\性能分析\\B120_t1xlwt.xlsx",
                                        new String[]{"汇总M", "组件下Handle"},
                                        new EEReaderDemo());
            eer.start();
            eer.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void startSheet(ESheet eSheet)
    {
        System.out.println(eSheet.getIndex() + "-" + eSheet.getName());
    }

    @Override
    public void startRow(int rowNum)
    {
        if (rowNum % 1000 == 1)
            System.out.println("row " + rowNum);
    }

    @Override
    public void doCell(int i, int j, ECell value)
    {
        if (i % 1000 == 1)
        {
            if (value.getType().equals(ECell.ECellType.XLS_NUMBER))
            {
                String val = value.getValue().replaceAll(".0$", "");
                value.setValue(val);
            }
            System.out.println("(" + i + "," + j + ") - " + value);
        }
    }

    @Override
    public void endRow(int rownId)
    {
        
    }
}
