package com.jatismobile.messagesapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLog {
	private static final Logger logger = LoggerFactory.getLogger(AppLog.class);
	
	public static void logInfo(String msg) {
		if (msg != null) {
			logger.info(msg);
			return;
		}
	}
	   

}
