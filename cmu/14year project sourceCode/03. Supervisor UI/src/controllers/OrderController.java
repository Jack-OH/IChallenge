/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import models.OrderFilterEnum;
import models.OrderModel;
import models.ResponseErrorResultException;
import views.MainJFrame;

/**
 *
 * @author sonmapsi
 */
public class OrderController {
    private final boolean DEBUG = true;
    private final OrderModel orderModel;
    private final MainJFrame frame;
    
    private final int BACKORDER_CHECK_PERIOD = 3000; // 3s
    private Thread backorderCheckThread;
    private BackorderCheckThread backorderCheckRun;
    private final BackorderObserber backorderObserver;
    
    private boolean contentsInvalidated;
    
    public OrderController(MainJFrame frame) {
        this.frame = frame;
        orderModel = new OrderModel();
        contentsInvalidated = true;
        
        frame.setOrderListTableModel(orderModel.getOrderListModel());
        frame.setOrderItemsTableModel(orderModel.getOrderItemModel());
        
        frame.setHiddenColumnOrderListTable(orderModel.getOrderListModel().getDateColumnIndex());
        frame.setHiddenColumnOrderListTable(orderModel.getOrderListModel().getItemsColumnIndex());
        frame.setHiddenColumnOrderListTable(orderModel.getOrderListModel().getBackorderedColumnIndex());
        
        frame.setOrderFilterButtonGroupActionCommand(OrderFilterEnum.values());
        frame.addOrderFilterButtonGroupActionListener(new FilterButtonGroupActionListener());
        
        frame.addRefreshOrderListButtonActionListener(new RefreshButtonActionListener());
        
        frame.addOrderListSelectionListener(new OrderListSelectionListener());
        
        // TODO: Order List filter is no implemented.
        frame.setOrderFilterButtonGroupVisible(false);
        
        //backorderCheckRun = new BackorderCheckThread(orderModel, BACKORDER_CHECK_PERIOD);
        backorderObserver = new BackorderObserber();
    }
    
    public void tabSelected() {
        if (contentsInvalidated) {
            refreshOrderStatus();
        }
    }
    
    public void actionAfterConnection() {
        startBackorderCheck();
    }
    
    public void actionBeforeDisconnection() {
        stopBackorderCheck();
    }
    
    public void refreshOrderStatus() {
        boolean result;
        frame.clearSelectedRowInRobotList();
        orderModel.clearItems();

        try {
            result = orderModel.refreshOrderStatus();

            if (orderModel.getOrderListModel().getRowCount() > 0) {
                frame.setSelectedRowInOrderList(0);
            }

            if (result) {
                contentsInvalidated = false;
            }
        } catch (ResponseErrorResultException ex) {
            Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, "refreshOrderStatus", ex);
            frame.displayError("Refresh Order Status Error\nreason=" + ex.getMessage());
        }
    }
    
    public void setCotentsInvalidated() {
        this.contentsInvalidated = true;
    }
    
    private class FilterButtonGroupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            String cmd = evt.getActionCommand();
            OrderFilterEnum selectedFilter = OrderFilterEnum.ALL;
            
            if (cmd.equals(OrderFilterEnum.ALL.toString())) {
                selectedFilter = OrderFilterEnum.ALL;
            } else if (cmd.equals(OrderFilterEnum.PENDING.toString())) {
                selectedFilter = OrderFilterEnum.PENDING;
            } else if (cmd.equals(OrderFilterEnum.INPROGRESS.toString())) {
                selectedFilter = OrderFilterEnum.INPROGRESS;
            } else if (cmd.equals(OrderFilterEnum.BACKORDERED.toString())) {
                selectedFilter = OrderFilterEnum.BACKORDERED;
            } else if (cmd.equals(OrderFilterEnum.COMPLETE.toString())) {
                selectedFilter = OrderFilterEnum.COMPLETE;
            }
            
            orderModel.setOrderListFilter(selectedFilter);
        }
    }
    
    private class RefreshButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            refreshOrderStatus();
        }
    }
    
    private class OrderListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // Ignore Multiple Event
                int selectedRow = frame.getSelecteRowInOrderList();
                
                if (DEBUG) {
                    System.out.println("Order List Selected: " + selectedRow);
                }
                
                if (selectedRow >= 0) {
                    orderModel.refreshOrderItemList(selectedRow);
                }
            }
        }
    }
    
    public void startBackorderCheck() {
        backorderCheckRun = new BackorderCheckThread(orderModel, BACKORDER_CHECK_PERIOD);
        backorderCheckThread = new Thread(backorderCheckRun);
        backorderCheckRun.addObserver(backorderObserver);
        backorderCheckThread.start();
    }
    
    public void stopBackorderCheck() {
        
        backorderCheckRun.setStop();
        backorderCheckThread.interrupt();
        backorderCheckRun.deleteObserver(backorderObserver);
    }
    
    private void performBackorderDetected() {
        System.out.println("Backorder Occured");
        String msg  = "Backorder is occured.\n"
                + "Please check below orders in order tab.\n";
        ArrayList<Integer> backorderedOrderIds = orderModel.getBackorderedList();
        
        if (backorderedOrderIds.size() > 0) {
            msg += "Order ID: " + backorderedOrderIds.toString();
        }
        
        frame.displayWarning(msg);
    }
    
    private class BackorderObserber implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            performBackorderDetected();
        }
    }
}
