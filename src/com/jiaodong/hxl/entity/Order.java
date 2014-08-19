package com.jiaodong.hxl.entity;

public class Order {
	private int hotel_id;
	private String name;
	private String tel;
	private int people;
	private String time;
	private String date;
	private boolean room;
	private int price;
	private String remark;
	public int getHotel_id() {
		return hotel_id;
	}
	public void setHotel_id(int hotel_id) {
		this.hotel_id = hotel_id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public boolean isRoom() {
		return room;
	}
	public void setRoom(boolean room) {
		this.room = room;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "Order [hotel_id=" + hotel_id + ", name=" + name + ", tel="
				+ tel + ", people=" + people + ", time=" + time + ", date="
				+ date + ", room=" + room + ", price=" + price + ", remark="
				+ remark + "]";
	}
	
	
}
