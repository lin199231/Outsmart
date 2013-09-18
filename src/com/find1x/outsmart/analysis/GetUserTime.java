package com.find1x.outsmart.analysis;

import java.util.Calendar;
import java.util.regex.*;

import com.find1x.outsmart.segmentation.SegmentationByBloom;

////////////////////////////////////////////////////
//String[] text为分词后的结果，对于地点的判断和选择 对其进行操作
//String Location为记录地点的字符串


public class GetUserTime {

	public GetUserTime(String msg) {
		// long ftime = Calendar.getInstance().getTimeInMillis();
		NoDateMsg = msg;
		msg = toAllNumic(msg);
		Msg = msg;
		// SegmentationByHash seg=new SegmentationByHash();
		SegmentationByBloom seg = new SegmentationByBloom();
		text = seg.getWords(msg);
		PhaseShiefTime();
		// Log.i("运行时间", Calendar.getInstance().getTimeInMillis() - ftime + "");
	}

	public boolean isMeeting() {
		return isMeeting;
	}

	public void setMsg(String msg) {
		// SegmentationByHash seg=new SegmentationByHash();
		// text = seg.getWords(msg);
		PhaseShiefTime();
		PhaseShiefPlace();
	}

	public String[] getText() {// 用于测试使用
		return text;
	}

	public String getNoDateMsg() {
		dropAllDate();
		return NoDateMsg;
	}

	public Calendar getTime() {// 返回日历对象
		return time;
		/*
		 * return time.get(Calendar.YEAR) + "年" + time.get(Calendar.MONTH) +
		 * "+1月" + time.get(Calendar.DATE) + "日" +
		 * time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE);
		 */
	}

