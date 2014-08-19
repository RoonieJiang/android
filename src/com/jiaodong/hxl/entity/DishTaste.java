package com.jiaodong.hxl.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DishTaste implements Parcelable{
	private int id;
	private String name;
	private int level;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Parcelable.Creator<DishTaste> CREATOR = new Creator<DishTaste>(){  
		  
        public DishTaste createFromParcel(Parcel source) {  
        	DishTaste dish = new DishTaste();
        	dish.setId(source.readInt());
        	dish.setName(source.readString());
        	dish.setLevel(source.readInt());
            return dish;  
        }  
  
        public DishTaste[] newArray(int size) {  
            return new DishTaste[size];  
        }  
          
    };
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(id);
		p.writeString(name);
		p.writeInt(level);		
	}
}