/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import types.InventoryInfo;

/**
 *
 * @author sonmapsi
 */
public final class InventoryModel extends DefaultTableModel {
    private final String MODULE_TAG = "INVENTORY_MODEL: ";
    private final boolean DEBUG = true;
    
    private final String ORDER_COLUMN_STRING = "Order";
    private final int  NAME_COLUMN_INDEX = 0;
    private final int  DESCRIPTION_COLUMN_INDEX = 2;
    private final int  PRICE_COLUMN_INDEX = 2;
    private final int  AVAILABLE_COUNT_COLUMN_INDEX = 3;
    private final int  ORDER_COLUMN_INDEX = 4;
    private final String[] COLUMN_NAMES = {"Name", "Description", "Price ($)", "Availablie Count", ""};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.String.class,
        java.lang.Float.class,
        java.lang.Integer.class,
        java.lang.Object.class
    };

    private final boolean[] CAN_EDIT = {false, false, false, false, true};

    public InventoryModel() {
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
    
    public int getOrderColumnIndex() {
        return ORDER_COLUMN_INDEX;
    }
    
    public void clearItems() {
        int rowCount = this.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            this.removeRow(i);
        }
    }
    
    public void refreshInventory() throws ResponseErrorResultException {
        // TODO: submit orders to server
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh inventories");
        }
        
        clearItems();
        
        ArrayList<String> itemList;
        
        itemList = Protocol.getInstance().getItemList();
        
        for (String name: itemList) {
            InventoryInfo itemInfo = Protocol.getInstance().getItemInfo(name);
            
            if (itemInfo != null) {
                addInventoryItem(name,
                        itemInfo.getDescription(),
                        itemInfo.getPrice(),
                        itemInfo.getCount());
            }
        }
    }
    
    public InventoryInfo getSelectedInventory(int rowIndex) {
        String name = this.getValueAt(rowIndex, NAME_COLUMN_INDEX).toString();
        String description = this.getValueAt(rowIndex, DESCRIPTION_COLUMN_INDEX).toString();
        float price = (float) this.getValueAt(rowIndex, PRICE_COLUMN_INDEX);
        int availCount = (int) this.getValueAt(rowIndex, AVAILABLE_COUNT_COLUMN_INDEX);
        
        InventoryInfo inventory = new InventoryInfo(name, description, price, availCount);
        
        return inventory;
    }

    private void addInventoryItem(String name, String description, float price, int count) {
        Object[] data = new Object[] {name, description, price, count, ORDER_COLUMN_STRING};
        this.addRow(data);
    }
}