package com.sloth.comm.unzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 解压工具
 *
 * @author lWX306898
 * @version 1.0, 2016年12月20日
 */
public class UnzipUtils
{
    /**
     * 解压zip
     *
     * @param zipStream zip输入流
     * @param outDir 输出目录
     * @param isabs 是否为绝对路径
     * @param charset 编码
     */
    public static void unZip(InputStream zipStream, String outDir, boolean isabs, String charset) throws Exception
    {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(zipStream, Charset.forName(charset));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null)
        {
            if (ze.getName().equals("/"))
            {
                ze = zis.getNextEntry();
                continue;
            }
            File newFile = null;
            if (isabs)
            {
                newFile = new File(outDir);
            }
            else
            {
                String name = ze.getName();
                name = name.replace("\\", File.separator);
                newFile = new File(outDir + File.separator + name);
            }

            File folder = newFile.getParentFile();
            if (!folder.exists())
            {
                if (!folder.mkdirs())
                {
                    throw new IOException("目录创建失败:" + folder.getAbsolutePath());
                }
            }

            if (ze.isDirectory())
            {
                if (!newFile.exists() && !newFile.mkdir())
                {
                    throw new IOException("目录创建失败:" + folder.getAbsolutePath());
                }
            }
            else
            {
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    /**
     * 解压文件到当前目录下
     *
     * @param zipFile
     * @param charset
     * @param deleteZip 是否删除源文件
     * @throws Exception
     */
    public static void unZip(File zipFile, String charset, boolean deleteZip) throws Exception
    {
        FileInputStream fis = new FileInputStream(zipFile);
        try
        {
            String outDir = zipFile.getParent();
            unZip(fis, outDir, false, charset);
            if (deleteZip)
            {
                if (!zipFile.delete())
                {
                    throw new IOException("文件删除失败:" + zipFile.getAbsolutePath());
                }
            }
        }
        finally
        {
            fis.close();
        }
    }

    /**
     * 解压文件到当前目录下
     * 
     * @param zipFile
     * @param charset
     * @throws Exception
     */
    public static void unZip(File zipFile, String charset) throws Exception
    {
        unZip(zipFile, charset, false);
    }
}
