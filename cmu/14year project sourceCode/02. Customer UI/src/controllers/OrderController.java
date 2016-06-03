/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import models.InventoryModel;
import models.OrderModel;
import models.ResponseErrorResultException;
import types.InventoryInfo;
import views.MainFrame;

/**
 *
 * @author sonmapsi
 */
public final class OrderController {
    private final String MODULE_TAG = "ORDER_CONTROLLER: ";
    private final boolean DEBUG = true;
    
    MainFrame frame;
    InventoryModel inventoryModel;
    OrderModel orderModel;
    
    public OrderController(MainFrame topFrame) {
        this.frame = topFrame;
        
        initListener();
        
        inventoryModel = new InventoryModel();
        orderModel = new OrderModel();
        
        Action orderAction;
        orderAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = Integer.valueOf(e.getActionCommand());
                
                InventoryInfo inventory = inventoryModel.getSelectedInventory(selectedRowIndex);
                orderModel.addOrderItem(inventory);
            }
        };
        
        Action deleteAction;
        deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = Integer.valueOf(e.getActionCommand());
                
                orderModel.deleteOrderItem(selectedRowIndex);
            }
        };
        
        frame.addUserIdTextFieldActionListener(new UserIdTextFieldDocumentListener());
        frame.setInventoryListTableModel(inventoryModel, orderAction, inventoryModel.getOrderColumnIndex());
        frame.setOrderListTableModel(orderModel, deleteAction, orderModel.getDeleteColumnIndex());
        
        orderModel.addTableModelListener(new orderTableModelListener());
    }
    
    public void initListener() {
        frame.addRefreshButtonActionListener(new RefreshButtonActionListener());
        frame.addClearOrdersButtonActionListener(new ClearOrdersButtonActionListener());
        frame.addSubmitOrdersButtonActionListener(new SubmitOrdersButtonActionListener());
    }
    
    private class UserIdTextFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateUserId();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateUserId();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateUserId();
        }
        
        private void updateUserId() {
            String newUserId = frame.getTextFieldUserID();
            orderModel.setUserId(newUserId);
        }
    }
       
    private class RefreshButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                inventoryModel.refreshInventory();
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "RefreshButtonClicked", ex);
                frame.displayError("Refresh Inventory Information Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class ClearOrdersButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // Do Clear Orders Action
            System.out.println("clear");
            orderModel.deleteAllOrderItems();
            frame.setTextFieldTotalPrice(0);
        }
    }
    
    private class SubmitOrdersButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // Do Submit Orders Action
            boolean submitResult;
            String userId;
            
            userId = frame.getTextFieldUserID();
            if (DEBUG) {
                System.out.println(MODULE_TAG + "Submit order, user=" + userId);
            }
            
            if (userId.equals("")) {
                orderModel.setUserIdToDefaultUserId();
                frame.setTextFieldUserID(orderModel.getDefaultUserId());
            }
            
            try {
                submitResult = orderModel.submitOrders();
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "SubmitButtonClicked", ex);
                frame.displayError("Submit Order Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class orderTableModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            boolean needRecalculateTotalPrice = false;
            
            if (e.getColumn() == orderModel.getCountColumnIndex()
                    && e.getType() == TableModelEvent.UPDATE) {
                System.out.println("order table updated row=" + e.getFirstRow() + ", lastrow=" + e.getLastRow());
                int firstRow = e.getFirstRow();
                int lastRow = e.getLastRow();
                for (int i = firstRow; i <= lastRow; i++) {
                    orderModel.calculateSubTotal(i);
                }
                
                needRecalculateTotalPrice = true;
            } else if (e.getType() == TableModelEvent.INSERT
                    || e.getType() == TableModelEvent.DELETE) {
                needRecalculateTotalPrice = true;
            }
            
            if (needRecalculateTotalPrice) {
                float totalPrice = orderModel.calculateTotalPrice();
                frame.setTextFieldTotalPrice(totalPrice);
            }
            
            if (orderModel.getRowCount() > 0) {
                frame.setClearButtonEnabled(true);
                frame.setSubmitButtonEnabled(true);
            } else {
                frame.setClearButtonEnabled(false);
                frame.setSubmitButtonEnabled(false);
            }
        }
    }
}
