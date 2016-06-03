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
public final class RobotStatus {
    private final int warehouseId;
    private final int id;
    private final RobotLocation location;
    private final ArrayList<ItemInfo> loadedItem;
    private final String status;
    private final boolean manualControl;
    private String errorMessage;
    
    public RobotStatus(int warehouseId, int id, RobotLocation location, ArrayList<ItemInfo> loadedItem, String status, boolean manualControl) {
        this.warehouseId = warehouseId;
        this.id = id;
        this.location = location;
        this.loadedItem = loadedItem;
        this.status = status;
        this.manualControl = manualControl;
        this.errorMessage = "";
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getWarehouseId() {
        return this.warehouseId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public RobotLocation getLocation() {
        return this.location;
    }
    
    public ArrayList<ItemInfo> getLoadedItem() {
        return this.loadedItem;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public boolean getManualControl() {
        return this.manualControl;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
