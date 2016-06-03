package com.lge.simple.WMSserver.MsgHandler;

import java.util.ArrayList;
import java.util.Date; 
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue; 
import com.lge.simple.WMSserver.WMSServer; 
import com.lge.simple.WMSserver.Model.Client; 
import com.lge.simple.WMSserver.Model.Item; 

public class CClientMsgHandler extends ClientMsgHandler {
	private static final String CMD_GETITEMLIST = "Customer.getItemList";
	private static final String CMD_GETITMEINFO = "Customer.getItemInfo";
	private static final String CMD_MAKEORDER = "Customer.makeOrder";

	private static CClientMsgHandler instance = new CClientMsgHandler();

	private CClientMsgHandler() {

	}

	public static CClientMsgHandler getInstance() {
		return instance;
	}

	public String HandleMessage(String recvString, Client client) {
		JSONObject recvJsonObj = (JSONObject) JSONValue.parse(recvString);
		String respString = "";

		String action = recvJsonObj.get("action").toString();

		switch (action) {
		case CMD_GETITEMLIST:
			respString = DogetItemList(recvJsonObj);
			break;
		case CMD_GETITMEINFO:
			respString = DogetItemInfo(recvJsonObj);
			break;
		case CMD_MAKEORDER:
			respString = DomakeOrder(recvJsonObj);
			break;
		}

		System.out.println("Customer Client Doing : " + recvString);
		return respString;
	}

	@SuppressWarnings("unchecked")
	private String DomakeOrder(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		String userId = (String) recvJsonObj.get("userId");
		HashMap<String, Long> orderInfo =  (HashMap<String, Long>) recvJsonObj.get("orderInfo");
		
		String result = "true";
		String reason = "";  

		String desc = "";
		Set<String> k = orderInfo.keySet();
		for (String  keystr : k)
		{
			 //System.out.println(keystr  +"  " + orderInfo.get(keystr));
			 desc = desc + keystr + ":" +orderInfo.get(keystr)+","; 
		}
		if( desc.length() !=0 ) 
			desc = desc.substring(0, desc.length()-1);
		WMSServer.WMSDB.insertNewOrder(userId, desc,new Date());
		

		respJsonObj.put("action", CMD_MAKEORDER);
		respJsonObj.put("userId", userId);		
		respJsonObj.put("result", result);
		if (result.equals("false"))
			respJsonObj.put("reason", reason); 

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String DogetItemInfo(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		String name = (String) recvJsonObj.get("name");
		String result = "";
		String reason = "";  
		
		int count = 0; 
		Item item;

		item = WMSServer.WMSDB.getItemInfo(name);
		count = WMSServer.WMSDB.getItemCount(name);

		if(item == null) {
			result = "false";
			reason = "DB Error";
		}
		else
			result = "true";
		
		respJsonObj.put("action", CMD_GETITMEINFO);
		respJsonObj.put("name", name);

		respJsonObj.put("result", result);
		if (result.equals("false"))
			respJsonObj.put("reason", reason);
		else {
			respJsonObj.put("description", item.desc);
			respJsonObj.put("price", item.price);
			respJsonObj.put("count", count);
		}
		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String DogetItemList(JSONObject recvJSONObject) {
		JSONObject respJsonObj = new JSONObject();
		String result = "";
		String reason = "";
		ArrayList<String> items = new ArrayList<String>();

		items = WMSServer.WMSDB.getItemType();
		
		if (items == null) {
			result = "false";
			reason = "DB Error";
		} 
		else result = "true"; 
		
		respJsonObj.put("action", CMD_GETITEMLIST);

		respJsonObj.put("result", result);
		if (reason != "")
			respJsonObj.put("reason", reason);
		if (items.size() != 0)
			respJsonObj.put("items", items);

		return respJsonObj.toJSONString();

	}
}
