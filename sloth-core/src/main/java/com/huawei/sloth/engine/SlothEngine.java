package com.huawei.sloth.engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huawei.sloth.comm.utils.PropertiesUtils;
import com.huawei.sloth.comm.utils.PropertiesUtils.LoadOrder;
import com.huawei.sloth.comm.utils.ReflectUtils;
import com.huawei.sloth.configure.ConfigureHelp;
import com.huawei.sloth.exception.ConfigLoadException;
import com.huawei.sloth.exception.SlothException;
import com.huawei.sloth.exception.init.InitSourceException;
import com.huawei.sloth.msg.Message;
import com.huawei.sloth.source.ISource;
import com.huawei.sloth.wflow.WorkFlow;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * 同步处理引擎
 *
 * @author lWX306898
 * @version 1.0, 2017年1月24日
 */
@SuppressWarnings("restriction")
public class SlothEngine
{
    private ISource<? extends Serializable> iSource; // 数据源

    private List<WorkFlow<? extends Serializable>> workFlows; // 工作流

    private int emptyTimes = 0; // Normal 状态连续没有取到数据的次数

    private final long NORMAL_SLEEP_MSECOND = 1; // Normal 状态 sleep 时间单位 ms

    private final long SLEEP_SLEEP_SECOND = 2 * 60; // Sleep 状态 sleep 时间单位 s

    private final long MAX_EMPTY_TIMES = SLEEP_SLEEP_SECOND * 1000 / NORMAL_SLEEP_MSECOND; // 最大为空次数（从时间维度看等于Sleep状态休眠的时间）

    private MainStatus status; // 引擎状态

    private final String CFG_PATH = "sloth.properties";

    private Map<String, String> cfgMap = null;

    private ConfigureHelp cfgHelp = null;

    public SlothEngine() throws SlothException
    {
        init();

        // 设置信号量状态处理
        QuitSingnalHandler handler = new QuitSingnalHandler();
        Signal.handle(new Signal("TERM"), handler);
        Signal.handle(new Signal("INT"), handler);

        // 设置进程退出程序
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                // 释放资源
                destory();
            }
        });
    }

    private void init() throws SlothException
    {
        // 1. 加载配置文件
        try
        {
            cfgMap = PropertiesUtils.mapProperties(CFG_PATH, LoadOrder.FIRST_PATH);
            cfgHelp = new ConfigureHelp(cfgMap, "sloth");
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(CFG_PATH, e);
        }

        // 2. 初始化数据源
        initSource();

        // 3. 初始化工作流
        initWorkFlows();
    }

    private void initWorkFlows() throws SlothException
    {
        String[] wfs = StringUtils.split(cfgHelp.getValue("wflows"));
        for (String wfCompName : wfs)
        {
            String key = cfgHelp.getCompName()+"."+wfCompName;
            ConfigureHelp ch = new ConfigureHelp(cfgMap, key);
            WorkFlow<String> wf = new WorkFlow<String>(ch);
            workFlows.add(wf);
        }
    }

    private void initSource() throws SlothException
    {
        String sCompName = cfgHelp.getCompName() + "." + cfgHelp.getValue("source");
        ConfigureHelp sourceCHelp = new ConfigureHelp(cfgMap, sCompName);
        try
        {
            iSource = ReflectUtils.createInstance(sourceCHelp.getValue("class"));
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            throw new InitSourceException("数据源实例创建失败", e);
        }

        iSource.init(sourceCHelp);
    }

    /**
     * 引擎启动
     *
     * @throws SlothException
     */
    public void start() throws SlothException
    {
        while (true)
        {
            switch (status)
            {
                case NOARMAL:
                    Message<?> msg = iSource.getMessage();
                    for (WorkFlow<? extends Serializable> wf : workFlows)
                    {
                        wf.handle(msg);
                    }
                    // 睡眠模式检查
                    checkSleep(msg.isEmpty() ? 0 : 1);
                    try
                    {
                        Thread.sleep(NORMAL_SLEEP_MSECOND);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case SLEEP:
                    if (iSource.hasNext())
                    {
                        status = MainStatus.NOARMAL;
                    }
                    try
                    {
                        Thread.sleep(SLEEP_SLEEP_SECOND * 1000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case QUIT:
                    break;
            }
            // 收到退出指令，退出循环
            if (status == MainStatus.QUIT)
            {
                break;
            }
        }
    }

    /**
     * 监控Normal状态时的状态，如果长期(2min)无数据则，进入SLEEP模式
     * 
     * @param size
     */
    private void checkSleep(int size)
    {
        if (size == 0)
        {
            emptyTimes++;
            if (emptyTimes == MAX_EMPTY_TIMES)
            {
                status = MainStatus.SLEEP;
            }
        }
        else
        {
            emptyTimes = 0;
        }
    }

    /**
     * 资源销毁
     */
    private void destory()
    {
        if (iSource != null)
        {
            iSource.destory();
            iSource = null;
        }
        if (workFlows != null)
        {
            for (WorkFlow<? extends Serializable> wf : workFlows)
            {
                if (wf != null)
                {
                    wf.destory();
                }
            }
            workFlows.clear();
            workFlows = null;
        }
    }

    /**
     * 主进程状态定义
     * 
     * @author lWX306898
     * @version 1.0, 2016年12月23日
     */
    private enum MainStatus
    {
     /**
      * 正常运行：每执行完一次导入之后，线程休息10s（防止无任务是cpu进行死循环占用资源）<br>
      * 切换SLEEP:当长期没有数据的时，需要切换到睡眠模式，防止过多占用cpu资源。2分钟没有数据时，切换到SLEEP模式
      */
        NOARMAL,

     /**
      * 休眠运行: 休眠期只进行数据监视，如果有数据，将状态调整为NORMAL后，开始导入。休眠期，线程休息2min
      */
        SLEEP,

     /**
      * 用户退出任务
      */
        QUIT
    }

    /**
     * 退出信号处理
     *
     * @author lWX306898
     * @version 1.0, 2016年12月23日
     */
    public class QuitSingnalHandler implements SignalHandler
    {
        @Override
        public void handle(Signal signal)
        {
            status = MainStatus.QUIT;
        }
    }

    public static void main(String[] args)
    {
        try
        {
            new SlothEngine();
        }
        catch (SlothException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
