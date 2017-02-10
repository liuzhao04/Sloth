package com.huawei.service.hutaf.idac.sloth;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.service.hutaf.idac.DacWebService;
import com.huawei.service.hutaf.idac.sloth.po.ftpinfo.ReqFtpInfo;
import com.huawei.service.hutaf.idac.sloth.po.ftpinfo.ResFtpInfo;
import com.huawei.service.hutaf.idac.sloth.po.task.ReqTask;
import com.huawei.service.hutaf.idac.sloth.po.task.ResTask;
import com.huawei.service.hutaf.idac.sloth.po.task.ResTasks;
import com.huawei.service.hutaf.idac.sloth.po.testsuit.ReqTestsuit;
import com.huawei.service.hutaf.idac.sloth.po.testsuit.ResTestsuit;
import com.sloth.comm.Common;
import com.sloth.comm.ftp.FtpDownloader;
import com.sloth.comm.http.HttpRequest;

/**
 * 提供ILog对接中，所有常用的基础接口
 *
 * @author lWX306898
 * @version 1.0, 2017年1月20日
 */
public class DacSlothService
{
    private DacWebService dSvc;

    private String dacIp;

    private final String PORT = "9095";

    private final String SERVICENAME = "downLoadTSLog";

    private FtpDownloader fdowner;

    public DacSlothService(String dacIp) throws Exception
    {
        dSvc = new DacWebService(dacIp);
        this.dacIp = dacIp;
        fdowner = FtpDownloader.getDownlaoder(dacIp, "root", "root");
    }

    /**
     * 获取昨天的所有任务
     *
     * @return
     * @throws Exception
     */
    public ResTasks getAllTaskOfYestoday() throws Exception
    {
        String yestoday = Common.getYestodayString();
        ReqTask reqTask = new ReqTask();
        reqTask.setStartTime(yestoday + " 00:00:00");
        reqTask.setEndTime(yestoday + " 23:59:59");
        String resValue = dSvc.getTaskInfoByTimeLimit(JSON.toJSONString(reqTask));
        ResTasks tasks = JSON.parseObject(resValue, ResTasks.class);
        return tasks;
    }

    /**
     * 获取时间区间内的所有任务
     *
     * @param reqTask
     * @return
     * @throws Exception
     */
    public ResTasks getAllTask(ReqTask reqTask) throws Exception
    {
        String resValue = dSvc.getTaskInfoByTimeLimit(JSON.toJSONString(reqTask));
        return JSON.parseObject(resValue, ResTasks.class);
    }

    /**
     * 测试套查询接口
     *
     * @param rTask
     * @throws Exception
     */
    public List<ResTestsuit> getTestsuits(ResTask rTask) throws Exception
    {
        ReqTestsuit rt = new ReqTestsuit();
        rt.setTaskId(rTask.getId());
        return getTestsuits(rt);
    }

    /**
     * 获取所有的测试套
     *
     * @param reqTestsuit
     * @throws Exception
     */
    public List<ResTestsuit> getTestsuits(ReqTestsuit reqTestsuit) throws Exception
    {
        String resString = dSvc.getTestsuitsByTaskId(JSON.toJSONString(reqTestsuit));
        JSONObject obj = JSON.parseObject(resString);
        JSONArray jArray = (JSONArray)obj.get(reqTestsuit.getTaskId());
        List<ResTestsuit> rs = new ArrayList<ResTestsuit>();
        for (int i = 0; i < jArray.size(); i++)
        {
            JSONObject jObj = (JSONObject)jArray.get(i);
            ResTestsuit resTsuit = JSON.toJavaObject(jObj, ResTestsuit.class);
            rs.add(resTsuit);
        }
        return rs;
    }