	/*
	 * public String getLocation() {// 返回地点字符串
	 * 
	 * return Location; }
	 */
	private void PhaseShiefTime() {// 用于解析出字符串中的时间和地点信息
		time = Calendar.getInstance();// 获取系统当前时间
		// time.set(Calendar.AM_PM, Calendar.AM);
		time.set(Calendar.SECOND, 0);
		time.setFirstDayOfWeek(Calendar.MONDAY);
		// 正则表达式确定
		boolean setWeek = false, setSureDate = false;
		int start;
		int year, month, day, hour, minute;
		year = time.get(Calendar.YEAR);
		month = time.get(Calendar.MONTH);
		day = time.get(Calendar.DATE);
		hour = time.get(Calendar.HOUR_OF_DAY);
		minute = time.get(Calendar.MINUTE);
		Pattern Date = Pattern
				.compile("((\\d{4}|\\d{2})[年.\\\\/-])?((\\d{1,2}[月.\\\\/-]\\d{1,2}[日号]?)|(\\d{1,2}[日号]))");
		// xx/xxxx年x-xx月x-xx日
		Pattern Year = Pattern.compile("(\\d{4}|\\d{2})[年.\\\\/-]");// xx-xxxx年
		Pattern Month = Pattern.compile("\\d{1,2}[月.\\\\/-]");// x-xx月
		Pattern Day = Pattern.compile("\\d{1,2}[日号]?");// x-xx日
		Pattern NextWeek = Pattern.compile("下(星期|礼拜|周)[一二三四五六日天1-7]");// 下星期x
		Pattern Week = Pattern.compile("(星期|礼拜|周)[一二三四五六日天1-7]");// 星期x
		Pattern TS = Pattern.compile("[AaPp].?[Mm].?");// am/pm
		Pattern Time = Pattern
				.compile("\\d{1,2}(([点时](半|[123一二三]刻|\\d{1,2}分|\\d{1,2}))|([：:]\\d{1,2})|[点 ])");// 精确时间
		// 明天 后天 大后天 晚上 分词后进行校正
		// 提取xxxx年xx月xx日/号
		Matcher MC = null;// 匹配器

		MC = Date.matcher(Msg);
		if (MC.find()) {
			start = MC.start();// 从匹配位置开始分解匹配
			MC = Year.matcher(Msg);
			if (MC.find(start)) {
				start = MC.end();// 从年份之后匹配月份
				if (MC.group().length() == 5)
					year = Integer.valueOf(MC.group().substring(0, 4));
				else
					year = Integer.valueOf(MC.group().substring(0, 2));
			}// 提取年份
			MC = Month.matcher(Msg);
			if (MC.find(start)) {
				start = MC.end();// 从月份之后匹配日期
				month = Integer.valueOf(MC.group().substring(0,
						MC.group().length() - 1)) - 1;
			}// 提取月份
			MC = Day.matcher(Msg);
			if (MC.find(start)) {
				if (MC.group().charAt(MC.group().length() - 1) == '日'
						|| MC.group().charAt(MC.group().length() - 1) == '号')
					day = Integer.valueOf(MC.group().substring(0,
							MC.group().length() - 1));
				else
					day = Integer.valueOf(MC.group().substring(0,
							MC.group().length()));
				setSureDate = true;
			}// 提取日期
		}
		// 提取下星期\星期x
		MC = NextWeek.matcher(Msg);
		if (MC.find()) {

			// year = time.get(Calendar.YEAR);
			// month = time.get(Calendar.MONTH);
			// day = time.get(Calendar.DATE);//超级疑问为什么这里不运行 时间就不能改为下星期
			switch (MC.group().charAt(MC.group().length() - 1)) {
			case '一':
			case '1':
				time.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				break;
			case '二':
			case '2':
				time.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
				break;
			case '三':
			case '3':
				time.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				break;
			case '四':
			case '4':
				time.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
				break;
			case '五':
			case '5':
				time.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				break;
			case '六':
			case '6':
				time.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				break;
			case '天':
			case '日':
			case '7':
				time.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				break;
			}
			setWeek = true;
			setSureDate = true;
			time.set(Calendar.WEEK_OF_YEAR, time.get(Calendar.WEEK_OF_YEAR) + 1);
			year = time.get(Calendar.YEAR);
			month = time.get(Calendar.MONTH);
			day = time.get(Calendar.DATE);
		} else {
			MC = Week.matcher(Msg);
			if (MC.find()) {
				switch (MC.group().charAt(MC.group().length() - 1)) {
				case '一':
				case '1':
					time.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					break;
				case '二':
				case '2':
					time.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
					break;
				case '三':
				case '3':
					time.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
					break;
				case '四':
				case '4':
					time.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
					break;
				case '五':
				case '5':
					time.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
					break;
				case '六':
				case '6':
					time.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
					break;
				case '天':
				case '日':
				case '7':
					time.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					break;
				}
				setWeek = true;
				setSureDate = true;
				year = time.get(Calendar.YEAR);
				month = time.get(Calendar.MONTH);
				day = time.get(Calendar.DATE);
			}
		}
		// 确定小时
		MC = Time.matcher(Msg);
		if (MC.find()) {
			String[] IKTime = toIKMode(MC.group());
			if (IKTime[0].charAt(IKTime[0].length() - 1) == '点'
					|| IKTime[0].charAt(IKTime[0].length() - 1) == '时') {
				if (IKTime[0].length() == 2)// x点只有2个字符 无法确定是否为24时制
					hour = Integer.valueOf(IKTime[0].substring(0, 1));
				else if (Integer.valueOf(IKTime[0].substring(0, 2)) < 12)
					hour = Integer.valueOf(IKTime[0].substring(0, 2));
				else {// 一定表示24时制
					hour = Integer.valueOf(IKTime[0].substring(0, 2));
					setAPM = 1;
				}

			} else {
				if (IKTime[0].length() == 1)
					hour = Integer.valueOf(IKTime[0].substring(0, 1));
				else if (Integer.valueOf(IKTime[0].substring(0, 2)) < 12)
					hour = Integer.valueOf(IKTime[0].substring(0, 2));
				else {// 一定表示24时制
					hour = Integer.valueOf(IKTime[0].substring(0, 2));
					setAPM = 1;
				}
			}
			// 确定分钟
			if (IKTime.length == 1)
				minute = 0;
			else if (IKTime[1].charAt(IKTime[1].length() - 1) == '分') {
				if (IKTime[1].length() == 2)
					minute = Integer.valueOf(IKTime[1].substring(0, 1));
				else
					minute = Integer.valueOf(IKTime[1].substring(0, 2));
			} else if (IKTime[1].charAt(IKTime[1].length() - 1) == '刻') {
				switch (IKTime[1].charAt(0)) {
				case '一':
				case '1':
					minute = 15;
					break;
				case '二':
				case '2':
					minute = 30;
					break;
				case '三':
				case '3':
					minute = 45;
					break;
				}
			} else if (IKTime.length == 2 && IKTime[1].charAt(0) == '半')
				minute = 30;
			else
				minute = Integer.valueOf(IKTime[1]);
		}
		// 处理年月日
		if (year > 1970) {
			time.set(Calendar.YEAR, year);
			if (month >= 0 && month <= 11) {
				time.set(Calendar.MONTH, month);
				if (day >= 1 && day <= getDayOfMonth(year, month))
					time.set(Calendar.DATE, day);
			}
		}

		// 如果不等于原时间且hour>12证明不需要改时制
		MC = TS.matcher(Msg);
		if (hour != time.get(Calendar.HOUR_OF_DAY) && setAPM == -1 && MC.find()) {
			// 校准时制
			if ((MC.group().charAt(MC.start() - 1) >= '0' && MC.group().charAt(
					MC.start() - 1) <= '9')
					&& MC.group().charAt(MC.end()) >= '0'
					&& MC.group().charAt(MC.end()) <= '9') {
				if (MC.group().charAt(0) == 'P' || MC.group().charAt(0) == 'p')
					time.set(Calendar.AM_PM, Calendar.PM);
				else
					time.set(Calendar.AM_PM, Calendar.AM);
			}
		}
		// 处理小时分钟
		if (hour >= 0 && hour <= 24) {
			time.set(Calendar.HOUR_OF_DAY, hour);
			if (minute >= 0 && minute <= 60)
				time.set(Calendar.MINUTE, minute);
		}
		time.get(Calendar.HOUR_OF_DAY);
		TSFix();// 早上等
		// 校准日期
		if (setSureDate == false)// 如果未定确定一个确切的日期则匹配明天等
			DateFix();// 明天等
		// 千年虫问题及判断时间是否被匹配
		Calendar timepp = Calendar.getInstance();
		// if (time.get(Calendar.YEAR) == timepp.get(Calendar.YEAR)
		// && time.get(Calendar.MONTH) == timepp.get(Calendar.MONTH)
		// && time.get(Calendar.DATE) == timepp.get(Calendar.DATE)
		// && time.get(Calendar.HOUR_OF_DAY) == timepp
		// .get(Calendar.HOUR_OF_DAY)
		// && time.get(Calendar.MINUTE) < timepp.get(Calendar.MINUTE)) {
		// time.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) + 1);
		// } else
		if (time.get(Calendar.YEAR) == timepp.get(Calendar.YEAR)
				&& time.get(Calendar.MONTH) == timepp.get(Calendar.MONTH)
				&& time.get(Calendar.DATE) == timepp.get(Calendar.DATE)
				&& time.get(Calendar.HOUR_OF_DAY) < timepp
						.get(Calendar.HOUR_OF_DAY)) {
			if (time.get(Calendar.AM_PM) == Calendar.AM && setAPM == -1) {
				// 短信中没有明确规定时间的上下午，上午的时间变为下午
				time.set(Calendar.AM_PM, Calendar.PM);
			} else if (time.get(Calendar.AM_PM) == Calendar.PM && setAPM == -1) {
				// 短信中没有明确规定时间的上下午，下午的时间变成明天上午
				time.set(Calendar.DATE, time.get(Calendar.DATE) + 1);
				time.set(Calendar.AM_PM, Calendar.AM);
			} else {
				// 用户确定时间，则时间改为明天的这个时间
				time.set(Calendar.DATE, time.get(Calendar.DATE) + 1);
			}
		} else if (time.get(Calendar.YEAR) == timepp.get(Calendar.YEAR)
				&& time.get(Calendar.MONTH) == timepp.get(Calendar.MONTH)
				&& time.get(Calendar.DATE) < timepp.get(Calendar.DATE)) {
			if (setWeek == false)
				time.set(Calendar.MONTH, time.get(Calendar.MONTH) + 1);
			else
				time.set(Calendar.WEEK_OF_YEAR,
						time.get(Calendar.WEEK_OF_YEAR) + 1);
		} else if (time.get(Calendar.YEAR) == timepp.get(Calendar.YEAR)
				&& time.get(Calendar.MONTH) < timepp.get(Calendar.MONTH)) {
			time.set(Calendar.YEAR, time.get(Calendar.YEAR) + 1);
		}
		// 日历时间未被修改则不认为是会议短信
		if (time.get(Calendar.YEAR) == timepp.get(Calendar.YEAR)
				&& time.get(Calendar.MONTH) == timepp.get(Calendar.MONTH)
				&& time.get(Calendar.DATE) == timepp.get(Calendar.DATE)
				&& time.get(Calendar.HOUR_OF_DAY) == timepp
						.get(Calendar.HOUR_OF_DAY)
				&& time.get(Calendar.MINUTE) == timepp.get(Calendar.MINUTE))
			isMeeting = false;
		else
			isMeeting = true;
	}

