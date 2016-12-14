package com.donote.imagehandler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.os.Environment;

public class ImageHandleGlobal 
{
	
	
	public final static String DIR_Work  = Environment.getExternalStorageDirectory().getPath();
	/**
	 * ɾ��ָ��Ŀ¼
	 * @param path
	 * @return
	 */
	public static boolean DeleteDir(File dir) 
	{
		boolean success = true;

		if (dir.exists()) 
		{
			File[] list = dir.listFiles();
			if (list != null) 
			{
				int len = list.length;
				for (int i = 0; i < len; ++i) 
				{
					if (list[i].isDirectory()) 
					{
						DeleteDir(list[i]);
					}else 
					{
						boolean ret = list[i].delete();
						if (!ret) 
						{
							success = false;
						}
					}
				}
			}
		}else 
			success = false;

		if (success) 
			dir.delete();

		return success;
	}
	
	

	/**
	 * ɾ��ָ��Ŀ¼
	 * @param path
	 * @return
	 */
	public static boolean DeleteDir(String path) 
	{
		File dir = new File(path);
		return DeleteDir(dir);
	}
	
	/**
	 * InputStreamת�ַ���
	 * @param is
	 * @return
	 */
	public static String GetString(InputStream is)
	{
		try {
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			return EncodingUtils.getString(buffer, "UTF8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }
	
	public static void copyInputStream(BufferedReader in, BufferedWriter out) throws IOException 
	{		
        char[] buffer = new char[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }
	
	/**
	 * ����Ϊһ���ļ�
	 */
	public static boolean SaveFile(File file,String s) 
	{
		boolean ret = false;
		BufferedOutputStream stream = null;
		try {				
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(s.getBytes());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return ret;
	}
	

	/**
	 * ��������
	 */
	public static boolean SaveNews(File file,BufferedReader br) 
	{
		byte[] lineEOF = {'\r','\n'};
		boolean ret = false;
		BufferedOutputStream stream = null;
		try {				
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			
			String line = br.readLine();
			while(line != null)
			{
				byte[] bs = line.getBytes("GB2312");
				stream.write(bs);
				stream.write(lineEOF);
				
				line = br.readLine();
			}
			ret = true;
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * ����Ϊһ���ļ�
	 */
	public static boolean SaveFile(File file,BufferedReader br) 
	{
		boolean ret = false;
		BufferedWriter output = null;
		try {				
			output = new BufferedWriter(new FileWriter(file));		
			copyInputStream(br,output);
			ret = true;
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * ����Ϊһ���ļ�
	 */
	public static boolean SaveFile(File file,InputStream is) 
	{
		boolean ret = false;
		BufferedOutputStream stream = null;
		try {				
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			
			copyInputStream(is,stream);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * ����Ϊһ���ļ�
	 */
	public static boolean SaveFile(File dir,String filename,InputStream is) 
	{
		if(!dir.exists()) 	
			dir.mkdirs();   
		
		File file = new File(dir.getPath(), filename);
		
		if(file.exists())
		{
			return true;
		}
		
		return SaveFile(file,is);
	}	
	
	/**
	 * ���ֽ����鱣��Ϊһ���ļ�
	 */
	public static boolean SaveFile(File file,byte[] b) 
	{
		return SaveFile(file,b,0,b.length);
	}
	
	/**
	 * ���ֽ����鱣��Ϊһ���ļ�
	 */
	public static boolean SaveFile(File file,byte[] b,int offset,int length) 
	{
		boolean ret = false;
		BufferedOutputStream stream = null;
		try {					
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b, offset, length);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * ���ֽ����鱣��Ϊһ���ļ�
	 */
	public static boolean SaveFile(File dir,String filename,byte[] b,int offset,int length) 
	{
		if(!dir.exists()) 	
			dir.mkdirs();   
		
		File file = new File(dir.getPath(), filename);
		return SaveFile(file,b,offset,length);
	}
	
	/**
	 * ���ֽ����鱣��Ϊһ���ļ�
	 */
	public static boolean SaveFile(File dir,String filename,byte[] b) 
	{
		return SaveFile(dir,filename,b,0,b.length);
	}
	
	/**
	 * ��ȡ�ļ� �����ֽ�����
	 */
	public static byte[] ReadFile(File file) 
	{
		try {
			if(!file.exists()) 	
				return null; 
			
			byte[] bs = new byte[(int) file.length()];
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(bs);
			fileInputStream.close();
			return(bs);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * �����ļ�
	 * @param fs Դ�ļ�
	 * @param fd Ŀ���ļ�
	 * @return
	 */
	public static boolean CopyFile(File fs,File fd) 
	{
		try {
			if(!fs.exists()) 	
				return false; 
			
			if(fd.exists()) 
				fd.delete();

			FileInputStream fileInputStream = new FileInputStream(fs);
			return SaveFile(fd,fileInputStream);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static String Format_Size(String strSize) {
		String s;
		int L = strSize.length();
		if(L<4)
			s = "0."+strSize.substring(0, 1)+"k";
		else if(L>6)
			s = strSize.substring(0, L-6)+"."+strSize.substring(6, 7) + "M";
		else if(L==4)
			s = strSize.substring(0, 1)+"."+strSize.substring(1, 2)+"k";
		else
			s = strSize.substring(0, L-3) + "k";
		return (s);
	}
	
	
	/**
	 * ����<, >,\n �ַ��ķ�����
	 * 
	 * @param input
	 *            ��Ҫ���˵��ַ�
	 * @return ��ɹ����Ժ���ַ���
	 */
	public static String Html_filter(String input) {
		if (input == null) {
			return null;
		}
		if (input.length() == 0) {
			return input;
		}
		input = input.replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll(" ", "&nbsp;");
		input = input.replaceAll("'", "&#39;");
		input = input.replaceAll("\"", "&quot;");
		input = input.replaceAll("\n", "<br>");
		
		return Html_ConvertURL(input);
	}

	public static String Html_ConvertURL(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		} else {
			StringBuffer buf = new StringBuffer();

			int i = 0, j = 0, oldend = 0;
			int len = input.length();
			char cur;

			while ((i = input.indexOf("http://", oldend)) >= 0) {
				j = i + 7;
				cur = input.charAt(j);
				while (j < len) {
					// Is a space?
					if (cur == ' ')
						break;
					// Is html?
					if (cur == '<')
						break;
					// Is a Win32 newline?
					if (cur == '\n')
						break;
					// Is Unix newline?
					if (cur == '\r' && j < len - 1
							&& input.charAt(j + 1) == '\n')
						break;

					j++;
					if (j < len) {
						cur = input.charAt(j);
					}
				}
				buf.append(input.substring(oldend, i));
				buf.append("<a href =\"");
				buf.append(input.substring(i, j));
				buf.append("\">");
				buf.append(input.substring(i, j));
				buf.append("</a>");
				oldend = j;
			}
			buf.append(input.substring(j, len));
			return buf.toString();
		}
	}
	
	@SuppressLint("DefaultLocale")
	public static boolean isImageFile(String filename)
	{
		String s = filename.toLowerCase();
		return (s.endsWith(".jpeg") || 
				s.endsWith(".jpg")  || 
				s.endsWith(".gif")  || 
				s.endsWith(".bmp")  || 
				s.endsWith(".png")
			);
	}
	
	/**
	 * �ַ����ر���
	 * @param str
	 * @param charset
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String ReEncode(String str,String charset)
    {
    	if(charset.toUpperCase().equals("GB2312"))
    		return str;
    	
    	try
    	{
    		return EncodingUtils.getString(str.getBytes("GB2312"), charset);
    	}catch (Exception e) {
			// TODO: handle exception
    		return str;
		}
    }
	
	/**
	 * �ַ����ر���
	 * @param str
	 * @param charset
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String ReEncode2(String str,String charset)
    {
    	if(charset.toUpperCase().equals("UTF8"))
    		return str;
    	
    	try
    	{
    		return EncodingUtils.getString(str.getBytes("UTF8"), charset);
    	}catch (Exception e) {
			// TODO: handle exception
    		return str;
		}
    }
}