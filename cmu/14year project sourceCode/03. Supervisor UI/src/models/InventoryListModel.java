/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import types.InventoryInfo;
import types.InventoryItemInfo;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sonmapsi
 */
public class InventoryListModel extends DefaultTableModel {
    private final String MODULE_TAG = "INVENTORY_LIST_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int NAME_COLUMN_INDEX = 0;
    private final int DESCRIPTION_COLUMN_INDEX = 1;
    private final int PRICE_COLUMN_INDEX = 2;
    private final int ITEMS_COLUMN_INDEX = 3;
    private final String[] COLUMN_NAMES = {"Name", "Description", "Price ($)", ""};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.String.class,
        java.lang.Float.class,
        java.lang.Object.class
    };

    private final boolean[] CAN_EDIT = {false, false, false, false};
    
    private int warehouseId;
    private int stationCount;

    public InventoryListModel(int warehouseId) {
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
    
    public int getItemsColumn() {
        return ITEMS_COLUMN_INDEX;
    }
    
    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }
    
    public boolean refreshInventory() throws ResponseErrorResultException {
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh inventories");
        }
        
        clearItems();
        
        ArrayList<String> inventoryList;
        
        inventoryList = Protocol.getInstance().getInventoryList(warehouseId);
        
        if (inventoryList == null || inventoryList.isEmpty()) {
            return false;
        }
        
        for (String name: inventoryList) {
            InventoryInfo inventoryInfo = Protocol.getInstance().getInventoryInfo(warehouseId, name);
            
            if (inventoryInfo != null) {
                addInventroyItem(inventoryInfo.getName(),
                        inventoryInfo.getDescription(),
                        inventoryInfo.getPrice(), refineItems(inventoryInfo.getItems()));
            }
        }
        
        return true;
    }
    
    private ArrayList<InventoryItemInfo> refineItems(ArrayList<InventoryItemInfo> items) {
        String stationId;
        
        ArrayList<String> curStations = new ArrayList<>();
        ArrayList<InventoryItemInfo> curItems = new ArrayList<>();
        for (int i = 1; i < stationCount; i++) {
            curStations.add(String.valueOf(i));
            curItems.add(new InventoryItemInfo(String.valueOf(i), 0));
        }
        
        for (InventoryItemInfo item: items) {
            stationId = item.getStationId();
            if (curStations.contains(stationId)) {
                int index = curStations.indexOf(stationId);
                curItems.set(index, new InventoryItemInfo(stationId, item.getCount()));
            }
        }
        
        return curItems;
    }
    
    public InventoryInfo getSelectedInventory(int rowIndex) {
        String name = this.getValueAt(rowIndex, NAME_COLUMN_INDEX).toString();
        String description = this.getValueAt(rowIndex, DESCRIPTION_COLUMN_INDEX).toString();
        float price = (float) this.getValueAt(rowIndex, PRICE_COLUMN_INDEX);
        ArrayList<InventoryItemInfo> items = (ArrayList<InventoryItemInfo>) this.getValueAt(rowIndex, ITEMS_COLUMN_INDEX);
        
        InventoryInfo inventoryInfo = new InventoryInfo(warehouseId, name, description, price, items);
        
        return inventoryInfo;
    }
    
    public int getInventoryIndexWithName(String name) {
        String curName;
        int searchedIndex = -1;
        
        for (int i = 0; i < this.getRowCount(); i++) {
            curName = this.getValueAt(i, NAME_COLUMN_INDEX).toString();
            if (name.equals(curName)) {
                searchedIndex = i;
                break;
            }
        }
        
        return searchedIndex;
    }
    
    private void addInventroyItem(String name, String description, float price, ArrayList<InventoryItemInfo> items) {
        Object[] data = new Object[] {name, description, price, items};
        this.addRow(data);
    }
}
