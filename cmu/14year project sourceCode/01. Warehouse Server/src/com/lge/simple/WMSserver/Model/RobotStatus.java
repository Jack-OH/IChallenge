package com.lge.simple.WMSserver.Model;

import java.util.HashMap;

public class RobotStatus {
	public int robotId;
	public int orderId;
	public HashMap<String,String> location;
	public HashMap<String,Integer> loadedItem;
	public String status = "";
	public String manualControl = "";
	public String error = "";
	
	public RobotStatus()
	{
		this.location = new HashMap<String,String>();
		this.loadedItem = new HashMap<String, Integer>();
			  
		
	}
}
