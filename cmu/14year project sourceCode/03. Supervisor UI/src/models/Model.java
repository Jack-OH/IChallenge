/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;

/**
 *
 * @author sonmapsi
 */
public class Model {
    //private String warehouse;
    //private InventoryModel inventoryModel;
    //private RobotModel robotModel;
    //private OrderModel orderModel;
    private WarehouseComboBoxModel warehouseModel;
    ArrayList<Warehouse> warehouseList;
    
    public Model() {
        //inventoryModel = new InventoryModel();
        //robotModel = new RobotModel();
        //orderModel = new OrderModel();
    }
    
    public void setWarehouseList() throws ResponseErrorResultException {
        // Send get Warehouse List Command
        warehouseList = Protocol.getInstance().getWarehouseList();
        warehouseModel = new WarehouseComboBoxModel(warehouseList);
    }
    
    public WarehouseComboBoxModel getWarehouseComboBoxModel() {
        return warehouseModel;
    }
    
    public int getSelectedWarehouseIndex() {
        int index;
        Object obj;
        
        obj = warehouseModel.getSelectedItem();
        index = warehouseModel.getIndexOf(obj);
        
        return index;
    }
    
    public int getWarehouseStationCount(int warehouseId) {
        int stationCount = 0;
        
        for (Warehouse warehouse: warehouseList) {
            if (warehouseId == warehouse.getId()) {
                stationCount = warehouse.getStationCount();
                break;
            }
        }
        
        return stationCount;
    }
}
