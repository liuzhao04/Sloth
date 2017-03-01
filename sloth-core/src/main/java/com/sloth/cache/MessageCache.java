package com.sloth.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sloth.comm.utils.FileUtil;
import com.sloth.exception.SlothException;
import com.sloth.exception.cache.CacheReadException;
import com.sloth.exception.cache.CacheWriteException;
import com.sloth.exception.init.InitMessageCacheException;
import com.sloth.msg.Message;

/**
 * 消息缓存
 *
 * @author liuzhao04
 * @version 1.0, 2017年2月24日
 */
public class MessageCache
{
    private int blockSize; // 缓存块的大小

    private long timeout; // 超时时间：超时后，有没有满足一块大小的数据，都要将其写入缓存文件

    private String dir; // 缓存目录

    private List<Message> msgListForWrite; // 写缓存

    private long lastWriteAt = -1; // 最后一次写数据的时间

    private Logger logger = LoggerFactory.getLogger(MessageCache.class);

    private boolean isRunning = true;

    private long curFileIndex; // 下一个写入的index

    private long minFileIndex; // 下一个读取的index

    private String fileNameRegex = "asyn_msg_(\\d+)\\.cache"; // 缓存文件名格式

    private Pattern fileNamePattern = Pattern.compile(fileNameRegex); // 正则

    private String fileNameFormat = "asyn_msg_#index#.cache"; // 缓存文件名模板

    private Thread timeoutMonitor;

    private boolean hasDestoryed = false;

    public MessageCache(int blockSize, long timeout, String dir) throws SlothException
    {
        this.blockSize = blockSize;
        this.timeout = timeout;
        this.dir = dir;
        initCache();
        timeoutMonitor = new Thread(new TimeoutMonitor());
        Runtime.getRuntime().addShutdownHook(new Thread()
        {

            @Override
            public void run()
            {
                destory();
            }

        });
    }

    private void initCache() throws SlothException
    {
        File dir = new File(this.dir);
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new InitMessageCacheException("缓存目录创建失败:" + dir.getAbsolutePath());
        }

        File[] files = getCacheFiles(dir);
        // 无缓存文件
        if (ArrayUtils.isEmpty(files))
        {
            curFileIndex = 0; // 缓存文件序号从0开始
            minFileIndex = -1;
        }
        // 有缓存文件：1. 从缓存文件中读取一个文件(blockSize)的数据;2. 计算下一个缓存文件序号
        else
        {
            long[] fileIndexs = getMinAndMaxIndexOfCacheFile(files);
            curFileIndex = fileIndexs[1] + 1; // 已有最大文件序号 + 1
            minFileIndex = fileIndexs[0];
        }

