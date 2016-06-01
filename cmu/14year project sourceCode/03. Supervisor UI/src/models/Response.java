/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import types.InventoryInfo;
import types.InventoryItemInfo;
import types.ItemInfo;
import types.OrderStatus;
import types.RobotLocation;
import types.RobotStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author sonmapsi
 */
public class Response {
    private final boolean DEBUG = true;
    private final String PREFIX = "Supervisor.";
    private String action;
    private String requestedAction;
    private String requestedjsonResult;
    private boolean result;
    private String reason;
    private Map<String, Object> response;
   
    public Response(String action, String jsonResult) throws ResponseParseException {
        if (DEBUG) {
            System.out.println("response: " + jsonResult);
        }
        requestedAction = action;
        requestedjsonResult = jsonResult;
        parse(action, jsonResult);
    }
    
    @Override
    public String toString() {
        return "Response for action=" + requestedAction + ", res=" + requestedjsonResult;
    }
    
    private boolean parse(String action, String jsonResult) throws ResponseParseException {
        try {
            response = (Map) JSONValue.parseWithException(jsonResult);
        } catch (ParseException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parseJSON", jsonResult);
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parseJSON", ex);
            throw new ResponseParseException(ex.toString());
        }
        
        this.action = response.get("action").toString();
        
        if (!action.equals(this.action)) {
            throw new ResponseParseException("req: " + action + ", res: " + this.action);
        }
        
        result = response.get("result").toString().equals("true");
        if (!result) {
            if (response.containsKey("reason")) {
                reason = response.get("reason").toString();
            } else {
                reason = "";
            }
        } else {
            reason = "";
        }
        
        return false;
    }
    
    public String getAction() {
        return action;
    }
    
    public boolean getResult() {
        return result;
    }
    
    public String getReson() {
        return reason;
    }
    
