package com.moon.erp;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.moon.erp.common.DateUtil;

public class codeExecuteTime {
	protected final Logger logger = Logger.getLogger(this.getClass());	
	
	public void codeExecuteTime() throws Exception {
		logger.info("**************执行操作开始******************开始时间："
				+ DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		long start = System.currentTimeMillis();
		logger.info("**************执行操作结束******************结束时间："
				+ DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "，用时："
				+ (System.currentTimeMillis() - start) / 1000 + "秒");
	}
	
	
	
}
