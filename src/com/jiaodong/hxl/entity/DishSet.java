package com.jiaodong.hxl.entity;

import java.util.ArrayList;

public class DishSet {
	private int set_id;
	private String set_name;
	private int people;
	private ArrayList<Dish> dish_list;
	public int getSet_id() {
		return set_id;
	}
	public void setSet_id(int set_id) {
		this.set_id = set_id;
	}
	public String getSet_name() {
		return set_name;
	}
	public void setSet_name(String set_name) {
		this.set_name = set_name;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	public ArrayList<Dish> getDish_list() {
		return dish_list;
	}
	public void setDish_list(ArrayList<Dish> dish_list) {
		this.dish_list = dish_list;
	}
	
	
}
