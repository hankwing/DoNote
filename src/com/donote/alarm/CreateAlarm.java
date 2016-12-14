package com.donote.alarm;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.donote.activity.DetectAlarmManager;
import com.donote.activity.MainActivity;
import com.donote.adapter.NoteDbAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;

public class CreateAlarm {
	private AlarmManager alarm_service;
	private long[] idList = new long[100];
	private int number = 0;
	// String 1:无缺省查询，group:1,3,4,8,14,15,18,20,依次为今天或XX或具体日期，年，月，日，下午或XX，时，分，事件
	private String detectString1 = "(((\\d{4}|\\d{2})[年/,，-]+((0?([1-9]))|(1[0-2]))[月./,，-]"
			+ "+((0?[1-9])|([12]([0-9]))|(3[0|1]))[日号,，/-]?)|明天|明日|后天|大后天|今天|今日|" +
			"星期一|星期二|星期三|星期四|星期五|星期六|星期日|星期天|周一|周二|周三|周四|周五|周六|周日|周天|下星期一|下星期二|" +
			"下星期三|下星期四|下星期五|下星期六|下星期天|下星期日|下周一|下周二|下周三|下周四|下周五|下周六|下周日|下周天)"
			+ "{1}\\s*(\\,|\\.|\\s|\\。|\\，)*(早晨|早上|上午|下午|中午|傍晚|晚上|夜晚)?((1|0?)[0-9]"
			+ "|2[0-3])[:：点时](([0-5][0-9])[分]?)?(\\,|\\.|\\s|\\。|\\，)*([\\u4e00-\\u9fa5]+)";
	// String 2:缺省“年”，group:1,3,7,13,14,17,19,依次为今天或XX或日期，月，日，下午或XX，时，分，事件
	private String detectString2 = "((((0?([1-9]))|(1[0-2]))[月./,，-]+((0?[1-9])|([12]([0-9]))"
			+ "|(3[0|1]))[日号,，/-]?)|明天|明日|今日|后天|大后天|今天|" +
			"星期一|星期二|星期三|星期四|星期五|星期六|星期日|星期天|周一|周二|周三|周四|周五|周六|周日|周天|下星期一|下星期二|" +
			"下星期三|下星期四|下星期五|下星期六|下星期天|下星期日|下周一|下周二|下周三|下周四|下周五|下周六|下周日|下周天)"
			+"{1}\\s*(\\,|\\.|\\s|\\。|\\，)*(早晨|早上|上午|下午|中午|傍晚|晚上)"
			+ "?((1|0?)[0-9]|2[0-3])[:：点时](([0-5][0-9])[分]?)?(\\,|\\.|\\s|\\。|\\，)*([\\u4e00-\\u9fa5]+)";
	// String 3:缺省“年，月”，group:1,3,9,10,13,15,依次为今天或者XX或具体日，日，下午或XX，时，分，事件
	private String detectString3 = "((((0?[1-9])|([12]([0-9]))|(3[0|1]))[日号]{1})|明天|明日|后天|大后天|今天|今日|"+
			"星期一|星期二|星期三|星期四|星期五|星期六|星期日|星期天|周一|周二|周三|周四|周五|周六|周日|周天|下星期一|下星期二|" +
			"下星期三|下星期四|下星期五|下星期六|下星期天|下星期日|下周一|下周二|下周三|下周四|下周五|下周六|下周日|下周天)"
			+ "{1}\\s*(\\,|\\.|\\s|\\。|\\，)*(早晨|早上|上午|下午|中午|傍晚|晚上|夜晚)?((1|0?)[0-9]|2[0-3])[:：点时](([0-5][0-9])"
			+ "[分]?)?(\\,|\\.|\\s|\\。|\\，)*([\\u4e00-\\u9fa5]+)";
	// String 4:缺省“年，月，日”，group:1,2,5,7,依次为下午或XX或无，时，分，事件
	private String detectString4 = "(早晨|早上|上午|下午|中午|傍晚|晚上|夜晚)?((1|0?)[0-9]|2[0-3])[:：点时]"
			+ "(([0-5][0-9])[分]?)?(\\,|\\.|\\s|\\。|\\，)*([\\u4e00-\\u9fa5]+)";
	// String 5:缺省“时，分”，group:1,4,6,10,16,18,依次为明天或XX或具体时间，年，月，日，上午或下午或没有，事件
	private String detectString5 = "((((\\d{4}|\\d{2})[年/,，-]+)?(((0?([1-9]))|(1[0-2]))[月./,，-]+)?" +
				"((0?[1-9])|([12]([0-9]))|(3[0|1]))[日号]{1})|明日|今日|明天|后天|大后天|今天|" +
				"星期一|星期二|星期三|星期四|星期五|星期六|星期日|星期天|周一|周二|周三|周四|周五|周六|周日|周天|下星期一|下星期二|" +
				"下星期三|下星期四|下星期五|下星期六|下星期天|下星期日|下周一|下周二|下周三|下周四|下周五|下周六|下周日|下周天)"
				+"{1}\\s*(\\,|\\.|\\s|\\。|\\，)" +
				"*(早晨|早上|上午|下午|中午|傍晚|晚上|夜晚)?(\\,|\\.|\\s|\\。|\\，)*([\\u4e00-\\u9fa5]+)";

