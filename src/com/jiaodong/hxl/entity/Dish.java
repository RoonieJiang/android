package com.jiaodong.hxl.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Dish implements Parcelable{
	private int id;
	private String name;
	private int price;
	private String weight;
	private String img_small;
	private String img_big;
	private String content;
	private String desc;//description
	private DishStatus dishStatus;
	private DishTaste dishTaste;
	private int show = 0;
	private int count = 1;
	private int onep = 0;//等于1时,表示每人一份
	private int recommend = 0;//是否推荐
	private int zan = 0;
	private int order_number = 0;//服务器点菜计数
	
	public Dish() {
	}
	
	public Dish(int id, String name, int price, String weight) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.weight = weight;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof Dish) {
			if(((Dish)o).getId() == this.id) {
				return true;
			}
		}
		return super.equals(o);
	}

	public int getOrder_number() {
		return order_number;
	}

	public void setOrder_number(int order_number) {
		this.order_number = order_number;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public int getOnep() {
		return onep;
	}

	public void setOnep(int onep) {
		this.onep = onep;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage_small() {
		return img_small;
	}

	public void setImage_small(String image_small) {
		this.img_small = image_small;
	}

	public String getImage_big() {
		return img_big;
	}

	public void setImage_big(String image_big) {
		this.img_big = image_big;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String description) {
		this.desc = description;
	}

	public DishStatus getDishStatus() {
		return dishStatus;
	}

	public void setDishStatus(DishStatus dishStatus) {
		this.dishStatus = dishStatus;
	}

	public DishTaste getDishTaste() {
		return dishTaste;
	}

	public void setDishTaste(DishTaste dishTaste) {
		this.dishTaste = dishTaste;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	@Override
	public String toString() {
		return "Dish [id=" + id + ", name=" + name + ", price=" + price
				+ ", weight=" + weight + ", img_small=" + img_small
				+ ", img_big=" + img_big + ", content=" + content + ", desc="
				+ desc + ", dishStatus=" + dishStatus + ", dishTaste="
				+ dishTaste + ", show=" + show + ", count=" + count + ", onep="
				+ onep + ", recommend=" + recommend + ", zan=" + zan
				+ ", order_number=" + order_number + "]";
	}


	public static Parcelable.Creator<Dish> CREATOR = new Creator<Dish>(){  
		  
        public Dish createFromParcel(Parcel source) {  
        	Dish dish = new Dish();
        	dish.setId(source.readInt());
        	dish.setName(source.readString());
        	dish.setPrice(source.readInt());
        	dish.setWeight(source.readString());
        	dish.setImage_small(source.readString());
        	dish.setImage_big(source.readString());
        	dish.setContent(source.readString());
        	dish.setDescription(source.readString());
        	dish.setDishStatus((DishStatus)source.readParcelable(DishStatus.class.getClassLoader()));
        	dish.setDishTaste((DishTaste)source.readParcelable(DishStatus.class.getClassLoader()));
        	dish.setCount(source.readInt());
        	dish.setOnep(source.readInt());
        	dish.setRecommend(source.readInt());
        	dish.setZan(source.readInt());
        	dish.setOrder_number(source.readInt());
            return dish;  
        }  
  
        public Dish[] newArray(int size) {  
            return new Dish[size];  
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
    	p.writeInt(price);
    	p.writeString(weight);
    	p.writeString(img_small);
    	p.writeString(img_big);
    	p.writeString(content);
    	p.writeString(desc);
    	p.writeParcelable(dishStatus, arg1);
    	p.writeParcelable(dishTaste, arg1);
    	p.writeInt(count);
    	p.writeInt(onep);
    	p.writeInt(recommend);
    	p.writeInt(zan);
    	p.writeInt(order_number);
	}
	
}
