package com.sloth.comm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

/**
 * 文件处理工具
 * 
 * @author lWX306898
 * @version 1.0, 2015-12-15
 */
public class FileUtil
{
    /***
     * 空文件标记
     */
    public static final byte[] EMPTY_FILE_TAG = "IAM_EMPTY_FILE".getBytes();

    /**
     * 获取BufferedReader对象
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static BufferedReader getReader(String path) throws FileNotFoundException
    {
        BufferedReader br = new BufferedReader(new FileReader(path));
        return br;
    }

    /**
     * 从文件中读取文本数据
     * 
     * @param path 文件路径
     * @return
     * @throws IOException
     */
    public static String readContent(String path) throws IOException
    {
        @SuppressWarnings("resource")
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuffer sb = new StringBuffer();
        String tmp = null;
        while ((tmp = br.readLine()) != null)
        {
            sb.append(tmp).append('\n');
        }
        return sb.toString();
    }

    /**
     * 将文本数据写入文件
     * 
     * @param content 待写入内容
     * @param path 文件路径
     * @throws IOException
     */
    public static void writeContent(String content, String path) throws IOException
    {
        File f = new File(path);
        File pf = f.getParentFile();
        if (!pf.exists() && !pf.mkdirs())
        {
            throw new IOException("无法创建指定的文件夹:" + f.getParent());
        }

        FileWriter fw = new FileWriter(f);

        try
        {
            fw.write(content);
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            fw.close();
        }
    }

