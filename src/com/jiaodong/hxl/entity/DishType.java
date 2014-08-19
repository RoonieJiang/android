package com.jiaodong.hxl.entity;

import java.util.ArrayList;

public class DishType {
	private int type_id;
	private String type_name;
	private int number;
	private ArrayList<Dish> dish_list;
	
	public DishType(int id,String name, ArrayList<Dish> dishes) {
		super();
		this.type_id = id;
		this.type_name = name;
		this.dish_list = dishes;
	}
	public int getId() {
		return type_id;
	}
	public void setId(int id) {
		this.type_id = id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return type_name;
	}
	public void setName(String name) {
		this.type_name = name;
	}
	public ArrayList<Dish> getDishes() {
		return dish_list;
	}
	public void setDishes(ArrayList<Dish> dishes) {
		this.dish_list = dishes;
	}
	@Override
	public String toString() {
		return "DishType [type_id=" + type_id + ", type_name=" + type_name
				+ ", number=" + number + ", dish_list=" + dish_list + "]";
	}
	
}
