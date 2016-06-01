/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.InventoryInfo;
import types.InventoryItemInfo;
import java.util.ArrayList;

/**
 *
 * @author sonmapsi
 */
public class InventoryModel {
    private final boolean DEBUG = true;
    
    private String warehouseName;
    private int warehouseId;
    private int warehouseStationCount;
    private final InventoryListModel inventoryListModel;
    private final InventoryItemModel inventoryItemModel;
    private final InventoryUpdateItemModel inventoryUpdateItemModel;
    
    private InventoryInfo inventoryInfo;
    
    public InventoryModel(int warehouseId) {
        this.warehouseId = warehouseId;
        inventoryListModel = new InventoryListModel(warehouseId);
        inventoryItemModel = new InventoryItemModel();
        inventoryUpdateItemModel = new InventoryUpdateItemModel();
        
        inventoryInfo = new InventoryInfo(warehouseId);
    }
    
    public void setInventoryInfo(InventoryInfo inventoryInfo) {
        this.inventoryInfo = inventoryInfo;
    }
    
    public InventoryInfo getInventoryInfo() {
        return this.inventoryInfo;
    }
    
    public void setInventoryName(String name) {
        inventoryInfo.setName(name);
    }
    
    public String getInventoryName() {
        return inventoryInfo.getName();
    }
    
    public boolean isNewInventory() {
        int index = inventoryListModel.getInventoryIndexWithName(inventoryInfo.getName());
        return index == -1;
    }
    
    public void setInventoryDescription(String description) {
        inventoryInfo.setDescription(description);
    }
    
    public void setInventoryPrice(float price) {
        inventoryInfo.setPrice(price);
    }
    
    public void setInventoryItems(ArrayList<InventoryItemInfo> items) {
        inventoryInfo.setItems(items);
    }
    
    public void changeInventoryItemCount(int index, int count) {
        inventoryInfo.changeItemCount(index, count);
    }
    
    public void changeInventoryItemChangedCount(int index, int changedCount) {
        inventoryInfo.changeItemChangedCount(index, changedCount);
    }
    
    public void setWarehouseId(int id) {
        warehouseId = id;
        inventoryListModel.setWarehouseId(id);
        inventoryInfo.setWarehouseId(id);
    }
    
    public void setWarehouseName(String name) {
        warehouseName = name;
    }
    
    public void setStationCount(int stationCount) {
        warehouseStationCount = stationCount;
        inventoryListModel.setStationCount(stationCount);
        inventoryItemModel.setStationCount(stationCount);
        inventoryUpdateItemModel.setStationCount(stationCount);
    }
    
    public InventoryListModel getInventoryListModel() {
        return inventoryListModel;
    }
    
    public InventoryItemModel getInventoryItemModel() {
        return inventoryItemModel;
    }
    
    public InventoryUpdateItemModel getInventoryUpdateItemModel() {
        return inventoryUpdateItemModel;
    }
    
    public void refreshInventoryList() throws ResponseErrorResultException {
        inventoryListModel.refreshInventory();
    }
    
    public void refreshInventoryItemList(int selectedRowInInventoryList) {
        ArrayList<InventoryItemInfo> items = (ArrayList<InventoryItemInfo>) inventoryListModel.getValueAt(selectedRowInInventoryList, inventoryListModel.getItemsColumn());
        inventoryItemModel.refreshInventoryItem(items);
    }
    
    public boolean updateInventory() throws ResponseErrorResultException {
        if (DEBUG) {
            System.out.println("update inventory");
        }
        
        boolean orderResult;
        orderResult = Protocol.getInstance().updateInventory(this.inventoryInfo);
        
        return orderResult;
    }
}
