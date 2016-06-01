/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import types.ItemInfo;
import types.OrderStatus;

/**
 *
 * @author sonmapsi
 */
public class OrderModel {
    private final boolean DEBUG = true;
    
    private final OrderListModel orderListModel;
    private final OrderItemModel orderItemModel;
    private OrderFilterEnum orderFilter;
    
    public OrderModel() {
        orderListModel = new OrderListModel();
        orderItemModel = new OrderItemModel();
        
        orderFilter = OrderFilterEnum.ALL;
    }
    
    public OrderListModel getOrderListModel() {
        return orderListModel;
    }
    
    public OrderItemModel getOrderItemModel() {
        return orderItemModel;
    }
    
    public void setOrderListFilter(OrderFilterEnum orderFilter) {
        this.orderFilter = orderFilter;
    }
    
    public boolean refreshOrderStatus() throws ResponseErrorResultException {
        return orderListModel.refreshOrderList(orderFilter);
    }
    
    public void refreshOrderItemList(int selectedRowInOrderList) {
        ArrayList<ItemInfo> items = (ArrayList<ItemInfo>) orderListModel.getValueAt(selectedRowInOrderList, orderListModel.getItemsColumnIndex());
        ArrayList<String> backOrderedItems = (ArrayList<String>) orderListModel.getValueAt(selectedRowInOrderList, orderListModel.getBackorderedColumnIndex());
        orderItemModel.refreshOrderItem(items, backOrderedItems);
    }
    
    public void clearItems() {
        orderListModel.clearItems();
        orderItemModel.clearItems();
    }
    
    public boolean checkBackordered() {
        boolean backordered;
        
        try {
            backordered = Protocol.getInstance().checkBackOrdered();
        } catch (ResponseErrorResultException ex) {
            Logger.getLogger(OrderModel.class.getName()).log(Level.SEVERE, "checkBackordered", ex);
            backordered = false;
        }
        
        return backordered;
    }
    
    public ArrayList<Integer> getBackorderedList() {
        ArrayList<Integer> backorderedList = new ArrayList<>();
        ArrayList<Integer> orderList;
        try {
            orderList = Protocol.getInstance().getOrderList(OrderFilterEnum.BACKORDERED.toString());

            if (orderList == null || orderList.isEmpty()) {
                return backorderedList;
            }

            for (int id : orderList) {

                OrderStatus orderStatus = Protocol.getInstance().getOrderStatus(id);

                if ("backordered".equals(orderStatus.getStatus())) {
                    backorderedList.add(orderStatus.getId());
                }
            }
        } catch (ResponseErrorResultException ex) {
            Logger.getLogger(OrderModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return backorderedList;
    }
}
