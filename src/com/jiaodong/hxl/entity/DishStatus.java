package com.jiaodong.hxl.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DishStatus implements Parcelable{
	private int id;
	private String name;
	private String reason;
	
	public DishStatus(){};
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Parcelable.Creator<DishStatus> CREATOR = new Creator<DishStatus>(){  
		  
        public DishStatus createFromParcel(Parcel source) {  
        	DishStatus dish = new DishStatus();
        	dish.setId(source.readInt());
        	dish.setName(source.readString());
        	dish.setReason(source.readString());
            return dish;  
        }  
  
        public DishStatus[] newArray(int size) {  
            return new DishStatus[size];  
        }  
          
    };
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeInt(id);
		p.writeString(name);
		p.writeString(reason);
	}
}