    /**
     * 请求FTP信息
     * 
     * @param reqFtpInfo
     * @return
     * @throws Exception
     */
    public List<ResFtpInfo> getFtpInfo(ReqFtpInfo reqFtpInfo) throws Exception
    {
        HttpRequest req = new HttpRequest(this.dacIp + ":" + PORT, SERVICENAME);
        String resString = req.sendPost("", reqFtpInfo.toJsonString(), "gbk");
        JSONObject obj = JSON.parseObject(resString);
        JSONArray jArray = (JSONArray)obj.get("value");
        List<ResFtpInfo> rs = new ArrayList<ResFtpInfo>();
        for (int i = 0; i < jArray.size(); i++)
        {
            JSONObject jObj = (JSONObject)jArray.get(i);
            ResFtpInfo resTsuit = JSON.toJavaObject(jObj, ResFtpInfo.class);
            rs.add(resTsuit);
        }
        return rs;
    }

    /**
     * 请求FTP信息：当测试套的个数大于分组个数时，进行分组发送请求
     * 
     * @param reqFtpInfo
     * @param groupSize 每个组多大
     * @return
     * @throws Exception
     */
    public List<ResFtpInfo> getFtpInfoByGroup(ReqFtpInfo reqFtpInfo, int groupSize) throws Exception
    {

        List<ResFtpInfo> rs = new ArrayList<ResFtpInfo>();
        for (ReqFtpInfo rfi : reqFtpInfo.group(groupSize))
        {
            rs.addAll(getFtpInfo(rfi));
        }
        return rs;
    }

    public void downloadFtpFile(String localRoot, ResFtpInfo resFtpInfo) throws Exception
    {
        String spath = resFtpInfo.getLogPath();
        if (spath.equals(""))
        {
            return;
        }
        String relPath = spath.split("//")[1];
        File tmp = new File(localRoot + "/" + relPath);
        fdowner.download(tmp.getParent() + "/" + resFtpInfo.getBlockId() + "/" + tmp.getName(), relPath, "utf-8");
    }

    public void destory()
    {
        dSvc = null;
        fdowner.close();
        FtpDownloader.destory();
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            long st = System.currentTimeMillis();
            DacSlothService drs = new DacSlothService("10.249.182.115");
            System.out.println("任务列表请求中...");
            // 1.请求任务列表
            ResTasks tasks = drs.getAllTaskOfYestoday();
            System.out.println("Task size = " + tasks.getTasks().size());
            int index = 0;
            int totalTsuit = 0;
            List<ResTestsuit> suits = new ArrayList<ResTestsuit>();
            List<ResFtpInfo> ftpinfos = new ArrayList<ResFtpInfo>();
            for (ResTask rt : tasks.getTasks())
            {
                index++;

                // 2.请求测试套
                List<ResTestsuit> tsuit = drs.getTestsuits(rt);
                totalTsuit += tsuit.size();
                suits.addAll(tsuit);
                System.out.println(index + " - " + tsuit.size());
                ReqFtpInfo rfi = new ReqFtpInfo();
                rfi.setTaskinfoid(rt.getId());

                // 3. 请求ftp信息
                List<String> ids = new ArrayList<String>();
                for (int i = 0; i < tsuit.size(); i++)
                {
                    ids.add(tsuit.get(i).getTsId());
                }
                rfi.setTestsuits(ids);
                List<ResFtpInfo> resFtpInfos = drs.getFtpInfoByGroup(rfi, 50);

//                for (ResFtpInfo info : resFtpInfos)
//                {
//                    drs.downloadFtpFile("down", info);
//                }
                ftpinfos.addAll(resFtpInfos);
            }
            System.out.println("请求耗时：" + (System.currentTimeMillis() - st) / 1000 + " 秒");
            long dst = System.currentTimeMillis();
            // 4. 下载ftp文件
            for (ResFtpInfo info : ftpinfos)
            {
                drs.downloadFtpFile("down", info);
            }
            System.out.println("下载耗时：" + (System.currentTimeMillis() - dst) / 1000 + " 秒");
            
            System.out.println("总共找到测试套：" + suits.size() + "/" + totalTsuit);
            System.out.println("耗时：" + (System.currentTimeMillis() - st) / 1000 + " 秒");
        }
        catch (RemoteException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
