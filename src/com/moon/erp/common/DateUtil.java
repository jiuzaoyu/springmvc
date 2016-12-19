package com.moon.erp.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateUtil {

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

	private static final Object object = new Object();

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param pattern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	private static SimpleDateFormat getDateFormat(String pattern)
			throws RuntimeException {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 获取日期中的某数值。如获取月份
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            日期格式
	 * @return 数值
	 */
	private static int getInteger(Date date, int dateType) {
		int num = 0;
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
			num = calendar.get(dateType);
		}
		return num;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期字符串
	 */
	private static String addInteger(String date, int dateType, int amount) {
		String dateString = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			myDate = addInteger(myDate, dateType, amount);
			dateString = DateToString(myDate, dateStyle);
		}
		return dateString;
	}

	/**
	 * 增加日期中某类型的某数值。如增加日期
	 * 
	 * @param date
	 *            日期
	 * @param dateType
	 *            类型
	 * @param amount
	 *            数值
	 * @return 计算后日期
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}
		return myDate;
	}

	/**
	 * 获取精确的日期
	 * 
	 * @param timestamps
	 *            时间long集合
	 * @return 日期
	 */
	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0;
		Map<Long, long[]> map = new HashMap<Long, long[]>();
		List<Long> absoluteValues = new ArrayList<Long>();

		if (timestamps != null && timestamps.size() > 0) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(timestamps.get(i)
								- timestamps.get(j));
						absoluteValues.add(absoluteValue);
						long[] timestampTmp = { timestamps.get(i),
								timestamps.get(j) };
						map.put(absoluteValue, timestampTmp);
					}
				}

				// 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的。此时minAbsoluteValue为0
				// 因此不能将minAbsoluteValue取默认值0
				long minAbsoluteValue = -1;
				if (!absoluteValues.isEmpty()) {
					minAbsoluteValue = absoluteValues.get(0);
					for (int i = 1; i < absoluteValues.size(); i++) {
						if (minAbsoluteValue > absoluteValues.get(i)) {
							minAbsoluteValue = absoluteValues.get(i);
						}
					}
				}

				if (minAbsoluteValue != -1) {
					long[] timestampsLastTmp = map.get(minAbsoluteValue);

					long dateOne = timestampsLastTmp[0];
					long dateTwo = timestampsLastTmp[1];
					if (absoluteValues.size() > 1) {
						timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne
								: dateTwo;
					}
				}
			} else {
				timestamp = timestamps.get(0);
			}
		}

		if (timestamp != 0) {
			date = new Date(timestamp);
		}
		return date;
	}

	/**
	 * 判断字符串是否为日期字符串
	 * 
	 * @param date
	 *            日期字符串
	 * @return true or false
	 */
	public static boolean isDate(String date) {
		boolean isDate = false;
		if (date != null) {
			if (getDateStyle(date) != null) {
				isDate = true;
			}
		}
		return isDate;
	}

	/**
	 * 获取日期字符串的日期风格。失敗返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期风格
	 */
	public static DateStyle getDateStyle(String date) {
		DateStyle dateStyle = null;
		Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
		List<Long> timestamps = new ArrayList<Long>();
		for (DateStyle style : DateStyle.values()) {
			if (style.isShowOnly()) {
				continue;
			}
			Date dateTmp = null;
			if (date != null) {
				try {
					ParsePosition pos = new ParsePosition(0);
					dateTmp = getDateFormat(style.getValue()).parse(date, pos);
					if (pos.getIndex() != date.length()) {
						dateTmp = null;
					}
				} catch (Exception e) {
				}
			}
			if (dateTmp != null) {
				timestamps.add(dateTmp.getTime());
				map.put(dateTmp.getTime(), style);
			}
		}
		Date accurateDate = getAccurateDate(timestamps);
		if (accurateDate != null) {
			dateStyle = map.get(accurateDate.getTime());
		}
		return dateStyle;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static Date StringToDate(String date) {
		DateStyle dateStyle = getDateStyle(date);
		return StringToDate(date, dateStyle);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return 日期
	 */
	public static Date StringToDate(String date, String pattern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateStyle
	 *            日期风格
	 * @return 日期
	 */
	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle != null) {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            日期格式
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, String pattern) {
		String dateString = null;
		if (date != null) {
			try {
				dateString = getDateFormat(pattern).format(date);
			} catch (Exception e) {
			}
		}
		return dateString;
	}

	/**
	 * 将日期转化为日期字符串。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dateStyle
	 *            日期风格
	 * @return 日期字符串
	 */
	public static String DateToString(Date date, DateStyle dateStyle) {
		String dateString = null;
		if (dateStyle != null) {
			dateString = DateToString(date, dateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String newPattern) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle newDateStyle) {
		DateStyle oldDateStyle = getDateStyle(date);
		return StringToString(date, oldDateStyle, newDateStyle);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newPattern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern,
			String newPattern) {
		return DateToString(StringToDate(date, olddPattern), newPattern);
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newParttern
	 *            新日期格式
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle,
			String newParttern) {
		String dateString = null;
		if (olddDteStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(),
					newParttern);
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddPattern
	 *            旧日期格式
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, String olddPattern,
			DateStyle newDateStyle) {
		String dateString = null;
		if (newDateStyle != null) {
			dateString = StringToString(date, olddPattern,
					newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 将日期字符串转化为另一日期字符串。失败返回null。
	 * 
	 * @param date
	 *            旧日期字符串
	 * @param olddDteStyle
	 *            旧日期风格
	 * @param newDateStyle
	 *            新日期风格
	 * @return 新日期字符串
	 */
	public static String StringToString(String date, DateStyle olddDteStyle,
			DateStyle newDateStyle) {
		String dateString = null;
		if (olddDteStyle != null && newDateStyle != null) {
			dateString = StringToString(date, olddDteStyle.getValue(),
					newDateStyle.getValue());
		}
		return dateString;
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期字符串
	 */
	public static String addYear(String date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的年份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param yearAmount
	 *            增加数量。可为负数
	 * @return 增加年份后的日期
	 */
	public static Date addYear(Date date, int yearAmount) {
		return addInteger(date, Calendar.YEAR, yearAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期字符串
	 */
	public static String addMonth(String date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的月份。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param monthAmount
	 *            增加数量。可为负数
	 * @return 增加月份后的日期
	 */
	public static Date addMonth(Date date, int monthAmount) {
		return addInteger(date, Calendar.MONTH, monthAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期字符串
	 */
	public static String addDay(String date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 增加日期的天数。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @param dayAmount
	 *            增加数量。可为负数
	 * @return 增加天数后的日期
	 */
	public static Date addDay(Date date, int dayAmount) {
		return addInteger(date, Calendar.DATE, dayAmount);
	}

	/**
	 * 
	* @Title: addHour 
	* @Description: 增加小时
	* @param date
	* @param dayAmount 正数加 负数减 
	* @return
	* Date
	 */
	public static Date addHour(Date date, int dayAmount) {
		return addInteger(date, Calendar.HOUR, dayAmount);
	}
	
	/**
	 * 
	* @Title: addMinute 
	* @Description: 添加分钟 
	* @param date
	* @param dayAmount 正数加 负数减 
	* @return
	* Date
	 */
	public static Date addMinute(Date date, int dayAmount) {
		return addInteger(date, Calendar.MINUTE, dayAmount);
	}
	
	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 年份
	 */
	public static int getYear(String date) {
		return getYear(StringToDate(date));
	}

	/**
	 * 获取日期的年份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 年份
	 */
	public static int getYear(Date date) {
		return getInteger(date, Calendar.YEAR);
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 月份
	 */
	public static int getMonth(String date) {
		return getMonth(StringToDate(date));
	}

	/**
	 * 获取日期的月份。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 月份
	 */
	public static int getMonth(Date date) {
		return getInteger(date, Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 天
	 */
	public static int getDay(String date) {
		return getDay(StringToDate(date));
	}

	/**
	 * 获取日期的天数。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 天
	 */
	public static int getDay(Date date) {
		return getInteger(date, Calendar.DATE);
	}
	
	/**
	 * 获取日期的小时数。失败返回0。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 天
	 */
	public static int getHour(String date) {
		return getDay(StringToDate(date));
	}
	
	/**
	 * 获取日期的小时数。失败返回0。
	 * 
	 * @param date
	 *            日期
	 * @return 天
	 */
	public static int getHour(Date date) {
		return getInteger(date, Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static String getDate(String date) {
		if(date==null){
			return null;
		}
		return StringToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期。默认yyyy-MM-dd格式。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 日期
	 */
	public static String getDate(Date date) {
		return DateToString(date, DateStyle.YYYY_MM_DD);
	}

	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 星期
	 */
	public static Week getWeek(String date) {
		Week week = null;
		DateStyle dateStyle = getDateStyle(date);
		if (dateStyle != null) {
			Date myDate = StringToDate(date, dateStyle);
			week = getWeek(myDate);
		}
		return week;
	}

	public static String getWeekZh(Date date){
		String result = "";
		//判断是否周末
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		switch(week){
			case 0:
				result = "星期日";
				break;
			case 1:
				result = "星期一";
				break;
			case 2:
				result = "星期二";
				break;
			case 3:
				result = "星期三";
				break;
			case 4:
				result = "星期四";
				break;
			case 5:
				result = "星期五";
				break;
			case 6:
				result = "星期六";
				break;
		}
		return result;
	}
	
	/**
	 * 获取日期的星期。失败返回null。
	 * 
	 * @param date
	 *            日期
	 * @return 星期
	 */
	public static Week getWeek(Date date) {
		Week week = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		switch (weekNumber) {
		case 0:
			week = Week.SUNDAY;
			break;
		case 1:
			week = Week.MONDAY;
			break;
		case 2:
			week = Week.TUESDAY;
			break;
		case 3:
			week = Week.WEDNESDAY;
			break;
		case 4:
			week = Week.THURSDAY;
			break;
		case 5:
			week = Week.FRIDAY;
			break;
		case 6:
			week = Week.SATURDAY;
			break;
		}
		return week;
	}

	/**
	 * 获取两个日期相差的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param otherDate
	 *            另一个日期字符串
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	/**
	 * @param date
	 *            日期
	 * @param otherDate
	 *            另一个日期
	 * @return 相差天数。如果失败则返回-1
	 */
	public static int getIntervalDays(Date date, Date otherDate) {
		int num = -1;
		Date dateTmp = DateUtil.StringToDate(DateUtil.getDate(date),
				DateStyle.YYYY_MM_DD);
		Date otherDateTmp = DateUtil.StringToDate(DateUtil.getDate(otherDate),
				DateStyle.YYYY_MM_DD);
		if (dateTmp != null && otherDateTmp != null) {
			long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
			num = (int) (time / (24 * 60 * 60 * 1000));
		}
		return num;
	}

	/**
	 * 
	* @Title: getQuarter 
	* @Description: 得到季度 
	* @param currenDate
	* @return
	* int
	 */
	public static int getQuarter(Date currentDate){
		int month = getMonth(currentDate);
		return getQuarter(month);
	}
	
	/**
	 * 
	* @Title: getQuarter 
	* @Description: 得到季度 
	* @param month
	* @return
	* int
	 */
	public static int getQuarter(int month){
		if(month >= 1 && month <=3){
			return 1;
		}
		else if(month >= 4 && month <= 6){
			return 2;
		}
		else if(month >= 7 && month <= 9){
			return 3;
		}
		else if(month >= 10 && month <= 12){
			return 4;
		}
		else{
			return 0;
		}
	}
	
	/**
	 * 
	* @Title: getDayNumbers 
	* @Description: 得到某年某月的总天数 
	* @param year
	* @param month
	* @return
	* int
	 */
	public static int getCountDayNumbers(int year,int month){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);//月份是从0开始
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public enum DateStyle {

		YYYY_MM("yyyy-MM", false), YYYY_MM_DD("yyyy-MM-dd", false), YYYY_MM_DD_HH_MM(
				"yyyy-MM-dd HH:mm", false), YYYY_MM_DD_HH_MM_SS(
				"yyyy-MM-dd HH:mm:ss", false),
				
		MM_DD_YYYY_HH_MM_SS("MM/dd/yyyy HH:mm:ss", false),

		YYYY_MM_EN("yyyy/MM", false), YYYY_MM_DD_EN("yyyy/MM/dd", false), YYYY_MM_DD_HH_MM_EN(
				"yyyy/MM/dd HH:mm", false), YYYY_MM_DD_HH_MM_SS_EN(
				"yyyy/MM/dd HH:mm:ss", false),

		YYYY_MM_CN("yyyy年MM月", false), YYYY_MM_DD_CN("yyyy年MM月dd日", false), YYYY_MM_DD_HH_MM_CN(
				"yyyy年MM月dd日 HH:mm", false), YYYY_MM_DD_HH_MM_SS_CN(
				"yyyy年MM月dd日 HH:mm:ss", false),

		HH_MM("HH:mm", true), HH_MM_SS("HH:mm:ss", true),

		MM_DD("MM-dd", true), MM_DD_HH_MM("MM-dd HH:mm", true), MM_DD_HH_MM_SS(
				"MM-dd HH:mm:ss", true),

		MM_DD_EN("MM/dd", true), MM_DD_HH_MM_EN("MM/dd HH:mm", true), MM_DD_HH_MM_SS_EN(
				"MM/dd HH:mm:ss", true),

		MM_DD_CN("MM月dd日", true), MM_DD_HH_MM_CN("MM月dd日 HH:mm", true), MM_DD_HH_MM_SS_CN(
				"MM月dd日 HH:mm:ss", true);

		private String value;

		private boolean isShowOnly;

		DateStyle(String value, boolean isShowOnly) {
			this.value = value;
			this.isShowOnly = isShowOnly;
		}

		public String getValue() {
			return value;
		}

		public boolean isShowOnly() {
			return isShowOnly;
		}
	}

	public enum Week {

		MONDAY("星期一", "Monday", "Mon.", 1), TUESDAY("星期二", "Tuesday", "Tues.",
				2), WEDNESDAY("星期三", "Wednesday", "Wed.", 3), THURSDAY("星期四",
				"Thursday", "Thur.", 4), FRIDAY("星期五", "Friday", "Fri.", 5), SATURDAY(
				"星期六", "Saturday", "Sat.", 6), SUNDAY("星期日", "Sunday", "Sun.",
				7);

		String name_cn;
		String name_en;
		String name_enShort;
		int number;

		Week(String name_cn, String name_en, String name_enShort, int number) {
			this.name_cn = name_cn;
			this.name_en = name_en;
			this.name_enShort = name_enShort;
			this.number = number;
		}

		public String getChineseName() {
			return name_cn;
		}

		public String getName() {
			return name_en;
		}

		public String getShortName() {
			return name_enShort;
		}

		public int getNumber() {
			return number;
		}
	}
	
	/**
	* <p>Title: isMixed</p> 
	* <p>Description: 两组时间段，是否有交集。一组时间的结束时间大于另一组的开始时间，且开始时间小于另一组的结束时间则有交集</p> 
	* @param firstStartTime
	* @param firstEndTime
	* @param secondStartTime
	* @param secondEndTime
	* @return
	 */
	public static boolean isMixed(Date firstStartTime,Date firstEndTime,Date secondStartTime,Date secondEndTime){		
		if((firstStartTime.before(secondEndTime) && firstEndTime.after(secondStartTime)) || (secondStartTime.before(firstEndTime) && secondEndTime.after(firstStartTime))){
			return true;
		}
		return false;
	}
	
	/** 
	 * 得到指定月的天数 
	 * */  
	public static int getMonthDayNum(int year, int month)  
	{  
	    Calendar a = Calendar.getInstance();  
	    a.set(Calendar.YEAR, year);  
	    a.set(Calendar.MONTH, month - 1);  
	    a.set(Calendar.DATE, 1);//把日期设置为当月第一天  
	    a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天  
	    int maxDate = a.get(Calendar.DATE);  
	    return maxDate;  
	}  
	/**
	 * 获取某个月的日期list
	 * @param year 年
	 * @param month 月
	 * @return
	 */
	public static List<Date> getMonthDayList(int year, int month){  
		List<Date> list=new ArrayList<Date>();
		int monthDayNum=getMonthDayNum(year, month);
		Calendar cal = Calendar.getInstance();  
		for(int i=1;i<=monthDayNum;i++){
			cal.set(Calendar.YEAR, year);  
			cal.set(Calendar.MONTH, month - 1);  
			cal.set(Calendar.DATE, i);//把日期设置为当月第一天
			list.add(cal.getTime());
		}
		return list;
	}
	/**
	 *  制定月内，指定日志之前的日期列表
	 * @param year 年
	 * @param month 月
	 * @param day 指定日志
	 * @return 如果今天是3号，返回结果是1和2
	 */
	public static List<Date> getMonthDayListBeforFay(int year, int month,int day){  
		List<Date> list=new ArrayList<Date>();
		int monthDayNum=getMonthDayNum(year, month);
		Calendar cal = Calendar.getInstance();  
		for(int i=1;i<day;i++){
			 cal.set(Calendar.YEAR, year);  
			 cal.set(Calendar.MONTH, month - 1);  
			 cal.set(Calendar.DATE, i);//把日期设置为当月第一天
			 list.add(cal.getTime());
		}
		return list;
	}
}
