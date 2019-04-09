package com.gameloft9.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@Lazy(false)
public class ContextUtil implements ApplicationContextAware {
	private static ApplicationContext context;
	private static String serverName, runEnv;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		log.info("Initializing application context");
		ContextUtil.context = context;
	}

	@PreDestroy
	public void destroy(){
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static <T> T getBean(Class<T> clz) {
		return context.getBean(clz);
	}

	public static String getRunEnv() {
		if(runEnv != null)
			return runEnv;

		runEnv = System.getProperty("run_env");
		if (runEnv == null) {
			runEnv = "local";
		}
		return runEnv;
	}

	public static String getServerName() {
		if(serverName != null)
			return serverName;

		serverName = System.getProperty("SERVER_NAME");
		if (serverName == null) {
			serverName = "NO_NAME";
		}
		return serverName;
	}

}