	private void PhaseShiefPlace() {
		// 对于地点选取的代码在这编写
	}

	private static String toAllNumic(String msg) {
		StringBuffer strb = new StringBuffer(msg);
		String temp;
		Pattern Zero = Pattern.compile("零[一二三四五六七八九]");// 零数
		Pattern Single = Pattern.compile("[一二三四五六七八九]");// 一位数
		Pattern Double = Pattern.compile("十[一二三四五六七八九]?");// 两位数
		Pattern Twenty = Pattern.compile("[一二三四五六七八九]十");// 二十这样的两位数
		Pattern Triple = Pattern.compile("[一二三四五六七八九]十[一二三四五六七八九]");// 三位数
		Matcher MC = null;// 匹配器
		MC = Triple.matcher(msg);
		while (MC.find()) {
			temp = MC.group();
			strb.replace(MC.start(), MC.start() + 3, toNumic(temp.charAt(0))
					+ "" + toNumic(temp.charAt(2)));
			msg = strb.toString();
			MC = Triple.matcher(msg);
		}
		MC = Twenty.matcher(msg);
		while (MC.find()) {
			temp = MC.group();
			strb.replace(MC.start(), MC.start() + 2, toNumic(temp.charAt(0))
					+ "0");
			msg = strb.toString();
		}
		MC = Double.matcher(msg);
		while (MC.find()) {
			temp = MC.group();
			if (temp.length() == 2)
				strb.replace(MC.start(), MC.start() + 2,
						"1" + toNumic(temp.charAt(1)));
			else
				strb.replace(MC.start(), MC.start() + 2, "10");
		}
		MC = Zero.matcher(msg);
		while (MC.find()) {
			temp = MC.group();
			strb.replace(MC.start(), MC.start() + 2,
					"0" + toNumic(temp.charAt(1)));
		}
		MC = Single.matcher(msg);
		while (MC.find()) {
			temp = MC.group();
			strb.replace(MC.start(), MC.start() + 1, toNumic(temp.charAt(0))
					+ "");
		}
		return msg = strb.toString();
	}

