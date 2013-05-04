package findix.meetingreminder.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.*;

import android.util.Log;
////////////////////////////////////////////////////
//String[] text为分词后的结果，对于地点的判断和选择 对其进行操作
//String Location为记录地点的字符串

import findix.meetingreminder.segmentation.SegmentationByHash;

public class GetUserTime {
	
	public GetUserTime(String msg) {
		long ftime = Calendar.getInstance().getTimeInMillis();
		Msg = msg;		
		//SegmentationByHash seg=new SegmentationByHash();
		//text = seg.getWords(msg);
		PhaseShiefTime();
		Log.i("运行时间", Calendar.getInstance().getTimeInMillis() - ftime + "");
	}

	public boolean isMeeting() {
		return isMeeting;
	}

	public void setMsg(String msg) {
		//SegmentationByHash seg=new SegmentationByHash();
		//text = seg.getWords(msg);
		PhaseShiefTime();
		PhaseShiefPlace();
	}

	public String[] getText() {// 用于测试使用
		return text;
	}

	public Date getTime() {// 返回日历对象
		return time.getTime();
		/*
		 * return time.get(Calendar.YEAR) + "年" + time.get(Calendar.MONTH) +
		 * "+1月" + time.get(Calendar.DATE) + "日" +
		 * time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE);
		 */
	}

	public String getLocation() {// 返回地点字符串
		
		return Location;
	}

