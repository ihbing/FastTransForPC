/******************************************************************
 *
 *    Java Lib For Android, Powered By S先生.
 *
 *    Copyright (c) 2017-2017 Digital Telemedia Co.,Ltd
 *    http://www.d-telemedia.com/
 *
 *    Package:     larson
 *
 *    Filename:    HttpUtils.java
 *
 *    Description: TODO(用一句话描述该文件做什么)
 *
 *    Copyright:   Copyright (c) 2001-2014
 *
 *    Company:     Digital Telemedia Co.,Ltd
 *
 *    @author:     S先生
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年9月25日 上午11:32:16
 *
 *    Revision:
 *
 *    2017年9月25日 上午11:32:16
 *        - first revision
 *
 *****************************************************************/
package larson;

/**
 * @ClassName HttpUtils
 * @Description TODO(Http请求的工具类)
 * @author S先生
 * @Date 2017年9月25日 上午11:32:16
 * @version 1.0.0
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils
{

	private static final int TIMEOUT_IN_MILLIONS = 5000;

	public interface CallBack
	{
		void onRequestComplete(int what,String result);
	}


	/**
	 * 异步的Get请求
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					
					System.err.println(urlStr);
					HttpResultMsg result = doGet(urlStr);
					
					if(callBack!=null)callBack.onRequestComplete(result.what, result.msg);
					
				} catch (Exception e)
				{
					if (callBack != null)callBack.onRequestComplete(0,e.toString());
				}

			};
		}.start();
	}

	/**
	 * 异步的Post请求
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	public static void doPostAsyn(final String urlStr, final String params,
								  final CallBack callBack) throws Exception
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					HttpResultMsg result = doPost(urlStr, params);
					
					if(callBack!=null)callBack.onRequestComplete(result.what, result.msg);
					
				} catch (Exception e)
				{
					if (callBack != null)callBack.onRequestComplete(0,e.toString());
				}

			};
		}.start();

	}

	/**
	 * Get请求，获得返回数据
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static HttpResultMsg doGet(String urlStr) 
	{
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		HttpResultMsg hPM=new HttpResultMsg();
		try
		{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				hPM.msg=baos.toString();
				return hPM;
			} else
			{
				hPM.what=0;
				hPM.msg="getResponseCode() != 200";
				return hPM;
			}

		} catch (Exception e)
		{
			hPM.what=0;
			hPM.msg=e.toString();
			return hPM;
			
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				
				hPM.what=0;
				hPM.msg=e.toString();
				return hPM;
				
			}
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
				
				hPM.what=0;
				hPM.msg=e.toString();
				return hPM;
				
			}
			conn.disconnect();
		}

	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static HttpResultMsg doPost(String url, String param) 
	{
		PrintWriter out = null;
		BufferedReader in = null;
		HttpResultMsg hPM=new HttpResultMsg();
		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
				.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
									"application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals(""))
			{
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
				
			String line;
			while ((line = in.readLine()) != null)
			{
				hPM.msg += line;
			}
		} catch (Exception e)
		{
			hPM.what=1;
			
			hPM.msg=e.toString();
			
			return hPM;
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			} catch (IOException ex)
			{
				hPM.what=1;
				
				hPM.msg=ex.toString();
				
				return hPM;
			}
		}

		return hPM;
	}
	
	/** 
     * 获取重定向地址 
     * @param path 
     * @return 
     * @throws Exception 
     */  
    public  static String getRedirectUrl(String path) throws Exception {  
        HttpURLConnection conn = (HttpURLConnection) new URL(path)  
			.openConnection();  
        conn.setInstanceFollowRedirects(false);  
        conn.setConnectTimeout(5000);  
        return conn.getHeaderField("Location");  
    }  
    
  public static class HttpResultMsg {
    	
    	public int what=1;
    	
    	public String msg="";
    	
    }
	
}

