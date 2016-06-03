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
public class InventoryUpdateItemModel extends DefaultTableModel {
    private final String MODULE_TAG = "INVENTORY_UPDATE_ITEM_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int  STATION_COLUMN_INDEX = 0;
    private final int  COUNT_COLUMN_INDEX = 1;
    private final int  CHANGED_COUNT_COLUMN_INDEX = 2;
    private final String[] COLUMN_NAMES = {"Station", "Count", "Change"};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.Integer.class,
        java.lang.Integer.class,
    };

    private final boolean[] CAN_EDIT = {false, true, false};
    
    private int stationCount;

    public InventoryUpdateItemModel() {
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
    
    public int getStationIdIndex() {
        return STATION_COLUMN_INDEX;
    }
    
    public int getCountColumnIndex() {
        return COUNT_COLUMN_INDEX;
    }
    
    public int getChangedCountColumnIndex() {
        return CHANGED_COUNT_COLUMN_INDEX;
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
    
    public String getStationId(int rowIndex) {
        return this.getValueAt(rowIndex, STATION_COLUMN_INDEX).toString();
    }
    
    public int calculateChangedCount(int rowIndex, int currentCount) {
        int count = (int) this.getValueAt(rowIndex, COUNT_COLUMN_INDEX);
        int changedCount = count - currentCount;
        
        this.setValueAt(changedCount, rowIndex, CHANGED_COUNT_COLUMN_INDEX);
        
        return changedCount;
    }
    
    public void setItems(ArrayList<InventoryItemInfo> items) {
        String stationId;
        int count;
        int changedCount;
        
        clearItems();
        
        for(InventoryItemInfo item: items) {
            stationId = item.getStationId();
            count = item.getCount();
            changedCount = item.getChangedCount();
            addItem(stationId, count, changedCount);
        }
    }
    
    private void addItem(String stationId, int availableCount, int changeCount) {
        Object[] data = {stationId, availableCount, changeCount};
        this.addRow(data);
    }
}
