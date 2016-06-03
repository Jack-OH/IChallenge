/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import types.ItemInfo;

/**
 *
 * @author sonmapsi
 */
public class OrderItemModel extends DefaultTableModel {
    private final String MODULE_TAG = "ORDER_ITEM_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int NAME_COLUMN_INDEX = 0;
    private final int ORDERED_COUNT_COLUMN_INDEX = 1;
    private final String[] COLUMN_NAMES = {"Item Name", "Ordered Count"};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.String.class
    };

    private final boolean[] CAN_EDIT = {false, false};
    
    public OrderItemModel() {
        super();

        this.setColumnIdentifiers(COLUMN_NAMES);
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
    
    public boolean refreshOrderItem(ArrayList<ItemInfo> items, ArrayList<String> backorderedItems) {
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh order items");
        }
        
        clearItems();
        
        String name;
        int count;
        String countString;
        
        for (ItemInfo item: items) {
            name = item.getItemName();
            count = item.getCount();
            
            countString = String.valueOf(count);
            if (backorderedItems.contains(name)) {
                 countString += " (backordered)";
            }
            
            addOrderItem(name, countString);
        }
        
        return true;
    }
    
    private void addOrderItem(String name, String count) {
        Object data[] = {name, count};
        this.addRow(data);
    }
}