	@SuppressWarnings("deprecation")
	private void PhaseShiefTime() {// 用于解析出字符串中的时间和地点信息
		time = Calendar.getInstance();// 获取系统当前时间
		time.setFirstDayOfWeek(Calendar.MONDAY);
		// 正则表达式确定
		Pattern Year = Pattern.compile("\\d{2,4}[年\\.\\/\\-]");// xx-xxxx年
		Pattern Month = Pattern.compile("\\d{1,2}[月\\.\\/\\-]");// x-xx月
		Pattern Day = Pattern.compile("\\d{1,2}[日号]");// x-xx日
		Pattern Week = Pattern.compile("(星期|礼拜|周)[一二三四五六日天1-7]");// 星期x
		Pattern NextWeek = Pattern.compile("下(星期|礼拜|周)[一二三四五六日天1-7]");// 下星期x
		Pattern TS = Pattern.compile("[AaPp]\\.?[Mm]\\.?");// am/pm
		Pattern Time = Pattern
				.compile("\\d{1,2}[：:点](\\d{1,2}|\\d{1,2}分|半|[123一二三]刻)?");// 精确时间
		// 明天 后天 大后天 晚上 分词后进行校正
		// 提取xxxx年xx月xx日/号
		Matcher MC = null;// 匹配器
		MC = Year.matcher(Msg);
		if (MC.find()) {
			if (MC.group().length() == 5)
				time.set(Calendar.YEAR,
						Integer.valueOf(MC.group().substring(0, 4)));
			else
				time.set(Calendar.YEAR, time.get(Calendar.YEAR) / 100 * 100
						+ Integer.valueOf(MC.group().substring(0, 2)));
		}// 提取年份
		MC = Month.matcher(Msg);
		if (MC.find()) {
			time.set(
					Calendar.MONTH,
					Integer.valueOf(MC.group().substring(0,
							MC.group().length() - 1)));
		}// 提取月份
		MC = Day.matcher(Msg);
		if (MC.find()) {
			time.set(
					Calendar.DATE,
					Integer.valueOf(MC.group().substring(0,
							MC.group().length() - 1)) - 1);
		}// 提取日期
		MC = NextWeek.matcher(Msg);
		if (MC.find()) {
			time.set(Calendar.WEEK_OF_YEAR, time.get(Calendar.WEEK_OF_YEAR) + 1);
			switch (MC.group().charAt(3)) {
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
		} else {
			MC = Week.matcher(Msg);
			if (MC.find()) {
				switch (MC.group().charAt(3)) {
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
			}
		}// 校正下星期\星期x
		/*
		 * MC = Time.matcher(Msg); if (MC.find()) { IK ik = new IK(MC.group());
		 * String[] IKTime = ik.Analyer(); // 确定小时 if
		 * (IKTime[0].charAt(IKTime[0].length() - 1) == '点') { if
		 * (IKTime[0].length() == 2) time.set(Calendar.HOUR,
		 * Integer.valueOf(IKTime[0].substring(0, 1))); else if
		 * (Integer.valueOf(IKTime[0].substring(0, 2)) <= 12)
		 * time.set(Calendar.HOUR, Integer.valueOf(IKTime[0].substring(0, 2)));
		 * else time.set(Calendar.HOUR_OF_DAY,
		 * Integer.valueOf(IKTime[0].substring(0, 2))); } else { if
		 * (IKTime[0].length() == 1) time.set(Calendar.HOUR,
		 * Integer.valueOf(IKTime[0].substring(0, 1))); else if
		 * (Integer.valueOf(IKTime[0].substring(0, 2)) < 12)
		 * time.set(Calendar.HOUR, Integer.valueOf(IKTime[0].substring(0, 2)));
		 * else time.set(Calendar.HOUR_OF_DAY,
		 * Integer.valueOf(IKTime[0].substring(0, 2))); } // 确定分钟
		 * if(IKTime.length==1)time.set(Calendar.MINUTE,0); else if
		 * (IKTime[1].charAt(IKTime[1].length() - 1) == '分') { if
		 * (IKTime[1].length() == 2) time.set(Calendar.MINUTE,
		 * Integer.valueOf(IKTime[1].substring(0, 1))); else
		 * time.set(Calendar.MINUTE, Integer.valueOf(IKTime[1].substring(0,
		 * 2))); } else if (IKTime[1].charAt(IKTime[1].length() - 1) == '刻') {
		 * switch (IKTime[1].charAt(0)) { case '一': case '1':
		 * time.set(Calendar.MINUTE, 15); break; case '二': case '2':
		 * time.set(Calendar.MINUTE, 30); break; case '三': case '3':
		 * time.set(Calendar.MINUTE, 45); break; } } else if (IKTime.length == 1
		 * && IKTime[1].charAt(0) == '半') { time.set(Calendar.MINUTE, 30); }
		 * else { time.set(Calendar.MINUTE, Integer.valueOf(IKTime[1])); } }
		 */
		// 校准时制
		MC = TS.matcher(Msg);
		if (MC.find()) {
			if (MC.group().charAt(0) == 'P' || MC.group().charAt(0) == 'p')
				time.set(Calendar.AM_PM, Calendar.PM);
			else
				time.set(Calendar.AM_PM, Calendar.AM);
		} else {
			if (time.get(Calendar.AM_PM) == Calendar.AM)
				TSFix();
		}
		// 校准日期
		DateFix();
	}

	private void PhaseShiefPlace() {
		// 对于地点选取的代码在这编写
	}

	private void TSFix() {
		for (int i = 0; i < text.length; i++) {
			if (text[i].compareTo("上午") == 0)
				time.set(Calendar.AM_PM, Calendar.AM);
			else if (text[i].compareTo("中午") == 0)
				time.set(Calendar.AM_PM, Calendar.AM);
			else if (text[i].compareTo("下午") == 0)
				time.set(Calendar.AM_PM, Calendar.PM);
			else if (text[i].compareTo("清晨") == 0)
				time.set(Calendar.AM_PM, Calendar.AM);
			else if (text[i].compareTo("早晨") == 0)
				time.set(Calendar.AM_PM, Calendar.AM);
			else if (text[i].compareTo("晚上") == 0)
				time.set(Calendar.AM_PM, Calendar.PM);
			else if (text[i].compareTo("傍晚") == 0)
				time.set(Calendar.AM_PM, Calendar.PM);
			else if (text[i].compareTo("半夜") == 0)
				time.set(Calendar.AM_PM, Calendar.PM);
			else if (text[i].compareTo("午夜") == 0)
				time.set(Calendar.AM_PM, Calendar.PM);
			else if (text[i].compareTo("凌晨") == 0) {// 特殊
				time.set(Calendar.AM_PM, Calendar.AM);
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 1);
			}
		}
	}

	private void DateFix() {
		for (int i = 0; i < text.length; i++) {
			if (text[i].compareTo("明天") == 0) {
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 1);
				break;
			}
			if (text[i].compareTo("后天") == 0) {
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 2);
				break;
			}
			if (text[i].compareTo("大后天") == 0) {
				time.set(Calendar.DAY_OF_YEAR,
						time.get(Calendar.DAY_OF_YEAR) + 3);
				break;
			}
		}
	}

	private String Msg;
	private String[] text;// 记录分词后的结果
	private Calendar time;// 记录时间
	private String Location;// 记录地点
	private boolean isMeeting = false;// 确定是否是会议
}
