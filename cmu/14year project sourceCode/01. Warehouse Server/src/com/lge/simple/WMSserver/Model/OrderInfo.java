package com.lge.simple.WMSserver.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderInfo {
	public String userId;
	public int orderId;
	public String status;
	public String route;
	public HashMap<String,Integer> items = new HashMap<String,Integer>();
	public ArrayList<String> backorderdItems = new ArrayList<String>();
}
