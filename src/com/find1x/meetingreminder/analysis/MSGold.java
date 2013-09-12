package com.find1x.meetingreminder.analysis;

import java.util.*;

/* 	getTime()为获取时间的函数,返回一个date值
 * 	getLocation()获取位置，返回String
 * 	有2个构造函数 一个是以String为参数的 另一个无参数，通过setMsg参数为String 改变短信的值
 *	Warning：isMeeting 必须要在getTime和getLocation之后使用
 */

public class MSGold {
	String msg = "";

	public MSGold(String msg) {
		this.msg = msg;
	}

	MSGold() {
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isMeeting() {
		return isMeeting;
	}

	public Date getTime() {
		Date time = new Date();
		time.setSeconds(0);
		/*
		 * //今天 明天 后天 大后天 for(int i=0;i<msg.length();i++){ if(msg.charAt(i)=='今'
		 * && msg.charAt(i+1)=='天'){ //time.setDate(time.getDate()+1);
		 * isMeeting=true; break; } if(msg.charAt(i)=='明' &&
		 * msg.charAt(i+1)=='天'){ time.setDate(time.getDate()+1); break; }
		 * if(msg.charAt(i)=='后' && msg.charAt(i+1)=='天'){
		 * time.setDate(time.getDate()+2); break; } if(msg.charAt(i)=='大' &&
		 * msg.charAt(i+1)=='后' && msg.charAt(i+2)=='天'){
		 * time.setDate(time.getDate()+3); break; } }
		 */// 集成于timeFixed();

		// xxxx年/xx年 XX月XX日
		for (int i = 0; i < msg.length(); i++) {
			if (msg.charAt(i) == '年') {
				if ((msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')) {
					time.setYear(Integer.valueOf(msg.substring(i - 4, i)));
				} else if ((msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')) {
					time.setYear(Integer.valueOf(msg.substring(i - 2, i)));
				}
			}
			if (msg.charAt(i) == '月') {
				if ((msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')) {
					time.setMonth(Integer.valueOf(msg.substring(i - 2, i)) - 1);
				} else if (msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9') {
					time.setMonth(Integer.valueOf(msg.substring(i - 1, i)) - 1);
				}
			}
			if (msg.charAt(i) == '日' || msg.charAt(i) == '号') {
				if ((msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')) {
					time.setDate(Integer.valueOf(msg.substring(i - 2, i)));
				} else if (msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9') {
					time.setDate(Integer.valueOf(msg.substring(i - 1, i)));
				}
			}
		}
		// X：/点X
		for (int i = 0; i < msg.length(); i++) {
			if (msg.charAt(i) == ':' || msg.charAt(i) == '：'
					|| msg.charAt(i) == '点') {
				if ((msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9')
						&& (msg.charAt(i - 2) >= '0' && msg.charAt(i - 2) <= '9')) {
					time.setHours(Integer.valueOf(msg.substring(i - 2, i)));
					timeFixed(i, time);
				} else if (msg.charAt(i - 1) >= '0' && msg.charAt(i - 1) <= '9') {
					time.setHours(Integer.valueOf(msg.substring(i - 1, i)));
					// System.out.println(Integer.valueOf(msg.substring(i-1,i))+12);
					timeFixed(i, time);
				}
				if ((msg.charAt(i + 1) >= '0' && msg.charAt(i + 1) <= '9')
						&& (msg.charAt(i + 2) >= '0' && msg.charAt(i + 2) <= '9')) {
					time.setMinutes(Integer.valueOf(msg.substring(i + 1, i + 3)));
				} else
					time.setMinutes(0);
			}
		}
		/*
		 * //下午\上午X点X for(int i=0;i<msg.length();i++){ if((msg.charAt(i)=='上' &&
		 * msg.charAt(i+1)=='午') || (msg.charAt(i)=='下' &&
		 * msg.charAt(i+1)=='午')){ if((msg.charAt(i+2)>='0' &&
		 * msg.charAt(i+2)<='9')&&(msg.charAt(i+3)>='0' &&
		 * msg.charAt(i+3)<='9')){
		 * time.setHours(Integer.valueOf(msg.substring(i+2,i+4))); }else
		 * if(msg.charAt(i+2)>='0' && msg.charAt(i+2)<='9'){
		 * time.setHours(Integer.valueOf(msg.substring(i+2,i+3))); }
		 * if(msg.charAt(i+2)=='点'||msg.charAt(i+3)=='点'){
		 * if((msg.charAt(i+1)>='0' &&
		 * msg.charAt(i+1)<='9')&&(msg.charAt(i+2)>='0' &&
		 * msg.charAt(i+2)<='9')){
		 * time.setMinutes(Integer.valueOf(msg.substring(i+1,i+3))); }else{
		 * time.setMinutes(0); } } } }
		 */
		if (time.after(new Date())) {
			isMeeting = true;
		}
		return time;
	}

	public String getLocation() {
		String location = "";
		for (int i = 0; i < msg.length(); i++) {
			for (int j = 0; j < 2; j++) {
				if (msg.charAt(i) == LocationEasy[j]) {
					if ((msg.charAt(i + 1) >= '0' && msg.charAt(i + 1) <= '9')
							&& (msg.charAt(i + 2) >= '0' && msg.charAt(i + 2) <= '9')
							&& (msg.charAt(i + 3) >= '0' && msg.charAt(i + 3) <= '9')
							&& (msg.charAt(i + 4) >= '0' && msg.charAt(i + 4) <= '9')) {
						location = msg.substring(i, i + 5);
					}
				}
			}
		}
		for (int i = 0; i < LocationComplex.length; i++) {
			if (msg.indexOf(LocationComplex[i]) != -1) {
				location = LocationComplex[i];
			}
		}
		if (!location.equals("")) {
			isMeeting = true;
		}
		return location;
	}

	// 傍晚 晚上 午夜 半夜
	private void timeFixed(int n, Date time) {
		if (time.getHours() < 12) {
			for (int i = n - 1; i >= 0; i--) {
				if (msg.charAt(i) == '午' && msg.charAt(i - 1) == '下') {
					time.setHours(time.getHours() + 12);
					break;
				}
				if (msg.charAt(i) == '晚' && msg.charAt(i - 1) == '傍') {
					time.setHours(time.getHours() + 12);
					break;
				}
				if (msg.charAt(i) == '夜' && msg.charAt(i - 1) == '午') {
					time.setHours(time.getHours() + 12);
					break;
				}
				if (msg.charAt(i) == '夜' && msg.charAt(i - 1) == '半') {
					time.setHours(time.getHours() + 12);
					break;
				}
			}
		}

		for (int i = n - 1; i > 0; i--) {
			if (msg.charAt(i) == '晨' && msg.charAt(i - 1) == '凌') {
				time.setDate(time.getDate() + 1);
				break;
			}
			if (msg.charAt(i) == '晚' && msg.charAt(i - 1) == '明') {
				time.setDate(time.getDate() + 1);
				if (time.getHours() < 12) {
					time.setHours(time.getHours() + 12);
				}
				break;
			}
			if (msg.charAt(i - 1) == '今' && msg.charAt(i) == '天') {
				// time.setDate(time.getDate()+1);
				break;
			}
			if (msg.charAt(i - 1) == '明' && msg.charAt(i) == '天') {
				time.setDate(time.getDate() + 1);
				break;
			}
			if (msg.charAt(i - 1) == '后' && msg.charAt(i) == '天') {
				time.setDate(time.getDate() + 2);
				break;
			}
			if (i >= 2) {
				if (msg.charAt(i - 2) == '大' && msg.charAt(i - 1) == '后'
						&& msg.charAt(i) == '天') {
					time.setDate(time.getDate() + 3);
					break;
				}
			}
		}

	}

	private boolean isMeeting = false;
	private final char[] LocationEasy = { '北', '南' };
	private final String[] LocationComplex = { "知行楼", "至诚楼", "奋进楼", "睿思楼",
			"南体育馆", "南操场", "南图", "北图", "南图书馆", "北图书馆", "三食堂", "北食堂", "南食堂",
			"一食堂", "张南线终点站", "张南线", "品尝访", "大学生活动中心", "38幢401c", "大活",
			"辅导员办公室", "党员之家" };
}