	private static char toNumic(char c) {
		switch (c) {
		case '一':
			return '1';
		case '二':
			return '2';
		case '三':
			return '3';
		case '四':
			return '4';
		case '五':
			return '5';
		case '六':
			return '6';
		case '七':
			return '7';
		case '八':
			return '8';
		case '九':
			return '9';
		default:
			return '0';
		}
	}

	private String[] toIKMode(String str) {
		for (int i = 1; i < str.length(); i++) {
			if (str.charAt(i) == ':' || str.charAt(i) == '：') {
				String[] IKMode = new String[2];
				IKMode[0] = str.substring(0, i);
				IKMode[1] = str.substring(i + 1, str.length());
				return IKMode;
			} else if (str.charAt(i) == '点' || str.charAt(i) == '时') {
				if (i + 1 == str.length()) {
					String[] IKMode = new String[1];
					IKMode[0] = str.substring(0, i + 1);
					return IKMode;
				} else {
					String[] IKMode = new String[2];
					IKMode[0] = str.substring(0, i + 1);
					IKMode[1] = str.substring(i + 1, str.length());
					return IKMode;
				}
			}
		}
		return null;
	}

	private void TSFix() {
		for (int i = 0; i < text.length; i++) {
			if (text[i].compareTo("上午") == 0 || text[i].compareTo("清晨") == 0
					|| text[i].compareTo("早晨") == 0
					|| text[i].compareTo("今天上午") == 0) {
				time.set(Calendar.AM_PM, Calendar.AM);
				setAPM = 0;
			} else if (text[i].compareTo("中午") == 0
					|| text[i].compareTo("下午") == 0
					|| text[i].compareTo("晚上") == 0
					|| text[i].compareTo("今晚") == 0
					|| text[i].compareTo("傍晚") == 0
					|| text[i].compareTo("半夜") == 0
					|| text[i].compareTo("午夜") == 0
					|| text[i].compareTo("今天中午") == 0
					|| text[i].compareTo("今天下午") == 0) {
				time.set(Calendar.AM_PM, Calendar.PM);
				setAPM = 1;
			} else if (text[i].compareTo("凌晨") == 0) {// 特殊
				time.set(Calendar.AM_PM, Calendar.AM);
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 1);
				setAPM = 0;
			} else if (text[i].compareTo("明晚") == 0) {// 特殊
				time.set(Calendar.AM_PM, Calendar.PM);
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 1);
				setAPM = 1;
			}
		}
	}

	private void DateFix() {
		for (int i = 0; i < text.length; i++) {
			if (text[i].compareTo("明天") == 0) {
				time.set(Calendar.DATE, time.get(Calendar.DATE) + 1);
				break;
			} else if (text[i].compareTo("后天") == 0) {
				time.set(Calendar.DATE, time.get(Calendar.DATE) + 2);
				break;
			} else if (text[i].compareTo("大后天") == 0) {
				time.set(Calendar.DATE, time.get(Calendar.DATE) + 3);
				break;
			}
		}
	}

	private void dropAllDate() {
		Pattern Year = Pattern.compile("\\d{2,4}[年\\.\\/\\-]");// xx-xxxx年
		Pattern Month = Pattern.compile("\\d{1,2}[月\\.\\/\\-]");// x-xx月
		Pattern Day = Pattern.compile("\\d{1,2}[日号]");// x-xx日
		Pattern Week = Pattern.compile("(星期|礼拜|周)[一二三四五六日天1-7]");// 星期x
		Pattern NextWeek = Pattern.compile("下(星期|礼拜|周)[一二三四五六日天1-7]");// 下星期x
		// Pattern TS = Pattern.compile("[AaPp]\\.?[Mm]\\.?");// am/pm
		Pattern Time = Pattern
				.compile("\\d{1,2}(([：:]\\d{1,2})|([点](\\d{1,2}分|半|[123一二三]刻)?))");// 精确时间
		Pattern APM = Pattern.compile("(上午|中午|下午|清晨|早晨|晚上|傍晚|半夜|午夜|凌晨)");// am/pm
		Pattern Date = Pattern.compile("(明天|后天|大后天)");
		deleteDateWord(Year);
		deleteDateWord(Month);
		deleteDateWord(Day);
		deleteDateWord(Week);
		deleteDateWord(NextWeek);
		// deleteDateWord(TS);
		deleteDateWord(Time);
		deleteDateWord(APM);
		deleteDateWord(Date);
	}

	private int getDayOfMonth(int year, int month) {
		switch (month) {
		case 0:
			return 31;
		case 1:
			if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
				return 29;
			else
				return 28;
		case 2:
			return 31;
		case 3:
			return 30;
		case 4:
			return 31;
		case 5:
			return 30;
		case 6:
			return 31;
		case 7:
			return 30;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 30;
		case 11:
			return 31;
		default:
			return 0;
		}
	}

	private void deleteDateWord(Pattern p) {
		Matcher MC = p.matcher(NoDateMsg);
		StringBuffer strb = new StringBuffer(NoDateMsg);
		while (MC.find()) {
			strb.replace(MC.start(), MC.end(), "");
			NoDateMsg = strb.toString();
			MC = p.matcher(NoDateMsg);
		}
	}

	private String Msg;
	private String NoDateMsg;
	private String[] text;// 记录分词后的结果
	private Calendar time;// 记录时间
	// private String Location;// 记录地点
	private int setAPM = -1;// 短信中是否有确定上下午-1表示未定 0表示早上 1表示下午
	private boolean isMeeting = false;// 确定是否是会议
}
