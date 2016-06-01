/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import javax.swing.table.DefaultTableModel;
import types.InventoryInfo;
import types.OrderInfo;

/**
 *
 * @author sonmapsi
 */
public final class OrderModel extends DefaultTableModel {
    private final String MODULE_TAG = "ORDER_MODEL: ";
    private final boolean DEBUG = true;
    
    private final String DEFAULT_USER_ID = "Lucky Guys";

    private final String DELETE_COLUMN_STRING = "Delete";
    private final int  NAME_COLUMN_INDEX = 0;
    private final int  PRICE_COLUMN_INDEX = 1;
    private final int  SUBTOTAL_COLUMN_INDEX = 2;
    private final int  COUNT_COLUMN_INDEX = 3;
    private final int  DELETE_COLUMN_INDEX = 4;
    private final String[] COLUMN_NAMES = {"Name", "Price ($)", "SubTotal ($)", "Count", ""};
    private final Class[] TYPES = {
        java.lang.String.class,
        java.lang.Float.class,
        java.lang.Float.class,
        java.lang.Integer.class,
        java.lang.Object.class
    };
    private final boolean[] CAN_EDIT = {false, false, false, true, true};

    private String userId;
    
    public OrderModel() {
        super();
        
        this.setUserId(DEFAULT_USER_ID);

        this.setColumnIdentifiers(COLUMN_NAMES);
    }
    
    public String getDefaultUserId() {
        return DEFAULT_USER_ID;
    }
    
    public void setUserIdToDefaultUserId() {
        this.userId = DEFAULT_USER_ID;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return this.userId;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return CAN_EDIT[columnIndex];
    }
    
    public int getDeleteColumnIndex() {
        return DELETE_COLUMN_INDEX;
    }
    
    public int getCountColumnIndex() {
        return COUNT_COLUMN_INDEX;
    }
    
    public void calculateSubTotal(int rowIndex) {
        float price = (float) this.getValueAt(rowIndex, PRICE_COLUMN_INDEX);
        int count = (int) this.getValueAt(rowIndex, COUNT_COLUMN_INDEX);
        
        float subTotal = price * (float) count;
        
        this.setValueAt(subTotal, rowIndex, SUBTOTAL_COLUMN_INDEX);
    }
    
    public float calculateTotalPrice() {
        int rowCount = this.getRowCount();
        float totalPrice = (float) 0.0;
        
        for (int i = 0; i < rowCount; i++) {
            totalPrice += (float) this.getValueAt(i, SUBTOTAL_COLUMN_INDEX);
        }
        
        return totalPrice;
    }
    
    public void deleteAllOrderItems() {
        int rowCount = this.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            this.removeRow(i);
        }
    }
    
    public void deleteOrderItem(int rowIndex) {
        this.removeRow(rowIndex);
    }
    
    public void addOrderItem(InventoryInfo inventory) {
        String name = inventory.getName();
        float price = inventory.getPrice();
        
        this.addOrderItem(name, price, price, 1);
    }

    public void addOrderItem(String name, float price, float subTotal, int count) {
        Object[] data = new Object[] {name, price, subTotal, count, DELETE_COLUMN_STRING};
        this.addRow(data);
    }
    
    public boolean submitOrders() throws ResponseErrorResultException {
        // TODO: submit orders to server
        if (DEBUG) {
            System.out.println(MODULE_TAG + "submit orders" + ", count=" + this.getRowCount());
        }
        
        int rowCount = this.getRowCount();
        OrderInfo orderInfo = new OrderInfo();
        String name;
        int count;
        
        for (int i = 0; i < rowCount; i++ ) {
            name = this.getValueAt(i, NAME_COLUMN_INDEX).toString();
            count = (int) this.getValueAt(i, COUNT_COLUMN_INDEX);
            orderInfo.put(name, count);
        }
        
        boolean orderResult;
        orderResult = Protocol.getInstance().makeOrder(this.userId, orderInfo);

        return orderResult;
    }
}