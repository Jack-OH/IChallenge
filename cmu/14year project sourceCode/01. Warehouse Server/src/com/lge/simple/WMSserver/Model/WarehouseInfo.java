package com.lge.simple.WMSserver.Model;
 
public class WarehouseInfo  {
	public WarehouseInfo(String name, String location, int stationCount) {
		this.name = name;
		this.location = location;
		this.stationCount = stationCount;
	}
	public String name;
	public String location;
	public int stationCount;
}
