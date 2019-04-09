package com.gameloft9.demo.jobs;

import com.gameloft9.demo.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 启动quartz
 * Created by gameloft9 on 2019/4/8.
 */
@Slf4j
public class QuartzStartup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        log.info("Startup quartz.");
        try {
            Scheduler scheduler = ContextUtil.getBean(Scheduler.class);
            scheduler.startDelayed(60);

        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        log.info("shutdown quartz.");
        try {
            Scheduler scheduler = ContextUtil.getBean(Scheduler.class);
            if(!scheduler.isShutdown()){
                scheduler.shutdown();
            }

        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

}
