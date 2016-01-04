package com.monkeylluffy.recorder.tools;

import android.text.format.Time;

/**
 * 得到系统时间
 * @author yang_yueyue
 *
 */
public class GetSystemDateTime {
	
	/** *******************************************
	 * 得到系统时间 
	 */
	public static String now()
	  {
	    Time localTime = new Time();
	    localTime.setToNow();
	    return localTime.format("%Y%m%d%H%M%S");
	  }
	
	
	//将6000ms转换为0：06
	public static String timeString(long mills)
	  {
		int totals = (int) (mills/1000);
		int totalm = totals/60;
		int m = totals/60;
		int s = totals%60;
		String string = "";
		if (m <= 9) {
			string = string+"0"+m+" : ";
		}else {
			string = string+m+" : ";
		}
		if (s <= 9) {
			string = string+"0"+s;		
		}else {
			string = string+s;	
		}
		return string;
	  }
	
	
	
	
}
