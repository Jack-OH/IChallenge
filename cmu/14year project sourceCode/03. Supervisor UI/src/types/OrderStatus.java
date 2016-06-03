/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package types;

import java.util.ArrayList;

/**
 *
 * @author sonmapsi
 */
public class OrderStatus {
    private final int id;
    private final String userId;
    private final String date;
    private final String status;
    private final ArrayList<ItemInfo> items;
    private final ArrayList<String> backorderedItems;
    
    public OrderStatus(int id, String userId, String date, String status, ArrayList<ItemInfo> items, ArrayList<String> backorderedItems) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.status = status;
        this.items = items;
        this.backorderedItems = backorderedItems;
    }
    
    public int getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public ArrayList<ItemInfo> getItems() {
        return items;
    }
    
    public ArrayList<String> getBackorderedItems() {
        return backorderedItems;
    }
}
