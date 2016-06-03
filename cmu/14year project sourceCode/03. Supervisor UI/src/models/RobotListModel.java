/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import types.RobotStatus;

/**
 *
 * @author sonmapsi
 */
public class RobotListModel extends DefaultTableModel {
    private final String MODULE_TAG = "ROBOT_LIST_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int ID_COLUMN_INDEX = 0;
    private final int LOCATION_COLUMN_INDEX = 1;
    private final int STATUS_COLUMN_INDEX = 2;
    private final int MANUAL_CONTROL_COLUMN_INDEX = 3;
    private final int ERROR_DESCRIPTIN_COLUMN_INDEX = 4;
    private final String[] COLUMN_NAMES = {"ID", "Location", "Status", "Manual Control", ""};
    private final Class[] TYPES = {
        java.lang.Integer.class,
        java.lang.String.class,
        java.lang.String.class,
        java.lang.String.class,
        java.lang.String.class
    };

    private final boolean[] CAN_EDIT = {false, false, false, false, false};
    
    private int warehouseId;

    public RobotListModel(int warehouseId) {
        super();

        this.warehouseId = warehouseId;
        this.setColumnIdentifiers(COLUMN_NAMES);
    }
    
    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    @Override
    public int getColumnCount() {
        return super.getColumnCount();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return CAN_EDIT[columnIndex];
    }
    
    public void clearItems() {
        int rowCount = this.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            this.removeRow(i);
        }
    }
    
    public int getManualControlColumn() {
        return MANUAL_CONTROL_COLUMN_INDEX;
    }
    
    public int getErrorDescriptionColumn() {
        return ERROR_DESCRIPTIN_COLUMN_INDEX;
    }
    
    public boolean refreshRobotStatus() throws ResponseErrorResultException {
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh robot status");
        }
        
        clearItems();
        
        ArrayList<RobotStatus> robotList;
        
        robotList = Protocol.getInstance().getRobotStatus(warehouseId);
        
        if (robotList == null || robotList.isEmpty()) {
            return false;
        }
        
        for (RobotStatus robot: robotList) {
            addRobot(robot);
        }
        
        return true;
    }
    
    private void addRobot(RobotStatus robot) {
        Object[] data = new Object[] {
            robot.getId(),
            robot.getLocation().toString(),
            robot.getStatus(),
            robot.getManualControl() ? "on" : "off",
            robot.getErrorMessage()
        };
        this.addRow(data);
    }
    
    public String getErrorMessage(int rowIndex) {
        String status = this.getValueAt(rowIndex, STATUS_COLUMN_INDEX).toString();
        String errorMessage = "";
        
        if (status.toUpperCase().equals("ERROR")) {
            errorMessage = this.getValueAt(rowIndex, ERROR_DESCRIPTIN_COLUMN_INDEX).toString();
        }
        
        return errorMessage;
    }
    
    public int getRobotId(int rowIndex) {
        return (int) this.getValueAt(rowIndex, ID_COLUMN_INDEX);
    }
    
    public boolean getManunalControl(int rowIndex) {
        return this.getValueAt(rowIndex, MANUAL_CONTROL_COLUMN_INDEX).toString().equals("on");
    }
    
    public void setManualControl(int robotId, boolean manualControl) {
        for (int i = 0; i < this.getRowCount(); i++) {
            int curRobotId = (int) this.getValueAt(i, ID_COLUMN_INDEX);
            if (curRobotId == robotId) {
                this.setValueAt(manualControl ? "on" : "off", i, MANUAL_CONTROL_COLUMN_INDEX);
                break;
            }
        }
    }
}