    public ArrayList<Warehouse> getWarehouseList() throws ResponseParseException {
        try {
            JSONObject warehouseDict = (JSONObject) response.get("warehouses");
            JSONObject warehouseObj;
            String key;
            int id;
            String name;
            String location;
            int stationCount;
            Warehouse warehouse;
            ArrayList<Warehouse> warehouseList = new ArrayList<>();
            Iterator<String> itr = warehouseDict.keySet().iterator();

            while (itr.hasNext()) {
                key = itr.next();
                id = Integer.parseInt(key);
                warehouseObj = (JSONObject) warehouseDict.get(key);
                name = warehouseObj.get("name").toString();
                location = warehouseObj.get("location").toString();
                stationCount = (int) ((long) warehouseObj.get("stationCount"));
                warehouse = new Warehouse(id, name, location, stationCount);
                warehouseList.add(warehouse);
            }

            return warehouseList;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getWarehouseList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getWarehouseList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public ArrayList<String> getInventoryList(int requestedWarehouseId) throws ResponseParseException {
        try {
            JSONArray inventoryListObj = (JSONArray) response.get("inventories");
            ArrayList<String> inventoryList = new ArrayList<>();

            for (Object inventory : inventoryListObj) {
                inventoryList.add(inventory.toString());
            }

            return inventoryList;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getInventoryList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getInventoryList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public InventoryInfo getInventoryInfo(int requestedWarehouseId, String requestedName) throws ResponseParseException  {
        try {
            int warehouseId = (int) ((long) response.get("warehouse"));
            String name = response.get("name").toString();
            String description = response.get("description").toString();
            float price = (float) ((double) response.get("price"));
            
            JSONObject itemsObj = (JSONObject) response.get("items");
            ArrayList<InventoryItemInfo> items = new ArrayList<>();
            Iterator<String> itr = itemsObj.keySet().iterator();
            String stationId;
            int availableCount;

            while (itr.hasNext()) {
                stationId = itr.next();
                availableCount = (int) ((long) itemsObj.get(stationId));
                items.add(new InventoryItemInfo(stationId, availableCount));
            }

            return new InventoryInfo(warehouseId, name, description, price, items);
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getInventoryInfo", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getInventoryInfo", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public ArrayList<RobotStatus> getRobotStatus(int requestedWarehouseId) throws ResponseParseException {
        try {
            int warehouseId = (int) ((long) response.get("warehouse"));
            
            JSONObject robotStatusObj = (JSONObject) response.get("statuses");
            ArrayList<RobotStatus> robots = new ArrayList<>();
            Iterator<String> itr = robotStatusObj.keySet().iterator();
            String key;
            JSONObject robotObj;
            int robotId;
            JSONObject robotLocationObj;
            RobotLocation robotLocation;
            JSONObject loadedItemsObj;
            Iterator<String> itrItem;
            String name;
            int count;
            ArrayList<ItemInfo> loadedItems = new ArrayList<>();
            String status;
            boolean manualControl;
            String errorMessage;
            RobotStatus robotStatus;

            while (itr.hasNext()) {
                key = itr.next();
                robotId = Integer.parseInt(key);
                
                robotObj = (JSONObject) robotStatusObj.get(key);
                
                robotLocationObj = (JSONObject) robotObj.get("location");
                robotLocation = new RobotLocation(
                        (int) ((long) robotLocationObj.get("src")),
                        (int) ((long) robotLocationObj.get("dst")));
                
                try {
                    loadedItemsObj = (JSONObject) robotObj.get("loadedItems");
                    itrItem = loadedItemsObj.keySet().iterator();
                    while (itrItem.hasNext()) {
                        name = itr.next();
                        count = (int) ((long) loadedItemsObj.get(name));
                        loadedItems.add(new ItemInfo(name, count));
                    }
                } catch (NullPointerException ex) {
                    // do nothing
                }
                
                status = robotObj.get("status").toString();
                
                manualControl = robotObj.get("manualControl").toString().toUpperCase().equals("ON");
                
                robotStatus = new RobotStatus(
                        warehouseId,
                        robotId,
                        robotLocation,
                        loadedItems,
                        status,
                        manualControl);
                
                try {
                    errorMessage = robotObj.get("error").toString();
                    robotStatus.setErrorMessage(errorMessage);
                } catch (NullPointerException ex) {
                    // Do nothing
                }
                
                robots.add(robotStatus);
            }

            return robots;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getRobotStatus", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getRobotStatus", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public ArrayList<Integer> getOrderList() throws ResponseParseException {
        try {
            JSONArray orderListObj = (JSONArray) response.get("orders");
            ArrayList<Integer> orderList = new ArrayList<>();

            for (Object order : orderListObj) {
                orderList.add((int) ((long) order));
            }

            return orderList;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public OrderStatus getOrderStatus(int requestedId) throws ResponseParseException  {
        try {
            int id = (int) ((long) response.get("id"));
            String userId = response.get("userId").toString();
            String date;
            String status = response.get("status").toString();
            
            try {
                date = response.get("date").toString();
            } catch (NullPointerException ex) {
                // TODO: date is not implemented in server
                date = "";
            }
            
            JSONObject itemsObj = (JSONObject) response.get("items");
            ArrayList<ItemInfo> items = new ArrayList<>();
            Iterator<String> itr = itemsObj.keySet().iterator();
            String name;
            int count;

            while (itr.hasNext()) {
                name = itr.next();
                count = (int) ((long) itemsObj.get(name));
                items.add(new ItemInfo(name, count));
            }
            
            ArrayList<String> backorderedItems = new ArrayList<>();
            
            try {
                JSONArray backorderedItemsObj = (JSONArray) response.get("backordereditems");
                for (Object item : backorderedItemsObj) {
                    name = item.toString();
                    backorderedItems.add(name);
                }
            } catch (NullPointerException ex) {
                // do nothing
            }

            return new OrderStatus(id, userId, date, status, items, backorderedItems);
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderStatus", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderStatus", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public boolean checkBackOrdered() throws ResponseParseException {
        try {
            boolean backordered = "true".equals(response.get("backordered").toString());

            return backordered;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getOrderList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
}
