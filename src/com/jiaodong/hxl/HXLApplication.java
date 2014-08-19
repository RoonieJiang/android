package com.jiaodong.hxl;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.jiaodong.hxl.entity.Hotel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class HXLApplication extends Application {
	final static int NETWORK_LOADING = 0;
	final static int NETWORK_NOTAVAILABLE = -1;
	final static int NETWORK_NORMAL = 1;
	
	final static String LOGIN_TEL_KEY = "login_key";
	final static String LOGIN_TEL = "login";
	
	private static HXLApplication instance;
	public static Hotel hotel;
	
	public static HXLApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	/**
	 * 跟本程序有关的文件都放到这个文件夹下面
	 * 
	 * @return 程序文件夹
	 */
	public String getStrogePath() {
		return Environment.getExternalStorageDirectory() + File.separator
				+ HXLApplication.getInstance().getResources().getString(R.string.package_dir);
	}
	/**
	 * @return 临时图片文件夹
	 */
	public String getTmpImagePath() {
		return getStrogePath()
				+ File.separator
				+ "tmp_image";
	}

	/**
	 * 获取网络是否可用状态
	 * 
	 * @return
	 */
	public boolean networkIsAvailable() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		if (info.isConnected()) {
			return true;
		}
		return false;
	}

	public int getNetType() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null) {
			return info.getType();
		} else {
			return -1;
		}
	}

	public String getDeviceId() {
		String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();
		if ((deviceId == null)||(deviceId.equals(""))) {
			deviceId = "-1";
		}
		return deviceId;
	}
	
	public String getMac() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		String macAddress = wifiManager.getConnectionInfo().getMacAddress();
		if ((macAddress == null)||(macAddress.equals(""))) {
			String str = "";
			try {
				Process pp = Runtime.getRuntime().exec(
						"cat /sys/class/net/wlan0/address");
				InputStreamReader ir = new InputStreamReader(pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);

				for (; null != str;) {
					str = input.readLine();
					if (str != null) {
						macAddress = str.trim();// 去空格
						break;
					}
				}
			} catch (Exception ex) {
				// 赋予默认值
				macAddress = "-2";
				ex.printStackTrace();
			}
		}
		return macAddress;
	}
}
