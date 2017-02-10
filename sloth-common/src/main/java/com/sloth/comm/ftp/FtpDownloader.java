package com.sloth.comm.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sloth.comm.unzip.UnzipUtils;

/**
 * ftp文件下载对象：缓存一组ftp连接，负责对单个文件进行下载， 如果是下载zip文件，下载后直接解压，并删除源文件
 * 
 * @author lWX306898
 * @version 1.0, 2016年5月19日
 */
public class FtpDownloader
{
    private FTPClient ftpClient; // ftp客户端对象

    private String serviceIp; // 服务器IP

    private String userName; // 用户名

    private String password; // 密码

    // 最大ftp client 缓存队列长度
    private static final int MAX_CLIENT_CACHE_SIZE = 5;

    private static List<FtpDownloader> instanceCache;

    private static Logger LOG = LoggerFactory.getLogger(FtpDownloader.class);

    // 记录最近一次使用的时间（1 - MAX_CLIENT_CACHE_SIZE），
    // 其中cacheUsedTimeRecords[i]=1表示缓存队列中第i个FtpDownloader是最久未调用的，
    // 反之MAX_CLIENT_CACHE_SIZE表示是最后一次调用的
    private static int[] cacheUsedTimeRecords;

    public FtpDownloader(String ip, String userName, String password) throws Exception
    {
        this.serviceIp = ip;
        this.userName = userName;
        this.password = password;
        initFtpClient();
    }

    public static FtpDownloader getDownlaoder(String ip, String userName, String password) throws Exception
    {
        synchronized (FtpDownloader.class)
        {
            // 缓存队列初始化
            if (instanceCache == null)
            {
                instanceCache = new ArrayList<FtpDownloader>();
                // 使用记录缓存
                cacheUsedTimeRecords = new int[MAX_CLIENT_CACHE_SIZE];
                for (int i = 0; i < cacheUsedTimeRecords.length; i++)
                {
                    cacheUsedTimeRecords[i] = 0;
                }
            }

            FtpDownloader downloader = getDownloaderCache(ip, userName, password);
            if (downloader == null)
            {
                downloader = new FtpDownloader(ip, userName, password);
                // 写入缓存队列
                pushDownloaderCache(downloader);
            }
            return downloader;
        }
    }

    /**
     * 将下载对象写入缓存队列
     *
     * @param downloader
     * @throws Exception
     */
    private static void pushDownloaderCache(FtpDownloader downloader) throws Exception
    {
        int listLength = instanceCache.size();
        // 1. 缓存未满，直接加入队列
        if (listLength < MAX_CLIENT_CACHE_SIZE)
        {
            // 1.1 在队列尾追加一个downloader
            instanceCache.add(downloader);
            cacheUsedTimeRecords[listLength] = MAX_CLIENT_CACHE_SIZE + 1;

            // 1.2 整体更新一遍cacheUsedTimeRecords
            for (int i = 0; i < listLength + 1; i++)
            {
                cacheUsedTimeRecords[i] -= 1;
            }
        }
        // 2. 缓存满了，淘汰一个最久未使用的FtpDownloader，然后加入队列
        else
        {
            // 2.1 找出最久未使用的FtpDownloader
            int indexToBeDeleted = -1;
            for (int i = 0; i < MAX_CLIENT_CACHE_SIZE; i++)
            {
                if (cacheUsedTimeRecords[i] == 1)
                {
                    indexToBeDeleted = i;
                    break;
                }
            }

            FtpDownloader downloaderToBeClose = null;
            // 2.2 淘汰indexToBeDeleted指向的FtpDownloader,加入新的缓存
            if (indexToBeDeleted != -1)
            {
                downloaderToBeClose = instanceCache.get(indexToBeDeleted);
                instanceCache.set(indexToBeDeleted, downloader);
                cacheUsedTimeRecords[indexToBeDeleted] = MAX_CLIENT_CACHE_SIZE + 1;
            }
            else
            {
                throw new Exception("无法找到需要淘汰的缓存，算法需调整");
            }

            // 2.3 整体更新一遍cacheUsedTimeRecords
            for (int i = 0; i < MAX_CLIENT_CACHE_SIZE; i++)
            {
                cacheUsedTimeRecords[i] -= 1;
            }

            // 2.4 关闭已淘汰的连接
            if (downloaderToBeClose != null)
            {
                downloaderToBeClose.close();
            }
        }
    }

