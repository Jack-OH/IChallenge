package com.lge.simple.WMSserver.MsgHandler;

import java.util.ArrayList; 
import java.util.HashMap; 

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.lge.simple.WMSserver.WMSServer;
import com.lge.simple.WMSserver.DB.ConstantMessages;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.Item;
import com.lge.simple.WMSserver.Model.OrderInfo;
import com.lge.simple.WMSserver.Model.RobotStatus;
import com.lge.simple.WMSserver.Model.WarehouseInfo; 

public class SClientMsgHandler extends ClientMsgHandler {
	private static final String CMD_GETWAREHOUSELIST = "Supervisor.getWarehouseList";
	private static final String CMD_GETINVENTORYLIST = "Supervisor.getInventoryList";
	private static final String CMD_GETINVENTORYINFO = "Supervisor.getInventoryInfo";
	private static final String CMD_UPDATEINVENTORY = "Supervisor.updateInventory";
	private static final String CMD_GETROBOTSTATUS = "Supervisor.getRobotStatus";
	private static final String CMD_GETORDERLIST = "Supervisor.getOrderList";
	private static final String CMD_MOVEROBOTMANUAL = "Supervisor.moveRobotManual";
	private static final String CMD_GETORDERSTATUS = "Supervisor.getOrderStatus";
	private static final String CMD_SETMANUALCONTROLMODE = "Supervisor.setRobotManualControlMode";
	private static final String CMD_CHECKBACKORDERD = "Supervisor.checkBackOrdered";

	private static SClientMsgHandler instance = new SClientMsgHandler();

	private SClientMsgHandler() {

	}

	public static SClientMsgHandler getInstance() {
		return instance;
	}

	public String HandleMessage(String recvString, Client client) {
		JSONObject recvJsonObj = (JSONObject) JSONValue.parse(recvString);
		String respString = "";

		String action = (String) recvJsonObj.get("action");

		switch (action) {
		case CMD_GETWAREHOUSELIST:
			respString = getWarehouseList(recvJsonObj);
			break;
		case CMD_GETINVENTORYLIST:
			respString = getInventoryList(recvJsonObj);
			break;
		case CMD_GETINVENTORYINFO:
			respString = getInventoryInfo(recvJsonObj);
			break;
		case CMD_UPDATEINVENTORY:
			respString = updateInventory(recvJsonObj);
			break;
		case CMD_GETROBOTSTATUS:
			respString = getRobotStatus(recvJsonObj);
			break;
		case CMD_MOVEROBOTMANUAL:
			respString = MoveRobotManual(recvJsonObj);
			break;
		case CMD_GETORDERLIST:
			respString = getOrderList(recvJsonObj);
			break;
		case CMD_GETORDERSTATUS:
			respString = getOrderStatus(recvJsonObj);
			break;
		case CMD_SETMANUALCONTROLMODE:
			respString = setManaulControlMode(recvJsonObj);			
			break;
		case CMD_CHECKBACKORDERD:
			respString = checkBackorderd(recvJsonObj);
			break;
		}

		System.out.println("Supervisor Client Doing : " + recvString );
		return respString;

		// String sender = (String) jsonObj.get("SENDER");
		// String cmd = (String) jsonObj.get("COMMAND");
		// String msg = (String) jsonObj.get("MESSAGE");

		// return "SuperVisor Client Doing : " + recvString +"\n"+
		// "SENDER : "+sender + "\nCMD :" + cmd + "\nMSG :"+msg+"\n";
	}

