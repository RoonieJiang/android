package com.jiaodong.hxl.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import android.os.Bundle;
import android.os.Message;

public class HttpService {
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	public final static String RESONSE_STRING = "string";
	public final static String RESONSE_BYTE = "byte";
	public final static String EXCEPTION = "exception";
	public final static int SUCCESS_STRING = 1;
	public final static int SUCCESS_BYTE = 2;
	public final static int ERROR = 0;
	private static HttpService instance = null;
	private String baseAddress;

	public static HttpService getInstance() {
		if (instance == null) {
			instance = new HttpService();
		}
		return instance;
	}

	public HttpService() {
		this.baseAddress = "http://p.jiaodong.net/food/hxl/Pub/";
	}

	/**
	 * 
	 * @param name
	 *            参数名
	 * @param value
	 *            参数值
	 * @return 参数字符串
	 * @throws UnsupportedEncodingException
	 */
	private String MapToParam(Map<String, String> paramMap) throws UnsupportedEncodingException {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (paramMap != null) {
			Set<Entry<String, String>> entrySet = paramMap.entrySet();
			for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
				String value = entry.getValue();
				String key = entry.getKey();
				if (value != null && !value.equals("")) {
					formparams.add(new BasicNameValuePair(key, value));
				}
			}

		}
		return URLEncodedUtils.format(formparams, HTTP.UTF_8);
	}

	/**
	 * 
	 * @param url
	 *            地址
	 * @param timeout
	 *            超时时间
	 * @return
	 */
	private HttpURLConnection initHttpURLConnection(String url, int timeout) {
		HttpURLConnection urlConnection = null;
		try {
			URL address = new URL(baseAddress + url);
			urlConnection = (HttpURLConnection) address.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(timeout * 1000);
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlConnection;
	}

	/**
	 * 
	 * @param url
	 *            地址
	 * @param param_name
	 *            参数名
	 * @param param_value
	 *            参数值
	 * @param handler
	 *            回调
	 * @param timeout
	 *            链接超时时间
	 */
	public void callService(final String url, final Map<String, String> paramMap,
			final HttpServiceHandler handler, final int timeout) {
		executorService.submit(new Runnable() {
			// Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				Bundle bundle = new Bundle();
				String response = new String();
				try {
					HttpURLConnection urlConnection = initHttpURLConnection(url, timeout);
					DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
					String content = MapToParam(paramMap);
					out.writeBytes(content);
					out.flush();
					out.close();
					InputStream is = urlConnection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String readLine = null;
					while ((readLine = br.readLine()) != null) {
						response = response + readLine;
					}
					is.close();
					br.close();
					urlConnection.disconnect();
					message.what = SUCCESS_STRING;
					bundle.putString(RESONSE_STRING, response);
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//					message.what = ERROR;
//					bundle.putSerializable(EXCEPTION, e);
//				} catch (IOException e) {
//					e.printStackTrace();
//					message.what = ERROR;
//					bundle.putSerializable(EXCEPTION, e);
				} catch (Exception e){
					e.printStackTrace();
					message.what = ERROR;
					bundle.putSerializable(EXCEPTION, e);
				} finally {
					message.setData(bundle);
					handler.sendMessage(message);
				}
			}
		});
		// thread.start();
	}

	public String synCallService(String url, Map<String, String> paramMap, int timeout) 
			throws MalformedURLException,IOException,ServerException{
		String response = "";
		HttpURLConnection urlConnection = initHttpURLConnection(url, timeout);
		DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		String content = MapToParam(paramMap);
		out.writeBytes(content);
		out.flush();
		out.close();
		InputStream is = urlConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			response = response + readLine;
		}
		is.close();
		br.close();
		urlConnection.disconnect();
		
		if(ServerException.checkServerException(response))
			throw new ServerException();

		return response;
	}
	
	@SuppressWarnings("serial")
	/**
	 * 	同步请求时，服务器端返回异常html页面
	 */
	public static class ServerException extends Exception{
		
		public static boolean checkServerException(String response){
			// 若服务器端返回html页面提示"内部错误"，该错误无法被解析为json格式，此时应提示服务器错误
			if(response.startsWith("<!DOCTYPE") && response.indexOf("您浏览的页面暂时发生了错误")!=-1){
				return true;
			}
			return false;
		}
	}

}
