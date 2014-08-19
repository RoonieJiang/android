package com.jiaodong.hxl.entity;

import java.util.ArrayList;
import java.util.Arrays;

public class Hotel {
	private int id;
	private String name;
	private String address;
	private String tel;
	private int max;
	private DinnerTimer[] time;
	private ArrayList<String> img;
	private double lon;
	private double lat;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getlon() {
		return lon;
	}
	public void setlon(double lon) {
		this.lon = lon;
	}
	public double getlat() {
		return lat;
	}
	public void setlat(double lat) {
		this.lat = lat;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public ArrayList<String> getImg() {
		return img;
	}
	public void setImg(ArrayList<String> img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public DinnerTimer[] getTime() {
		return time;
	}
	public void setTime(DinnerTimer[] time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "Hotel [id=" + id + ", name=" + name + ", address=" + address
				+ ", tel=" + tel + ", max=" + max + ", time="
				+ Arrays.toString(time) + ", img=" + img + "]";
	}

	public class DinnerTimer{
		private String start_time;
		private String end_time;
		private int interval;
		public String getStart_time() {
			return start_time;
		}
		public void setStart_time(String start_time) {
			this.start_time = start_time;
		}
		public String getEnd_time() {
			return end_time;
		}
		public void setEnd_time(String end_time) {
			this.end_time = end_time;
		}
		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
		@Override
		public String toString() {
			return "DinnerTimer [start_time=" + start_time + ", end_time="
					+ end_time + ", interval=" + interval + "]";
		}
		
	}
	
}
