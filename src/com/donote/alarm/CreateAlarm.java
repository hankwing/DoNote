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
	// String 1:��ȱʡ��ѯ��group:1,3,4,8,14,15,18,20,����Ϊ�����XX��������ڣ��꣬�£��գ������XX��ʱ���֣��¼�
	private String detectString1 = "(((\\d{4}|\\d{2})[��/,��-]+((0?([1-9]))|(1[0-2]))[��./,��-]"
			+ "+((0?[1-9])|([12]([0-9]))|(3[0|1]))[�պ�,��/-]?)|����|����|����|�����|����|����|" +
			"����һ|���ڶ�|������|������|������|������|������|������|��һ|�ܶ�|����|����|����|����|����|����|������һ|�����ڶ�|" +
			"��������|��������|��������|��������|��������|��������|����һ|���ܶ�|������|������|������|������|������|������)"
			+ "{1}\\s*(\\,|\\.|\\s|\\��|\\��)*(�糿|����|����|����|����|����|����|ҹ��)?((1|0?)[0-9]"
			+ "|2[0-3])[:����ʱ](([0-5][0-9])[��]?)?(\\,|\\.|\\s|\\��|\\��)*([\\u4e00-\\u9fa5]+)";
	// String 2:ȱʡ���ꡱ��group:1,3,7,13,14,17,19,����Ϊ�����XX�����ڣ��£��գ������XX��ʱ���֣��¼�
	private String detectString2 = "((((0?([1-9]))|(1[0-2]))[��./,��-]+((0?[1-9])|([12]([0-9]))"
			+ "|(3[0|1]))[�պ�,��/-]?)|����|����|����|����|�����|����|" +
			"����һ|���ڶ�|������|������|������|������|������|������|��һ|�ܶ�|����|����|����|����|����|����|������һ|�����ڶ�|" +
			"��������|��������|��������|��������|��������|��������|����һ|���ܶ�|������|������|������|������|������|������)"
			+"{1}\\s*(\\,|\\.|\\s|\\��|\\��)*(�糿|����|����|����|����|����|����)"
			+ "?((1|0?)[0-9]|2[0-3])[:����ʱ](([0-5][0-9])[��]?)?(\\,|\\.|\\s|\\��|\\��)*([\\u4e00-\\u9fa5]+)";
	// String 3:ȱʡ���꣬�¡���group:1,3,9,10,13,15,����Ϊ�������XX������գ��գ������XX��ʱ���֣��¼�
	private String detectString3 = "((((0?[1-9])|([12]([0-9]))|(3[0|1]))[�պ�]{1})|����|����|����|�����|����|����|"+
			"����һ|���ڶ�|������|������|������|������|������|������|��һ|�ܶ�|����|����|����|����|����|����|������һ|�����ڶ�|" +
			"��������|��������|��������|��������|��������|��������|����һ|���ܶ�|������|������|������|������|������|������)"
			+ "{1}\\s*(\\,|\\.|\\s|\\��|\\��)*(�糿|����|����|����|����|����|����|ҹ��)?((1|0?)[0-9]|2[0-3])[:����ʱ](([0-5][0-9])"
			+ "[��]?)?(\\,|\\.|\\s|\\��|\\��)*([\\u4e00-\\u9fa5]+)";
	// String 4:ȱʡ���꣬�£��ա���group:1,2,5,7,����Ϊ�����XX���ޣ�ʱ���֣��¼�
	private String detectString4 = "(�糿|����|����|����|����|����|����|ҹ��)?((1|0?)[0-9]|2[0-3])[:����ʱ]"
			+ "(([0-5][0-9])[��]?)?(\\,|\\.|\\s|\\��|\\��)*([\\u4e00-\\u9fa5]+)";
	// String 5:ȱʡ��ʱ���֡���group:1,4,6,10,16,18,����Ϊ�����XX�����ʱ�䣬�꣬�£��գ�����������û�У��¼�
	private String detectString5 = "((((\\d{4}|\\d{2})[��/,��-]+)?(((0?([1-9]))|(1[0-2]))[��./,��-]+)?" +
				"((0?[1-9])|([12]([0-9]))|(3[0|1]))[�պ�]{1})|����|����|����|����|�����|����|" +
				"����һ|���ڶ�|������|������|������|������|������|������|��һ|�ܶ�|����|����|����|����|����|����|������һ|�����ڶ�|" +
				"��������|��������|��������|��������|��������|��������|����һ|���ܶ�|������|������|������|������|������|������)"
				+"{1}\\s*(\\,|\\.|\\s|\\��|\\��)" +
				"*(�糿|����|����|����|����|����|����|ҹ��)?(\\,|\\.|\\s|\\��|\\��)*([\\u4e00-\\u9fa5]+)";

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

			if (dayString.equals("����")||dayString.equals("����"))
			{
				day += 1;
			} else if (dayString.equals("����"))
			{
				day += 2;
			} else if (dayString.equals("�����"))
			{
				day += 3;
			} else if(dayString.equals("������һ")||dayString.equals("����һ")||dayString.equals("����һ")||dayString.equals("��һ")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("���ڶ�")||dayString.equals("�ܶ�")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("������")||dayString.equals("����")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("�����ڶ�")||dayString.equals("���ܶ�")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("��������")||dayString.equals("������")||dayString.equals("������")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("����")||dayString.equals("����")))
			{
				year = Integer.valueOf(alarmMatcher.group(3));
				month = Integer.valueOf(alarmMatcher.group(4))-1;
				day = Integer.valueOf(alarmMatcher.group(8));
			} 
			int hour = Integer.valueOf(alarmMatcher.group(15));
			String temp = alarmMatcher.group(14);
			if(temp != null) {
				if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")||temp.equals("ҹ��")) {
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
		body = body.replaceAll(detectString1, "");// ɾ���Ѽ������ַ���
		
		alarmPattern = Pattern.compile(detectString2);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			
			if (dayString.equals("����")||dayString.equals("����"))
			{
				day += 1;
			} else if (dayString.equals("����"))
			{
				day += 2;
			} else if (dayString.equals("�����"))
			{
				day += 3;
			}else if(dayString.equals("������һ")||dayString.equals("����һ")||dayString.equals("����һ")||dayString.equals("��һ")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("���ڶ�")||dayString.equals("�ܶ�")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("������")||dayString.equals("����")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("�����ڶ�")||dayString.equals("���ܶ�")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("��������")||dayString.equals("������")||dayString.equals("������")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(!(dayString.equals("����")||dayString.equals("����")||dayString.equals("����һ")||dayString.equals("��һ")))
			{
				month = Integer.valueOf(alarmMatcher.group(3))-1;
				day = Integer.valueOf(alarmMatcher.group(7));
			}
			int hour = Integer.valueOf(alarmMatcher.group(14));
			String temp = alarmMatcher.group(13);
			if(temp != null) {
				if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")||temp.equals("ҹ��")) {
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
		body = body.replaceAll(detectString2, "");// ɾ���Ѽ������ַ���
		
		alarmPattern = Pattern.compile(detectString3);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			if (dayString.equals("����")||dayString.equals("����"))
			{
				day += 1;
			} else if (dayString.equals("����"))
			{
				day += 2;
			} else if (dayString.equals("�����"))
			{
				day += 3;
			} else if(dayString.equals("������һ")||dayString.equals("����һ")||dayString.equals("����һ")||dayString.equals("��һ")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("���ڶ�")||dayString.equals("�ܶ�")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("������")||dayString.equals("����")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("�����ڶ�")||dayString.equals("���ܶ�")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("��������")||dayString.equals("������")||dayString.equals("������")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("����")||dayString.equals("����")||dayString.equals("����һ")||dayString.equals("��һ")))
			{
				day = Integer.valueOf(alarmMatcher.group(3));
			}
			int hour = Integer.valueOf(alarmMatcher.group(10));
			String temp = alarmMatcher.group(9);
			if(temp != null) {
				if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")||temp.equals("ҹ��")) {
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
		body = body.replaceAll(detectString3, "");// ɾ���Ѽ������ַ���
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
				if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 8) {
						hour += 12;
					}
				}
				else if(temp.equals("����")) {
					if(hour < 12) {
						hour += 12;
					}
				}
				else if(temp.equals("����")||temp.equals("ҹ��")) {
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
		body = body.replaceAll(detectString4, "");// ɾ���Ѽ������ַ���
		alarmPattern = Pattern.compile(detectString5);
		alarmMatcher = alarmPattern.matcher(body);
		while (alarmMatcher.find())
		{
			calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String dayString = alarmMatcher.group(1);
			if (dayString.equals("����")||dayString.equals("����"))
			{
				day += 1;
			} else if (dayString.equals("����"))
			{
				day += 2;
			} else if (dayString.equals("�����"))
			{
				day += 3;
			} else if(dayString.equals("������һ")||dayString.equals("����һ")||dayString.equals("����һ")||dayString.equals("��һ")) {
				day += (2+7-calendar.get(Calendar.DAY_OF_WEEK));
			} else if(dayString.equals("���ڶ�")||dayString.equals("�ܶ�")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 3 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (3-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 4 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (4-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 5 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (5-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 6 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (6-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) < 7 && calendar.get(Calendar.DAY_OF_WEEK)!= 1) {
					day += (7-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("������")||dayString.equals("������")||dayString.equals("����")||dayString.equals("����")) {
				if( calendar.get(Calendar.DAY_OF_WEEK) >1) {
					day += (8-calendar.get(Calendar.DAY_OF_WEEK));
				}
			}else if(dayString.equals("�����ڶ�")||dayString.equals("���ܶ�")) {
				day += (3+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (4+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (5+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (6+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("������")) {
				day += (7+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(dayString.equals("��������")||dayString.equals("��������")||dayString.equals("������")||dayString.equals("������")) {
				day += (8+7-calendar.get(Calendar.DAY_OF_WEEK));
			}else if(!(dayString.equals("����")||dayString.equals("����")||dayString.equals("����һ")||dayString.equals("��һ")))
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
				if(temp.equals("����")||temp.equals("�糿")||temp.equals("����")) {
					hour = 8;
				}
				if(temp.equals("����")) {
					hour = 14;
				}
				else if(temp.equals("����")) {
					hour = 12;
				}
				else if(temp.equals("����")) {
					hour = 16;
				}
				else if(temp.equals("����")||temp.equals("ҹ��")) {
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
		body = body.replaceAll(detectString5, "");// ɾ���Ѽ������ַ���
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
