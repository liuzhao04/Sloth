package com.sloth.sink;

import java.util.LinkedList;

import com.sloth.configure.ConfigureHelp;
import com.sloth.exception.SlothException;
import com.sloth.msg.Message;

/**
 * 异步Sink<br>
 * 内部维护多个
 * 
 * @author liuzhao04
 * @version 1.0, 2017年2月23日
 */
public class AbsAsynSink implements ISink
{
    private LinkedList<Message> msgQueue = new LinkedList<Message>();

    private boolean hasCacheFile = false; // 是否带有缓存

    private int msgQueueLength = 1000; // 消息队列长度，当带有缓存(hasCacheFile==true)时此参数有效,默认1000

    private int maxThreadCount = 5; // 最大线程数

    private String cacheDir = ".cdir";

    @Override
    public void init(ConfigureHelp sHelp) throws SlothException
    {

    }

    @Override
    public void writeMessage(Message message) throws SlothException
    {

    }

    /**
     * 提交数据到<b>处理队列</b>中等候<br>
     * 提交规则：<br>
     * 1. 无缓存时，直接提交到队列，队列长度不限，直到内存溢出为止;<br>
     * 2. 有缓存时：<br>
     * 2.1 缓存有数据，提交到缓存;<br>
     * 2.2 缓存无数据时，提交到队列;<br>
     * 2.3 缓存无数据，且队列已满，提交到缓存;<br>
     *
     * @param message
     * @throws SlothException
     */
    private void submitMessage(Message message) throws SlothException
    {
        // TODO: 提交数据到队列
    }

    /**
     * 从<b>处理队列</b>提取数据<br>
     * 提取规则：<br>
     * 1. 无缓存，从队列提取;<br>
     * 2. 有缓存，缓存有数据，直接取;否则，从缓存中提取一批数据到队列，然后从队列中取;
     * 
     * @param message
     * @throws SlothException
     */
    private synchronized void requireMessage(Message message) throws SlothException
    {
        // TODO: 从队列提取数据
    }

    @Override
    public void destory()
    {

    }

}