	@SuppressWarnings("unchecked")
	private String checkBackorderd(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		String result = "true";
		String reason = "";
		String backordered = "";
		
		backordered = WMSServer.WMSDB.checkBackorderd();

		if (backordered == "-1")
		{
			result = "false";
			reason = "DB false";
		}
		
		respJsonObj.put("action", CMD_CHECKBACKORDERD);
		respJsonObj.put("result", result);
		if (result == "false")
			respJsonObj.put("reason", reason);
		else
			respJsonObj.put("backordered",backordered);
		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String setManaulControlMode(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		int wmsId = (Long.valueOf((long) (recvJsonObj.get("warehouse")))).intValue();
		int robotId = (Long.valueOf((long) (recvJsonObj.get("robot")))).intValue();
		String manualMode = (String) recvJsonObj.get("manualMode");
		
 		String reason = "";
 		
		reason = WMSServer.WMSDB.setManaulControlMode(wmsId, robotId, manualMode);
		respJsonObj.put("action", CMD_SETMANUALCONTROLMODE);
		respJsonObj.put("warehouse",wmsId);
		respJsonObj.put("robot",robotId);	
		respJsonObj.put("maunalMode",manualMode);		
		if (reason == "")
			respJsonObj.put("result", "true");
		else
		{
			respJsonObj.put("result", "false");
			respJsonObj.put("reason", reason);
		}

		return respJsonObj.toJSONString();

	}

	@SuppressWarnings("unchecked")
	private String MoveRobotManual(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		String result = "";
		String reason = "";
		int wmsId = (Long.valueOf((long) (recvJsonObj.get("warehouse")))).intValue();
		int robotId = (Long.valueOf((long) (recvJsonObj.get("robot")))).intValue();
		String direction = (String) recvJsonObj.get("direction");
		
		reason = WMSServer.WMSDB.insertRobotMoveOrder(wmsId, robotId, direction);
		
		if (reason == "")
		{
			result = "true";
		}
		else 
			result = "false";
		
		
		respJsonObj.put("action", CMD_MOVEROBOTMANUAL);
		respJsonObj.put("warehouse", wmsId);
		respJsonObj.put("robot", robotId);
		respJsonObj.put("direction", direction);
		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);

		return respJsonObj.toJSONString();

	}

