package com.gameloft9.demo.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * job示例
 * Created by gameloft9 on 2019/4/8.
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class PrintJob implements InterruptableJob {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String countStr = context.getJobDetail().getJobDataMap().getString("count");
            long count = 0;
            if (countStr != null){
                count = Long.parseLong(countStr);
            }

            context.getJobDetail().getJobDataMap().put("count", "" + (count + 1));

            // 模拟任务执行
            Thread.sleep(1000);

            log.info("任务执行成功，累计执行次数:{}",count);
        } catch (Exception e) {
            log.error("", e);
        } finally {
        }
    }

    public void interrupt() throws UnableToInterruptJobException {
        // do nothing
    }

}
