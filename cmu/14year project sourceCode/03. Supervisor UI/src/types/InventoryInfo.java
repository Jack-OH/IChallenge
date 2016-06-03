/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author sonmapsi
 */
public final class InventoryInfo {
    private int warehouseId;
    private String name;
    private String description;
    private float price;
    private ArrayList<InventoryItemInfo> items;
    
    public InventoryInfo(int warehouseId) {
        this.warehouseId = warehouseId;
        this.name = "";
        this.description = "";
        this.price = 0.0f;
        this.items = new ArrayList<>();
    }
    
    public InventoryInfo(int warehouseId, String name, String description, float price, ArrayList<InventoryItemInfo> items) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.items = (ArrayList<InventoryItemInfo>) items.clone();
        
        Collections.sort(this.items, new Comparator<InventoryItemInfo>() {
            @Override
            public int compare(InventoryItemInfo item1, InventoryItemInfo item2) {
                return item1.getStationId().compareTo(item2.getStationId());
            }
        });
    }
    
    public int getWarehouseId() {
        return this.warehouseId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public float getPrice() {
        return this.price;
    }
    
    public ArrayList<InventoryItemInfo> getItems() {
        return this.items;
    }
    
    public void setWarehouseId(int id) {
        warehouseId = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(float price) {
        this.price = price;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void changeItemCount(int index, int count) {
        InventoryItemInfo item = this.items.get(index);
        item.setCount(count);
        this.items.set(index, item);
    }
    
    public void changeItemChangedCount(int index, int changedCount) {
        InventoryItemInfo item = this.items.get(index);
        item.setChangedCount(changedCount);
        this.items.set(index, item);
    }
    
    public void clearAllItemChangedCount() {
        for (InventoryItemInfo item: items) {
            item.setChangedCount(0);
        }
    }
    
    public void clearItemChangedCount() {
        for (InventoryItemInfo item: items) {
            item.setCount(0);
        }
    }
    
    public void setItems(ArrayList<InventoryItemInfo> items) {
        this.items.clear();
        for (InventoryItemInfo item: items) {
            this.items.add(item);
        }
    }
}
