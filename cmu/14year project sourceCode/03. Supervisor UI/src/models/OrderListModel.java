/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import types.ItemInfo;
import types.OrderStatus;

/**
 *
 * @author sonmapsi
 */
public class OrderListModel extends DefaultTableModel {
    private final String MODULE_TAG = "ORDER_LIST_MODEL: ";
    private final boolean DEBUG = true;
    
    private final int ID_COLUMN_INDEX = 0;
    private final int USER_ID_COLUMN_INDEX = 1;
    private final int DATE_COLUMN_INDEX = 2;
    private final int STATUS_CONTROL_COLUMN_INDEX = 3;
    private final int ITEMS_DESCRIPTIN_COLUMN_INDEX = 4;
    private final int BACKORDERED_ITEMS_DESCRIPTIN_COLUMN_INDEX = 5;
    //private final String[] COLUMN_NAMES = {"ID", "User ID", "Date", "Status", "", ""};
    private final String[] COLUMN_NAMES = {"ID", "User ID", "", "Status", "", ""};
    private final Class[] TYPES = {
        java.lang.Integer.class,
        java.lang.String.class,
        java.lang.String.class,
        java.lang.String.class,
        java.lang.Object.class,
        java.lang.Object.class
    };

    private final boolean[] CAN_EDIT = {false, false, false, false, false, false};

    public OrderListModel() {
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
    
    public int getDateColumnIndex() {
        return DATE_COLUMN_INDEX;
    }
    
    public int getItemsColumnIndex() {
        return ITEMS_DESCRIPTIN_COLUMN_INDEX;
    }
    
    public int getBackorderedColumnIndex() {
        return BACKORDERED_ITEMS_DESCRIPTIN_COLUMN_INDEX;
    }
    
    public boolean refreshOrderList(OrderFilterEnum selectedFilter) throws ResponseErrorResultException {
        if (DEBUG) {
            System.out.println(MODULE_TAG + "refresh inventories");
        }
        
        clearItems();
        
        ArrayList<Integer> orderList;
        
        orderList = Protocol.getInstance().getOrderList(selectedFilter.toString());
        
        if (orderList == null || orderList.isEmpty()) {
            return false;
        }
        
        for (int id: orderList) {
            OrderStatus orderStatus = Protocol.getInstance().getOrderStatus(id);
            
            if (orderStatus != null) {
                addOrderItem(orderStatus.getId(),
                        orderStatus.getUserId(),
                        orderStatus.getDate(),
                        orderStatus.getStatus(),
                        orderStatus.getItems(),
                        orderStatus.getBackorderedItems());
            }
        }
        
        return true;
    }
    
    private void addOrderItem(int id, String userId, String date, String status, ArrayList<ItemInfo> items, ArrayList<String> backordedItems) {
        Object[] data = {
            id, userId, date, status, items, backordedItems
        };
        
        this.addRow(data);
    }
}
