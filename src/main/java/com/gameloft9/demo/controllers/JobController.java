package com.gameloft9.demo.controllers;

import com.gameloft9.demo.jobs.PrintJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 任务操作示例
 * Created by gameloft9 on 2017/11/27.
 */
@Slf4j
@Controller
public class JobController {

    @Autowired
    private SchedulerFactoryBean quartzScheduler;

    /**
     * 添加任务
     * */
    @RequestMapping(value = "/addJob.do", method = RequestMethod.POST)
    @ResponseBody
    public String add(Model model, HttpServletResponse response){
        String id = "PrintJob";
        String desc = "打印日志";
        String cron = "1/10 * * * * ?";// 从1开始每10秒执行一次


        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAsString("count",0);

        JobDetail jobDetail = JobBuilder.newJob(PrintJob.class)
                .storeDurably(true).withIdentity(id)
                .withDescription(desc).setJobData(jobDataMap).build();

        CronScheduleBuilder schBuilder = CronScheduleBuilder.cronSchedule(cron);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(id).withDescription(desc)
                .forJob(jobDetail).withSchedule(schBuilder).build();

        Date firstFileTime = null;
        try{
            quartzScheduler.getScheduler().addJob(jobDetail, false);
            firstFileTime = quartzScheduler.getScheduler().scheduleJob(trigger);
        }catch(SchedulerException e){
            log.info("新增任务失败：{}-{}", id,desc,e);
            return "fail";
        }

        log.info("新增任务成功：{}-{},第一次执行时间为:{}", id,desc,firstFileTime);
        return "success";
    }

    /**
     * 更新
     * */
    @RequestMapping(value = "/updateJob.do", method = RequestMethod.POST)
    @ResponseBody
    public String update(Model model, HttpServletResponse response){
        String id = "PrintJob";
        String desc = "打印日志";
        String cron = "1/20 * * * * ?";// 从1开始每10秒执行一次


        // 先停掉原来的trigger
        try{
            List<? extends Trigger> triggers = quartzScheduler.getScheduler()
                    .getTriggersOfJob(new JobKey(id));
            for (Trigger t : triggers) {
                quartzScheduler.getScheduler().unscheduleJob(t.getKey());
            }
        }catch(SchedulerException e){
            log.error("停掉原有trigger异常",e);
            return "fail";
        }

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAsString("count",0);

        JobDetail jobDetail = JobBuilder.newJob(PrintJob.class)
                .storeDurably(true).withIdentity(id)
                .withDescription(desc).setJobData(jobDataMap).build();

        CronScheduleBuilder schBuilder = CronScheduleBuilder.cronSchedule(cron);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(id).withDescription(desc)
                .forJob(jobDetail).withSchedule(schBuilder).build();

        Date firstFileTime = null;
        try{
            quartzScheduler.getScheduler().addJob(jobDetail, true);// 替换
            firstFileTime = quartzScheduler.getScheduler().scheduleJob(trigger);
        }catch(SchedulerException e){
            log.info("更新任务失败：{}-{}", id,desc,e);
            return "fail";
        }

        log.info("更新任务成功：{}-{},第一次执行时间为:{}", id,desc,firstFileTime);
        return "success";
    }

    /**
     * 立即执行任务
     * */
    @RequestMapping(value = "/runJobNow.do", method = RequestMethod.POST)
    @ResponseBody
    public String run(Model model){
        String id = "PrintJob";
        Trigger trigger = TriggerBuilder.newTrigger().forJob(id)
                .startNow().build();
        try{
            Date startDate = quartzScheduler.getScheduler().scheduleJob(trigger);
            log.info("任务启动成功：{},执行时间为:{}", id,startDate);
            return "success";
        }catch(SchedulerException e){
            log.error("启动失败",e);
            return "fail";
        }
    }

    /**
     * 停止任务计划
     * */
    @RequestMapping(value = "/pauseJob.do", method = RequestMethod.POST)
    @ResponseBody
    public String pause(Model model){
        String id = "PrintJob";
        try{
            quartzScheduler.getScheduler().pauseJob(new JobKey(id));
            log.info("停用任务成功：{}", id);
            return "success";
        }catch(SchedulerException e){
            log.error("停用任务失败",e);
            return "fail";
        }
    }

    /**
     * 恢复任务计划
     * */
    @RequestMapping(value = "/resumeJob.do", method = RequestMethod.POST)
    @ResponseBody
    public String resume(Model model){
        String id = "PrintJob";

        try{
            quartzScheduler.getScheduler().resumeJob(new JobKey(id));
            log.info("回复任务计划成功：{}", id);
            return "success";
        }catch(SchedulerException e){
            log.error("回复任务计划失败",e);
            return "fail";
        }
    }

    /**
     * 删除任务
     * */
    @RequestMapping(value = "/deleteJob.do", method = RequestMethod.POST)
    @ResponseBody
    public String delete(Model model){
        String id = "PrintJob";

        JobKey jobKey = new JobKey(id);
        try{
            List<? extends Trigger> triggers = quartzScheduler.getScheduler()
                    .getTriggersOfJob(jobKey);
            for (Trigger t : triggers) {
                quartzScheduler.getScheduler().unscheduleJob(t.getKey());
            }

            quartzScheduler.getScheduler().deleteJob(jobKey);
            log.info("删除任务成功:{}",jobKey.getName());
            return "success";
        }catch(SchedulerException e){
            log.error("删除任务异常",e);
            return "fail";
        }
    }
}
