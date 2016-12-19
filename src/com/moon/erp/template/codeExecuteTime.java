package com.moon.erp.template;

import java.util.Date;
import org.apache.log4j.Logger;
import com.moon.erp.common.DateUtil;

//计算代码执行时间
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