    /**
     * 判断指定的路径下的文件是否存在
     * 
     * @param path
     * @return
     */
    public static boolean isFileExist(String path)
    {
        return new File(path).exists();
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean delete(String path)
    {
        File f = new File(path);
        if (f.exists())
        {
            return f.delete();
        }
        return false;
    }

    /**
     * 加载配置文件
     *
     * @param path
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static Properties loadProperties(String path) throws FileNotFoundException, IOException
    {
        Properties properties = new Properties();
        File proFile = new File(path);
        properties.load(new FileInputStream(proFile));
        return properties;
    }

    /**
     * 文件重命名，可以当做mv命令用
     *
     * @param path 原文件路径
     * @param newPath 目标文件路径
     * @throws IOException
     */
    public static void renameFile(String path, String newPath) throws IOException
    {
        File orgFile = new File(path);
        FileUtils.moveFile(orgFile, new File(newPath));
        if (orgFile.exists() && (!orgFile.delete()))
        {
            throw new IOException("重命名时源文件删除失败:" + path);
        }
    }
    
    /**
     * 拷贝一份文件
     *
     * @param path
     * @param dstDirectory
     * @throws IOException
     */
    public static void copyFile(String path, String dstDirectory) throws IOException
    {
        FileUtils.copyFileToDirectory(new File(path), new File(dstDirectory));
    }

    /**
     * 拷贝一份文件
     *
     * @param path
     * @param dstDirectory
     * @throws IOException
     */
    public static void copyFileSystem(String path, String dstDirectory) throws IOException
    {
        File file = new File(path);
        File dir = new File(dstDirectory);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        String dstfile = dstDirectory + file.getName();
        Runtime.getRuntime().exec("cp " + path + " " + dstfile);
    }

    /**
     * 递归获取所有文件
     *
     * @param path
     * @return
     */
    public static List<File> getAllFiles(String path)
    {
        List<File> rs = new ArrayList<File>();
        File root = new File(path);
        getFileInDir(rs, root);
        return rs;
    }

    /**
     * 递归遍历文件夹的所有文件
     *
     * @param rs
     * @param root
     */
    private static void getFileInDir(List<File> rs, File root)
    {
        File[] fs = root.listFiles();
        // 先根遍历
        List<File> directorys = new ArrayList<File>();
        // 遍历文件
        for (File f : fs)
        {
            if (f.isFile())
            {
                rs.add(f);
            }
            else
            {
                directorys.add(f);
            }
        }
        // 递归遍子文件夹
        for (File dir : directorys)
        {
            if (dir.isDirectory())
            {
                getFileInDir(rs, dir);
            }
        }
    }

    /**
     * 刪除某个目录下所有后缀为某个类型的文件
     *
     * @param dir
     * @param suffix
     */
    public static void deleteAllFileBySuffix(File dir, String suffix)
    {
        File[] fs = dir.listFiles();
        // 先根遍历
        List<File> directorys = new ArrayList<File>();
        // 遍历文件
        for (File f : fs)
        {
            if (f.isFile())
            {
                if (f.getName().toUpperCase().endsWith(suffix.toUpperCase()))
                {
                    if (!deleteFile(f))
                    {
                        System.out.println("删除失败:" + f.getAbsolutePath());
                    }
                    else
                    {
                        System.out.println("删除成功:" + f.getAbsolutePath());
                    }
                }
            }
            else
            {
                directorys.add(f);
            }
        }
        // 递归遍子文件夹
        for (File d : directorys)
        {
            if (dir.isDirectory())
            {
                deleteAllFileBySuffix(d, suffix);
            }
        }
    }

    /**
     * 文件删除
     * 
     * @param f
     * @return
     */
    public static boolean deleteFile(File f)
    {
        return f.delete();
    }

    /**
     * 生成一个空的采集文件
     * 
     * @param path 文件保存的位置
     */
    public static boolean createEmptySourceFile(String path)
    {
        FileOutputStream fos = null;
        try
        {
            File f = new File(path);
            if (!f.getParentFile().exists())
            {
                if (!f.getParentFile().mkdirs())
                {
                    return false;
                }
            }
            fos = new FileOutputStream(new File(path));
            fos.write(EMPTY_FILE_TAG);
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压zip文件
     * 
     * @param zipSrc zip文件
     * @param outDir 目标目录
     * @param isabs 是否为绝对路径
     */
    @SuppressWarnings("resource")
    public static void unZip(String zipSrc, String outDir, boolean isabs, String charset)
    {
        byte[] buffer = new byte[1024];
        try
        {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipSrc), Charset.forName(charset));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null)
            {
                if (ze.getName().equals("/"))
                {
                    ze = zis.getNextEntry();
                    // continue;
                }
                File newFile = null;
                if (isabs)
                {
                    newFile = new File(outDir);
                }
                else
                {
                    newFile = new File(outDir + File.separator + ze.getName());
                }
                File folder = newFile.getParentFile();
                if (!folder.exists())
                {
                    folder.mkdir();
                }

                if (ze.isDirectory())
                {
                    newFile.mkdir();
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
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
                try
                {
                    ze = zis.getNextEntry();
                }
                catch (Exception e)
                {
                    System.out.println("=====" + ze.getName());
                    e.printStackTrace();
                    throw e;
                }
            }
            zis.closeEntry();
            zis.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void unzip(String zipSrc, String outDir, String charset, Map<String, String> fileMap, boolean isWindows)
    {
        try
        {
            int buffer = 2048;
            ZipFile zip = new ZipFile(new File(zipSrc), Charset.forName(charset));
            String zipName = zipSrc.substring(zipSrc.lastIndexOf(File.separator)+1,zipSrc.length());
            String fileNames ="";
            File file = new File(outDir);
            if (!file.exists())
            {
                file.mkdirs();
            }
            BufferedOutputStream dest = null;
            BufferedInputStream is = null;
            ZipEntry zipEntry = null;
            Enumeration<?> e = zip.entries();
            while (e.hasMoreElements())
            {
                zipEntry = (ZipEntry)e.nextElement();
//                 System.out.println("Extracting:" + zipEntry);
                is = new BufferedInputStream(zip.getInputStream(zipEntry));
                int count;
                byte data[] = new byte[buffer];
                if (zipEntry.getName().equals("/"))
                {
                    continue;
                }
                FileOutputStream fos = null;
                if (zipEntry.getName().getBytes().length > 100)
                {
                    String name = zipEntry.getName();
                    Pattern p = Pattern.compile("(\\.\\w*|)$");
                    Matcher m = p.matcher(zipEntry.getName());
                    if (m.find())
                    {
                        name = FileUtil.getMD5(name, true) + m.group(1);
                    }
                    else
                    {
                        name = FileUtil.getMD5(name, true);
                    }
                    fos = new FileOutputStream(outDir + File.separator + name);
                }
                else
                {
                    fos = new FileOutputStream(outDir + File.separator + zipEntry.getName());
                }
                dest = new BufferedOutputStream(fos, buffer);
                while ((count = is.read(data, 0, buffer)) != -1)
                {
                    dest.write(data, 0, count);
                }                  
                dest.flush();
                dest.close();
                is.close();
                if(isWindows)
                {
                    fileNames += outDir + File.separator +zipEntry.getName() +"\n";
                }
                else
                {
                    fileNames += outDir + File.separator +zipEntry.getName() +"\r\n";
                }
            }
            fileMap.put(zipName, fileNames);
            zip.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 递归为目录下的符合筛选条件的所有文件做callback接口中指定的事
     *
     * @param dir 递归目录
     * @param fileFilter 过滤条件
     * @param callback 文件处理回调函数接口
     */
    public static void doSomethingForEveryFileInDirectory(File dir, FileFilter fileFilter, DoSomethingCallback callback)
    {
        File[] files = dir.listFiles(fileFilter);
        // 返回null时需要异常处理
        if (files == null)
        {
            return;
        }
        for (File file : files)
        {
            if (file.isFile())
            {
                callback.callback(file);
            }
            else
            {
                doSomethingForEveryFileInDirectory(file, fileFilter, callback);
            }
        }
    }

    /**
     * 获取md5
     * 
     *
     * @param s 字符串
     * @return
     */
    public static String getMD5(String s, boolean get16)
    {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try
        {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            if (get16)
            {
                return new String(str).substring(8, 24);
            }
            return new String(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件处理接口
     * 
     * @author lWX306898
     * @version 1.0, 2016年3月31日
     */
    public static interface DoSomethingCallback
    {
        /**
         * 对该文件做某件事
         * 
         * @param file
         */
        public void callback(File file);
    }
}
