/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.InventoryInfo;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author sonmapsi
 */
public class Response {
    private final boolean DEBUG = true;
    private final String PREFIX = "Customer.";
    private String action;
    private boolean result;
    private String reason;
    private Map<String, Object> response;
   
    public Response(String action, String jsonResult) throws ResponseParseException {
        parse(action, jsonResult);
    }
    
    private boolean parse(String action, String jsonResult) throws ResponseParseException {
        try {
            response = (Map) JSONValue.parseWithException(jsonResult);
        } catch (ParseException ex) {
            System.out.println("Parsing Error with Response: " + jsonResult);
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parseJSON", ex);
            throw new ResponseParseException(ex.toString());
        }
        
        if (DEBUG) {
            System.out.println("response: " + response.toString());
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
    
    public ArrayList<String> getItemList() throws ResponseParseException {
        try {
            JSONArray itemListObj = (JSONArray) response.get("items");
            ArrayList<String> itemList = new ArrayList<>();

            for (Object item : itemListObj) {
                itemList.add(item.toString());
            }

            return itemList;
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getItemList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getItemList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
    
    public InventoryInfo getItemInfo() throws ResponseParseException {
        try {
            String name = response.get("name").toString();
            String description = response.get("description").toString();
            float price = (float) ((double) response.get("price"));
            int count = (int) ((long) response.get("count"));

            return new InventoryInfo(name, description, price, count);
        } catch (NullPointerException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getItemList", response.toString());
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, "parse-getItemList", ex);
            throw new ResponseParseException(ex.toString());
        }
    }
}
