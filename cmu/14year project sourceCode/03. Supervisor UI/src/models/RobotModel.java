/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author sonmapsi
 */
public class RobotModel {
    private final boolean DEBUG = true;
    
    private int warehouseId;
    private final RobotListModel robotListModel;
    
    public RobotModel(int warehouseId) {
        this.warehouseId = warehouseId;
        
        robotListModel = new RobotListModel(warehouseId);
    }
    
    public void setWarehouseId(int id) {
        warehouseId = id;
        robotListModel.setWarehouseId(id);
    }
    
    public RobotListModel getRobotListModel() {
        return robotListModel;
    }
    
    public void refreshRobotStatus() throws ResponseErrorResultException {
        robotListModel.refreshRobotStatus();
    }
    
    public boolean setRobotManualControlMode(int robotId, boolean manualMode) throws ResponseErrorResultException {
        String onOff = manualMode ? "on" : "off";
        if (DEBUG) {
            System.out.println("robot manual mode: robot=" + robotId + ", on/off=" + onOff);
        }
        
        boolean result;
        result = Protocol.getInstance().setRobotManualControlMode(warehouseId, robotId, onOff);
        
        return result;
    }
    
    public boolean moveRobotManual(int robotId, String direction) throws ResponseErrorResultException {
        if (DEBUG) {
            System.out.println("robot manual move: robot=" + robotId + ", direction=" + direction);
        }
        
        boolean result;
        result = Protocol.getInstance().moveRobotManual(warehouseId, robotId, direction);
        
        return result;
    }
}