	public void create(long noteID, String body, Context context) {

		alarm_service = (AlarmManager) context
				.getSystemService(android.content.Context.ALARM_SERVICE);
		Pattern alarmPattern = Pattern.compile(detectString1);
		Matcher alarmMatcher = alarmPattern.matcher(body);
		Calendar calendar;
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);

			if (dayString.equals("明天")||dayString.equals("明日"))
			{
				day += 1;
			} else if (dayString.equals("后天"))
			{
				day += 2;
			} else if (dayString.equals("大后天"))
			{
				day += 3;
			} else if(dayString.equals("下星期一")||dayString.equals("下周一")||dayString.equals("星期一")||dayString.equals("周一")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("星期二")||dayString.equals("周二")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期三")||dayString.equals("周三")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期四")||dayString.equals("周四")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期五")||dayString.equals("周五")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期六")||dayString.equals("周六")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期日")||dayString.equals("星期天")||dayString.equals("周天")||dayString.equals("周日")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("下星期二")||dayString.equals("下周二")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期三")||dayString.equals("下周三")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期四")||dayString.equals("下周四")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期五")||dayString.equals("下周五")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期六")||dayString.equals("下周六")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期日")||dayString.equals("下星期天")||dayString.equals("下周日")||dayString.equals("下周天")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("今天")||dayString.equals("今日")))
			{
				year = Integer.valueOf(alarmMatcher.group(3));
				month = Integer.valueOf(alarmMatcher.group(4))-1;
				day = Integer.valueOf(alarmMatcher.group(8));
			} 
			int hour = Integer.valueOf(alarmMatcher.group(15));
			String temp = alarmMatcher.group(14);
			if(temp != null) {
				if(temp.equals("下午")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("中午")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("傍晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("晚上")||temp.equals("夜晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
			}
			int minute = 0;
			if (alarmMatcher.group(18) != null)
			{
				minute = Integer.valueOf(alarmMatcher.group(18));
			}
			String catagoryString = alarmMatcher.group(20);
			Calendar anotherCalendar = Calendar.getInstance();
			anotherCalendar.set(year, month, day, hour, minute, 0);
			if(calendar.compareTo(anotherCalendar)==-1) {
				calendar.set(year, month, day, hour, minute, 0);
			}
			else {
				continue;
			}

			if (!MainActivity.mDbHelper.findAlarmByContent(noteID,
					catagoryString, calendar))
			{
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						noteID, catagoryString);
				idList[number] = alarmIdLong;
				number++;
				// MainActivity.mDbHelper.updateAlarmflag(noteID);
				setAlarm(noteID, calendar, context, catagoryString, alarmIdLong);
			}
		}
		body = body.replaceAll(detectString1, "");// 删除已检测过的字符串
		
		alarmPattern = Pattern.compile(detectString2);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			
			if (dayString.equals("明天")||dayString.equals("明日"))
			{
				day += 1;
			} else if (dayString.equals("后天"))
			{
				day += 2;
			} else if (dayString.equals("大后天"))
			{
				day += 3;
			}else if(dayString.equals("下星期一")||dayString.equals("下周一")||dayString.equals("星期一")||dayString.equals("周一")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("星期二")||dayString.equals("周二")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期三")||dayString.equals("周三")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期四")||dayString.equals("周四")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期五")||dayString.equals("周五")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期六")||dayString.equals("周六")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期日")||dayString.equals("星期天")||dayString.equals("周天")||dayString.equals("周日")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("下星期二")||dayString.equals("下周二")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期三")||dayString.equals("下周三")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期四")||dayString.equals("下周四")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期五")||dayString.equals("下周五")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期六")||dayString.equals("下周六")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期日")||dayString.equals("下星期天")||dayString.equals("下周日")||dayString.equals("下周天")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(!(dayString.equals("今天")||dayString.equals("今日")||dayString.equals("星期一")||dayString.equals("周一")))
			{
				month = Integer.valueOf(alarmMatcher.group(3))-1;
				day = Integer.valueOf(alarmMatcher.group(7));
			}
			int hour = Integer.valueOf(alarmMatcher.group(14));
			String temp = alarmMatcher.group(13);
			if(temp != null) {
				if(temp.equals("下午")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("中午")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("傍晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("晚上")||temp.equals("夜晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
			}
			int minute = 0;
			if (alarmMatcher.group(17) != null)
			{
				minute = Integer.valueOf(alarmMatcher.group(17));
			}
			String catagoryString = alarmMatcher.group(19);
			Calendar anotherCalendar = Calendar.getInstance();
			anotherCalendar.set(year, month, day, hour, minute, 0);
			if(calendar.compareTo(anotherCalendar)==-1) {
				calendar.set(year, month, day, hour, minute, 0);
			}
			else {
				continue;
			}
			if (!MainActivity.mDbHelper.findAlarmByContent(noteID,
					catagoryString, calendar))
			{
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						noteID, catagoryString);
				idList[number] = alarmIdLong;
				number++;
				// MainActivity.mDbHelper.updateAlarmflag(noteID);
				setAlarm(noteID, calendar, context, catagoryString, alarmIdLong);
			}
		}
		body = body.replaceAll(detectString2, "");// 删除已检测过的字符串
		
		alarmPattern = Pattern.compile(detectString3);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			if (dayString.equals("明天")||dayString.equals("明日"))
			{
				day += 1;
			} else if (dayString.equals("后天"))
			{
				day += 2;
			} else if (dayString.equals("大后天"))
			{
				day += 3;
			} else if(dayString.equals("下星期一")||dayString.equals("下周一")||dayString.equals("星期一")||dayString.equals("周一")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("星期二")||dayString.equals("周二")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期三")||dayString.equals("周三")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期四")||dayString.equals("周四")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期五")||dayString.equals("周五")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期六")||dayString.equals("周六")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期日")||dayString.equals("星期天")||dayString.equals("周天")||dayString.equals("周日")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("下星期二")||dayString.equals("下周二")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期三")||dayString.equals("下周三")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期四")||dayString.equals("下周四")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期五")||dayString.equals("下周五")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期六")||dayString.equals("下周六")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期日")||dayString.equals("下星期天")||dayString.equals("下周日")||dayString.equals("下周天")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("今天")||dayString.equals("今日")||dayString.equals("星期一")||dayString.equals("周一")))
			{
				day = Integer.valueOf(alarmMatcher.group(3));
			}
			int hour = Integer.valueOf(alarmMatcher.group(10));
			String temp = alarmMatcher.group(9);
			if(temp != null) {
				if(temp.equals("下午")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("中午")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("傍晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("晚上")||temp.equals("夜晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
			}
			int minute = 0;
			if (alarmMatcher.group(13) != null)
			{
				minute = Integer.valueOf(alarmMatcher.group(13));
			}
			String catagoryString = alarmMatcher.group(15);
			Calendar anotherCalendar = Calendar.getInstance();
			anotherCalendar.set(year, month, day, hour, minute, 0);
			if(calendar.compareTo(anotherCalendar)==-1) {
				calendar.set(year, month, day, hour, minute, 0);
			}
			else {
				continue;
			}
			if (!MainActivity.mDbHelper.findAlarmByContent(noteID,
					catagoryString, calendar))
			{
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						noteID, catagoryString);
				idList[number] = alarmIdLong;
				number++;
				// MainActivity.mDbHelper.updateAlarmflag(noteID);
				setAlarm(noteID, calendar, context, catagoryString, alarmIdLong);
			}
		}
		body = body.replaceAll(detectString3, "");// 删除已检测过的字符串
		alarmPattern = Pattern.compile(detectString4);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = Integer.valueOf(alarmMatcher.group(2));
			String temp = alarmMatcher.group(1);
			if(temp != null) {
				if(temp.equals("下午")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("中午")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("傍晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("晚上")||temp.equals("夜晚")) {
					if(hour < 12) {
						hour += 12;
					}
				}
			}
			int minute = 0;
			
			if (alarmMatcher.group(5) != null)
			{
				minute = Integer.valueOf(alarmMatcher.group(5));
			}
			String catagoryString = alarmMatcher.group(7);
			Calendar anotherCalendar = Calendar.getInstance();
			anotherCalendar.set(year, month, day, hour, minute, 0);
			if(calendar.compareTo(anotherCalendar)==-1) {
				calendar.set(year, month, day, hour, minute, 0);
			}
			else {
				continue;
			}
			if (!MainActivity.mDbHelper.findAlarmByContent(noteID,
					catagoryString, calendar))
			{		
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						noteID, catagoryString);
				idList[number] = alarmIdLong;
				number++;
				// MainActivity.mDbHelper.updateAlarmflag(noteID);
				setAlarm(noteID, calendar, context, catagoryString, alarmIdLong);
			}
		}
		body = body.replaceAll(detectString4, "");// 删除已检测过的字符串
		alarmPattern = Pattern.compile(detectString5);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			if (dayString.equals("明天")||dayString.equals("明日"))
			{
				day += 1;
			} else if (dayString.equals("后天"))
			{
				day += 2;
			} else if (dayString.equals("大后天"))
			{
				day += 3;
			} else if(dayString.equals("下星期一")||dayString.equals("下周一")||dayString.equals("星期一")||dayString.equals("周一")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("星期二")||dayString.equals("周二")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期三")||dayString.equals("周三")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期四")||dayString.equals("周四")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期五")||dayString.equals("周五")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期六")||dayString.equals("周六")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("星期日")||dayString.equals("星期天")||dayString.equals("周天")||dayString.equals("周日")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("下星期二")||dayString.equals("下周二")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期三")||dayString.equals("下周三")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期四")||dayString.equals("下周四")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期五")||dayString.equals("下周五")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期六")||dayString.equals("下周六")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("下星期日")||dayString.equals("下星期天")||dayString.equals("下周日")||dayString.equals("下周天")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("今天")||dayString.equals("今日")||dayString.equals("星期一")||dayString.equals("周一")))
			{
				if(alarmMatcher.group(4)!= null) {
					year = Integer.valueOf(alarmMatcher.group(4));
				}
				if(alarmMatcher.group(6)!= null) {
					month = Integer.valueOf(alarmMatcher.group(6))-1;
				}
				day = Integer.valueOf(alarmMatcher.group(10));
			}
			int hour = 12;
			String temp = alarmMatcher.group(16);
			if(temp != null) {
				if(temp.equals("上午")||temp.equals("早晨")||temp.equals("早上")) {
					hour = 8;
				}
				if(temp.equals("下午")) {
					hour = 14;
				}
				else if(temp.equals("中午")) {
					hour = 12;
				}
				else if(temp.equals("傍晚")) {
					hour = 16;
				}
				else if(temp.equals("晚上")||temp.equals("夜晚")) {
					hour = 20;
				}
			}
			int minute = 0;
			String catagoryString = alarmMatcher.group(18);
			Calendar anotherCalendar = Calendar.getInstance();
			anotherCalendar.set(year, month, day, hour, minute, 0);
			if(calendar.compareTo(anotherCalendar)==-1) {
				calendar.set(year, month, day, hour, minute, 0);
			}
			else {
				continue;
			}
			if (!MainActivity.mDbHelper.findAlarmByContent(noteID,
					catagoryString, calendar))
			{
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						noteID, catagoryString);
				idList[number] = alarmIdLong;
				number++;
				// MainActivity.mDbHelper.updateAlarmflag(noteID);
				setAlarm(noteID, calendar, context, catagoryString, alarmIdLong);
			}
		}
		body = body.replaceAll(detectString5, "");// 删除已检测过的字符串
		if (number > 0)
		{
			Intent i = new Intent(context, DetectAlarmManager.class);
			i.putExtra("NoteID", noteID);
			Bundle bundle = new Bundle();
			bundle.putLongArray("idlist", idList);
			i.putExtra("idlist", bundle);
			i.putExtra("number", number);
			((Activity) context).startActivityForResult(i, 1);
		} else
		{
			((Activity) context).finish();
		}

	}

	private void setAlarm(long noteID, Calendar calendar, Context context,
			String catagory, long alarmID) {
		Cursor cursor = MainActivity.mDbHelper.getnote(noteID);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("title", cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
		intent.putExtra("body", catagory);
		intent.putExtra("ID", noteID);
		intent.putExtra("alarmID", alarmID);
		PendingIntent p_intent = PendingIntent.getBroadcast(context,
				(int) alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm_service.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				p_intent);
		cursor.close();
	}
}
