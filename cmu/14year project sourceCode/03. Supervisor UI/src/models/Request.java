/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.InventoryInfo;
import types.InventoryItemInfo;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONValue;

/**
 *
 * @author sonmapsi
 */
public class Request {
    private final String PREFIX = "Supervisor.";
    private final Map<String, Object> request;
    
    public Request() {
        request = new LinkedHashMap<>();
    }
    
    private void addAction(String action) {
        request.put("action", PREFIX + action);
    }
    
    public String getAction() {
        return request.get("action").toString();
    }
    
    public String getWarehouseList() {
        addAction("getWarehouseList");
        
        return JSONValue.toJSONString(request);
    }
    
    public String getInventoryList(int warehouseId) {
        addAction("getInventoryList");
        
        request.put("warehouse", warehouseId);
        
        return JSONValue.toJSONString(request);
    }
    
    public String getInventoryInfo(int warehouseId, String name) {
        addAction("getInventoryInfo");
        
        request.put("warehouse", warehouseId);
        request.put("name", name);
        
        return JSONValue.toJSONString(request);
    }
    
    public String updateInventory(InventoryInfo inventory) {
        addAction("updateInventory");
        
        request.put("warehouse", inventory.getWarehouseId());
        request.put("name", inventory.getName());
        request.put("description", inventory.getDescription());
        request.put("price", inventory.getPrice());
        
        HashMap<String, Integer> items = new HashMap<>();
        for (InventoryItemInfo item: inventory.getItems()) {
            items.put(item.getStationId(), item.getChangedCount());
        }
        request.put("items", items);
        
        return JSONValue.toJSONString(request);
    }
    
    public String getRobotStatus(int warehouseId) {
        addAction("getRobotStatus");
        
        request.put("warehouse", warehouseId);
        
        return JSONValue.toJSONString(request);
    }
    
    public String setRobotManualControlMode(int warehouseId, int robotId, String onOff) {
        addAction("setRobotManualControlMode");
        
        request.put("warehouse", warehouseId);
        request.put("robot", robotId);
        request.put("manualMode", onOff);
        
        return JSONValue.toJSONString(request);
    }
    
    public String moveRobotManual(int warehouseId, int robotId, String direction) {
        addAction("moveRobotManual");
        
        request.put("warehouse", warehouseId);
        request.put("robot", robotId);
        request.put("direction", direction);
        
        return JSONValue.toJSONString(request);
    }
 
    public Response getResponse(String jsonResponse) throws ResponseParseException {
        Response response = new Response(getAction(), jsonResponse);

        return response;
    }
    
    public String getOrderList(String filter) {
        addAction("getOrderList");
        
        // TODO: Filter is not implemented in server
        //request.put("filter", filter);
        
        return JSONValue.toJSONString(request);
    }
    
    public String getOrderStatus(int id) {
        addAction("getOrderStatus");
        
        request.put("id", id);
        
        return JSONValue.toJSONString(request);
    }
    
    public String checkBackOrdered() {
        addAction("checkBackOrdered");
        
        return JSONValue.toJSONString(request);
    }
}
