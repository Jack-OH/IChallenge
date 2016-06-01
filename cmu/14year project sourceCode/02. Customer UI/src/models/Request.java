/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.OrderInfo;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONValue;

/**
 *
 * @author sonmapsi
 */
public class Request {
    private final String PREFIX = "Customer.";
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
    
    public String getItemList() {
        addAction("getItemList");
        
        return JSONValue.toJSONString(request);
    }
    
    public String getItemInfo(String itemName) {
        addAction("getItemInfo");
        request.put("name", itemName);
        
        return JSONValue.toJSONString(request);
    }
    
    public String makeOrder(String userId, OrderInfo orderInfo) {
        addAction("makeOrder");
        
        request.put("userId", userId);
        request.put("orderInfo", orderInfo.getOrderInfo());
        
        return JSONValue.toJSONString(request);
    }
 
    public Response getResponse(String jsonResponse) throws ResponseParseException {
        Response response = new Response(getAction(), jsonResponse);

        return response;
    }
}