	@SuppressWarnings("unchecked")
	private String getWarehouseList(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		String result = "";
		String reason = "";
		ArrayList<WarehouseInfo> warehouses = WMSServer.WMSDB.getWarehouselist();

		if (warehouses.size() == 0)
		{
			result = "false";
			reason = "No Warehouse or DB Error";
		}
		else
			result = "true";

		respJsonObj.put("action", CMD_GETWAREHOUSELIST);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		else
		{
			JSONObject warehouseList = new JSONObject();
			int tempint = 1;
			for (WarehouseInfo w : warehouses)
			{
				JSONObject warehouseDetail = new JSONObject();
				warehouseDetail.put("name", w.name);
				warehouseDetail.put("location", w.location);
				warehouseDetail.put("stationCount", w.stationCount);
				warehouseList.put(tempint, warehouseDetail); 
				tempint++;
				}
			respJsonObj.put("warehouses", warehouseList);
		}

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String getInventoryList(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		String result = "";
		String reason = "";
		ArrayList<String> inventories = WMSServer.WMSDB.getInventoryList();

		if (inventories == null) {
			result = "false";
			reason = "DB Error";
		} else
			result = "true";

		respJsonObj.put("action", CMD_GETINVENTORYLIST);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		if (inventories.size() != 0)
			respJsonObj.put("inventories", inventories);

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String getInventoryInfo(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		
		int id = (Long.valueOf((long) (recvJsonObj.get("warehouse")))).intValue();
		String name = (String) recvJsonObj.get("name");
		String result = "";
		String reason = "";
		String description = "";
		float price = 0;

		HashMap<Integer, Integer> cntInventoryItemInStation = WMSServer.WMSDB
				.getInventoryInfo(id, name);

		if (cntInventoryItemInStation == null) {
			result = "false";
			reason = "DB Error";
		} else {
			result = "true";
			Item i = WMSServer.WMSDB.getItemInfo(name);
			description = i.desc;
			price = i.price;

		}
		respJsonObj.put("action", CMD_GETINVENTORYINFO);
		respJsonObj.put("warehouse", id);
		respJsonObj.put("name", name);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		else {
			respJsonObj.put("description", description);
			respJsonObj.put("price", price);
			respJsonObj.put("items", cntInventoryItemInStation);			
		}

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String updateInventory(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		int id = (Long.valueOf((long) (recvJsonObj.get("warehouse")))).intValue();
		String name = (String) recvJsonObj.get("name");
		String description = (String) recvJsonObj.get("description");
		double price = (double) recvJsonObj.get("price");
		HashMap<String, Long> items = (HashMap<String, Long>) recvJsonObj.get("items"); 
		String reason = "";
		String result = "";		

		reason = WMSServer.WMSDB.updateInventory(id,name,description,price,items);

		if (reason != "")
			result = "false";
		else
			result = "true";

		respJsonObj.put("action", CMD_UPDATEINVENTORY);
		respJsonObj.put("warehouse", id);
		respJsonObj.put("name", name);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String getRobotStatus(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		int wmsId = (Long.valueOf((long) (recvJsonObj.get("warehouse")))).intValue();;
		String result = "";
		String reason = "";
		ArrayList<RobotStatus> status = 
				WMSServer.WMSDB.getRobotStatus(wmsId);
		
		if(status.size() == 0)
		{
			result = "false";
			reason = "NO robot or DB Error";
		}
		else
			result = "true";

		respJsonObj.put("action", CMD_GETROBOTSTATUS);
		respJsonObj.put("warehouse", wmsId);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		else
		{
			int tempint = 1;
			JSONObject robotinfoList = new JSONObject();
			for (RobotStatus r : status)
			{
				JSONObject robotinfo = new JSONObject();
				JSONObject robotLocinfo = new JSONObject();
				robotLocinfo.put("src",Integer.parseInt(r.location.get("src")));
				robotLocinfo.put("dst",Integer.parseInt(r.location.get("dst")));
				robotinfo.put("location", robotLocinfo);
				robotinfo.put("loadedItems",r.loadedItem);
				robotinfo.put("status",r.status);
				if (r.status.equals(ConstantMessages.STATUS_INPROGRESS))
					robotinfo.put("orderId", r.orderId);
				robotinfo.put("manualControl",r.manualControl);
				if (r.error != "" &&  r.error != null) robotinfo.put("error", r.error);
				robotinfoList.put(tempint,robotinfo);
				tempint++;
			}
			respJsonObj.put("statuses", robotinfoList);
		}
		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String getOrderList(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();

		String result = "true";
		String reason = "";
		ArrayList<Integer> orders = WMSServer.WMSDB.getOrderList();
		
		if (orders == null) {
			result = "false";
		}

		respJsonObj.put("action", CMD_GETORDERLIST);

		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		respJsonObj.put("orders", orders);

		return respJsonObj.toJSONString();
	}

	@SuppressWarnings("unchecked")
	private String getOrderStatus(JSONObject recvJsonObj) {
		JSONObject respJsonObj = new JSONObject();
		int orderid = (Long.valueOf((long) (recvJsonObj.get("id")))).intValue();;

		String userid = "";
		String status = "";
		String result = "";
		String reason = "";
		String route = "";
		ArrayList<String> backorderItems = null;
		HashMap<String, Integer> items = null;


		OrderInfo oi = WMSServer.WMSDB.getOrderInfo(orderid);
		
		if (oi == null) {
			result = "false";
			reason = "No Order Infomation or DB Error";
		}
		else
		{
			result = "true";
			userid = oi.userId;
			status = oi.status;
			items = oi.items;
			if (status.equals(ConstantMessages.STATUS_INPROGRESS) &&
					oi.route != null)
				route = oi.route;
				
			if (oi != null &&
					oi.status != null &&
					oi.status.equals(ConstantMessages.STATUS_BACKORDER))
				backorderItems  = oi.backorderdItems;			
		}
		
		
		respJsonObj.put("action", CMD_GETORDERSTATUS);
		respJsonObj.put("id", orderid);
		respJsonObj.put("result", result);
		if (result == "false" && reason != "")
			respJsonObj.put("reason", reason);
		else
		{
		respJsonObj.put("userId", userid);
		respJsonObj.put("status", status);
		respJsonObj.put("items", items);
		respJsonObj.put("route", route);
		if (backorderItems != null)
			respJsonObj.put("backorderedItems", backorderItems);
		}
		return respJsonObj.toJSONString();
	}
}