    /**
     * 尝试从缓存中获取数据
     * 
     * @param ip
     * @param userName
     * @param password
     * @return
     */
    private static FtpDownloader getDownloaderCache(String ip, String userName, String password)
    {
        int index = 0;
        for (FtpDownloader downloader : instanceCache)
        {
            if (downloader.isTheSameService(ip, userName, password))
            {
                refreshCacheIndex(index);
                return downloader;
            }
            index++;
        }
        return null;
    }

    /**
     * 刷新缓存使用几率
     * 
     *
     * @param index
     */
    private static void refreshCacheIndex(int index)
    {
        int minIndexNeedToRefresh = cacheUsedTimeRecords[index];
        for (int i = 0; i < MAX_CLIENT_CACHE_SIZE; i++)
        {
            if (cacheUsedTimeRecords[i] > minIndexNeedToRefresh)
            {
                cacheUsedTimeRecords[i] -= 1;
            }
        }
        cacheUsedTimeRecords[index] = MAX_CLIENT_CACHE_SIZE;
    }

    /**
     * 判断用户请求的连接是否和当前连接相同
     *
     * @param ip
     * @param userName
     * @param password
     * @return
     */
    private boolean isTheSameService(String ip, String userName, String password)
    {
        return this.serviceIp.equals(ip) && this.userName.equals(userName) && this.password.equals(password);
    }

    /**
     * 初始化连接
     *
     * @throws Exception
     */
    private void initFtpClient() throws Exception
    {
        ftpClient = new FTPClient();
        // 初始化Ftp服务器的各种参数
        // 链接
        ftpClient.connect(serviceIp);
        // 登录服务
        if (!ftpClient.login(userName, password))
        {
            // 链接失败，退出
            ftpClient.logout();
            throw new Exception("ftp登录失败！");
        }
        // 设置Linux文件模式为二进制格式，windows下默认ascii
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (!os.startsWith("win") && !os.startsWith("Win"))
        {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
        // 链接响应
        final int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftpClient.disconnect();
            throw new Exception("ftp响应失败！");
        }
        // ftp进入模式
        ftpClient.enterLocalPassiveMode();
    }

    private void reConnect() throws Exception
    {
        System.out.println("重新连接ftp");
        close();
        initFtpClient();
    }

    /**
     * 连接是否有效
     * 
     * @param serverPath
     * @return
     */
    private boolean isAvailable()
    {
        try
        {
            ftpClient.sendCommand("cmd");
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /**
     * 文件下载
     *
     * @param savePath
     * @param serverPath
     * @param charset
     * @param deleteZip
     * @return
     * @throws Exception
     */
    public long download(String savePath, String serverPath, String charset) throws Exception
    {
        File localDownLoadfile = new File(savePath);
        File parentFileDir = localDownLoadfile.getParentFile();
        OutputStream OutputStream = null;
        // 文件路径不存在时创建
        if (!parentFileDir.exists())
        {
            parentFileDir.mkdirs();
        }
        OutputStream = new BufferedOutputStream(new FileOutputStream(localDownLoadfile));
        if (!isAvailable())
        {
            reConnect();
        }
        // 获取ftp文件的大小
        FTPFile ftpFile = ftpClient.mlistFile(serverPath);
        // 以'/'分割文件路径
        long ftpFileSize = ftpFile.getSize();
        // 下载
        boolean downLoad = ftpClient.retrieveFile(serverPath, OutputStream);
        if (null != OutputStream)
        {
            OutputStream.close();
        }
        if (downLoad)
        {
            boolean isZip = serverPath.endsWith(".zip");
            if (isZip)
            {
                // 解压
                UnzipUtils.unZip(new FileInputStream(savePath), parentFileDir.getAbsolutePath(), false, charset);
                // 删除压缩文件
                delFile(savePath);
            }
            return ftpFileSize;
        }
        else
        {
            LOG.info("文件下载失败  : " + serverPath);
            return -1;
        }
    }

    /**
     * 删除文件（zip）
     */
    private void delFile(String fileUrl)
    {
        // 获取文件
        File file = new File(fileUrl);
        // 是文件且文件存在
        if (file.isFile() && file.exists())
        {
            file.delete();
        }
    }

    public void close()
    {
        if (ftpClient == null)
        {
            return;
        }
        if (ftpClient.isConnected())
        {
            try
            {
                ftpClient.disconnect();
                ftpClient = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 销毁缓存队列，释放资源
     */
    public static void destory()
    {
        for (FtpDownloader downloader : instanceCache)
        {
            if (downloader != null)
            {
                downloader.close();
            }
        }
        instanceCache.clear();
        instanceCache = null;
    }

}