        // 初始化写入缓存队列
        this.msgListForWrite = new ArrayList<Message>();
    }

    /**
     * 获取指定目录下的所有缓存文件
     *
     * @param dir
     * @return
     */
    private File[] getCacheFiles(File dir)
    {
        return dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                if (pathname.isFile() && pathname.getName().matches(fileNameRegex))
                {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 判断是否有缓存
     * 
     * @return
     */
    public boolean hasNext()
    {
        return minFileIndex != -1;
    }

    /**
     * 获取最小最大index
     * 
     * @param files
     * @return
     */
    private long[] getMinAndMaxIndexOfCacheFile(File[] files)
    {
        if (ArrayUtils.isEmpty(files))
        {
            return null;
        }
        long min = Long.MAX_VALUE;
        long max = -1;
        for (File cfile : files)
        {
            String fName = cfile.getName();
            Matcher m = fileNamePattern.matcher(fName);
            // 跳过不匹配的文件
            if (!m.find())
            {
                continue;
            }
            long cnum = Long.parseLong(m.group(1));
            if (min > cnum)
            {
                min = cnum;
            }
            if (max < cnum)
            {
                max = cnum;
            }
        }
        return new long[]{min, max};
    }

    /**
     * 写入数据
     *
     * @param msg
     * @throws SlothException
     */
    public void put(Message msg) throws SlothException
    {
        if (hasDestoryed)
        {
            throw new CacheWriteException("缓存对象已销毁");
        }

        // 不对空消息做任何处理
        if (null == msg || msg.isEmpty())
        {
            return;
        }

        // 第一次put时初始化提交时间
        if (-1 == lastWriteAt)
        {
            lastWriteAt = System.currentTimeMillis();
            // 启动监控
            timeoutMonitor.start();
        }

        msgListForWrite.add(msg);

        // 如果满了，写缓存
        if (msgListForWrite.size() == blockSize)
        {
            writeToCacheFile();
        }
    }

    /**
     * 获取一块消息
     *
     * @return
     * @throws SlothException
     */
    public List<Message> gets() throws SlothException
    {
        if (hasDestoryed)
        {
            throw new CacheReadException("缓存对象已销毁");
        }

        return readFromCacheFile();
    }

    /**
     * 下一个写入缓存文件名称
     *
     * @return
     */
    private File getNextFileNameForWrite()
    {
        while (true)
        {
            String name = this.dir + File.separator + fileNameFormat.replace("#index#", "" + curFileIndex);
            File file = new File(name);
            if (!file.exists())
            {
                return file;
            }
            curFileIndex++;// 寻找第一个不存在的文件
        }
    }

    /**
     * 下一个读取缓存文件名称
     *
     * @return
     */
    private File getNextFileNameForRead()
    {
        if (-1 == minFileIndex)
        {
            return null;
        }

        while (true)
        {
            String name = this.dir + File.separator + fileNameFormat.replace("#index#", "" + minFileIndex);
            File file = new File(name);
            if (file.exists())
            {
                return file;
            }
            minFileIndex++; // 寻找最小序号文件
        }
    }

    /**
     * 将缓存队列中的数据写入文件<br>
     * 可被destory(),timeoutMonitor，因此需要加同步锁
     * 
     * @throws SlothException
     */
    private synchronized void writeToCacheFile() throws SlothException
    {
        File file = getNextFileNameForWrite();
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean writeSuccess = false;
        try
        {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(msgListForWrite);
            oos.flush();
            writeSuccess = true;
        }
        catch (FileNotFoundException e)
        {
            throw new CacheWriteException("找不到缓存文件", e);
        }
        catch (IOException e)
        {
            throw new CacheWriteException("对象输出流或其他IO异常", e);
        }
        finally
        {
            if (null != oos)
            {
                try
                {
                    oos.close();
                }
                catch (IOException e)
                {
                    logger.info("对象输入流关闭失败", e);
                }
            }
            if (null != fos)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    logger.info("缓存文件输入流关闭失败", e);
                }
            }
            // 关闭流后才算写入成功
            if (writeSuccess)
            {
                msgListForWrite.clear();
                lastWriteAt = System.currentTimeMillis();
                // 缓存从无数据到有数据时，初始化minFileIndex
                if (minFileIndex == -1)
                {
                    minFileIndex = curFileIndex;
                }
                curFileIndex++; // 指向下一个将要写入的文件序号
            }
        }

    }

    /**
     * 从缓存中读取一块数据到缓存
     * 
     * @return
     *
     * @throws SlothException
     */
    @SuppressWarnings("unchecked")
    private List<Message> readFromCacheFile() throws SlothException
    {
        File file = getNextFileNameForRead();
        if (file == null)
        {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream bis = null;
        boolean readSuccess = false;
        try
        {
            fis = new FileInputStream(file);
            bis = new ObjectInputStream(fis);
            List<Message> msgList = (List<Message>)bis.readObject();
            readSuccess = true;
            return msgList;
        }
        catch (FileNotFoundException e)
        {
            throw new CacheReadException("找不到缓存文件", e);
        }
        catch (IOException e)
        {
            throw new CacheReadException("对象输入流或其他IO异常", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new CacheReadException("对象输入流转List<Message>对象失败", e);
        }
        finally
        {
            if (null != bis)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    logger.info("对象输入流关闭失败", e);
                }
            }
            if (null != fis)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    logger.info("缓存文件输入流关闭失败", e);
                }
            }

            // 读取成功：删除缓存文件
            if (readSuccess && !FileUtil.delete(file.getAbsolutePath()))
            {
                throw new CacheReadException("删除缓存文件失败:" + file.getAbsolutePath());
            }

            // 最小文件序号自增
            minFileIndex++;
            // 缓存文件被读完，从新开始记录缓存文件
            if (minFileIndex == curFileIndex)
            {
                minFileIndex = -1; // 无数据
                curFileIndex = 0; // 下一要写的文件
            }
        }
    }

    /**
     * 超时监控线程
     *
     * @author liuzhao04
     * @version 1.0, 2017年2月24日
     */
    private class TimeoutMonitor implements Runnable
    {

        @Override
        public void run()
        {
            while (isRunning)
            {

                long delta = System.currentTimeMillis() - lastWriteAt;
                if (lastWriteAt != -1 && delta >= timeout && msgListForWrite.size() > 0)
                {
                    try
                    {
                        writeToCacheFile();
                    }
                    catch (SlothException e)
                    {
                        logger.error("超时监控写入缓存失败", e);
                    }
                }
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    logger.info("超时监控器，时钟异常,监控退出", e);
                    break;
                }
            }
            logger.info("超时监控退出");
        }
    }

    /**
     * 对象销毁<br>
     * 可能和shutdown hook同时调用
     */
    public void destory()
    {
        if (hasDestoryed)
        {
            return;
        }
        // 可能被用户和shutdown hook同时调用
        synchronized (MessageCache.class)
        {
            isRunning = false;
            // 子线程监控退出标识
            try
            {
                // 让子线程先退出，再释放关键内存
                timeoutMonitor.join();
            }
            catch (InterruptedException e1)
            {
                logger.info("时钟中断：主线程等待超市监控退出", e1);
            }
            // 保存断点，重启后继续sink
            if (msgListForWrite.size() > 0)
            {
                try
                {
                    writeToCacheFile();
                }
                catch (SlothException e)
                {
                    logger.error("对象销毁时，断点保存失败", e);
                }
            }

            // 销毁其它对象
            msgListForWrite.clear();
            msgListForWrite = null;
            hasDestoryed = true;
        }
    }
}
