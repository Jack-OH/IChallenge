/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.InventoryItemInfo;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sonmapsi
 */
public class InventoryItemModel extends DefaultTableModel {
    private final String MODULE_TAG = "INVENTORY_ITEM_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int  STATION_COLUMN_INDEX = 0;
    private final int  CURRENT_COUNT_COLUMN_INDEX = 1;
    private final String[] COLUMN_NAMES = {"Station", "Current Count"};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.Integer.class,
    };

    private final boolean[] CAN_EDIT = {false, false};
    private int stationCount;

    public InventoryItemModel() {
        super();

        this.setColumnIdentifiers(COLUMN_NAMES);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return CAN_EDIT[columnIndex];
    }
    
    public int getCurrentCountColumnIndex() {
        return this.CURRENT_COUNT_COLUMN_INDEX;
    }
    
    public int getCurrentCount(String stationId) {
        int currentCount = 0;
        
        for (int i = 0; i < this.getRowCount(); i++ ) {
            String curStationId = this.getValueAt(i, STATION_COLUMN_INDEX).toString();
            if (curStationId.equals(stationId)) {
                currentCount = (int) this.getValueAt(i, CURRENT_COUNT_COLUMN_INDEX);
                break;
            }
        }
        
        return currentCount;
    }
    
    public void clearItems() {
        int rowCount = this.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            this.removeRow(i);
        }
    }
    
    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }
    
    public boolean refreshInventoryItem(ArrayList<InventoryItemInfo> items) {
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh inventory items");
        }
        
        clearItems();
        
        String stationId;
        int availableCount;
        
        for (InventoryItemInfo item: items) {
            stationId = item.getStationId();
            availableCount = item.getCount();
            addInventoryItem(stationId, availableCount);
        }
        
        return true;
    }
    
    private void addInventoryItem(String stationId, int availableCount) {
        Object data[] = {stationId, availableCount};
        this.addRow(data);
    }
}
