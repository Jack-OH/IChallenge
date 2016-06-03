/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.Communication;
import models.Model;
import models.Protocol;
import models.ResponseErrorResultException;
import views.MainJFrame;

/**
 *
 * @author sonmapsi
 */
public class Controller {
    private final boolean DEBUG = true;
    private final String DEFAULT_ADDRESS = "127.0.0.1";
    private final int PORT = 9001;
    
    private MainJFrame frame;
    private final Model model;
    private Communication connection;
    private String address;
    
    private final ChangeListener tabChangedListener;
    private final ActionListener warehouseSelectedListener;
    
    private final InventoryController inventoryController;
    private final OrderController orderController;
    private final RobotController robotController;
    
    public Controller() {
        this.frame = new MainJFrame();
        
        frame.addAddressTextFieldActionListener(new AddressTextFieldDocumentListener());
        frame.addConnectButtonActionListener(new ConnectButtonActionListener());
        frame.addDisconnectButtonActionListener(new DisconnectButtonActionListener());
        
        tabChangedListener = new TabChangedChangeListener();
        warehouseSelectedListener = new WarehouseSelectedActionListener();
        //orderController = new OrderController(frame);
        
        frame.setConnectButtonEnabled(true);
                 
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
        setAddress(DEFAULT_ADDRESS);
        frame.setTextFieldAddress(address);
        address = frame.getTextFieldAddress();
        connection = new Communication(address, PORT);
        
        frame.addWindowCloseActionListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection.isConnected()) {
                    orderController.actionBeforeDisconnection();
                    connection.disconnect();
                }
                e.getWindow().dispose();
            }
        });
        
        model = new Model();
        inventoryController = new InventoryController(frame);
        orderController = new OrderController(frame);
        robotController = new RobotController(frame);
    }
    
    public class ConnectButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // Do Network Connection
            if (DEBUG) {
                Logger.getLogger(Communication.class.getName()).log(Level.INFO,
                        "connect to server {0}:{1}",
                        new Object[]{connection.getAddress(), connection.getPort()});
            }
            
            connection = new Communication(address, PORT);
            boolean connectionResult = connection.connect();
            
            if (connectionResult) {
                Protocol.getInstance().setConnection(connection);
                
                try {
                    model.setWarehouseList();
                } catch (ResponseErrorResultException ex) {
                    Protocol.getInstance().setConnection(null);
                    connection.disconnect();
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "setWarehouseList", ex);
                    frame.displayError("Connection Error (Get Warehouse List Error)\nReconnection is needed.\nreason=" + ex.getMessage());
                    return;
                }
                frame.setWarehouseComboBoxModel(model.getWarehouseComboBoxModel());
                
                
                frame.setSelectedWarehouseComboBoxIndex(0);
                frame.setSelectedTabIndex(0);
                
                frame.addWarehouseSelectedActionListener(warehouseSelectedListener);
                frame.addTabChangedChangeListener(tabChangedListener);
                
                Object selecteWarehouseItem = model.getWarehouseComboBoxModel().getSelectedItem();
                
                if (selecteWarehouseItem != null) {
                    int selectedWarehouseId = model.getWarehouseComboBoxModel().getSelectedId(0);
                    int stationCount = model.getWarehouseStationCount(selectedWarehouseId);
                    inventoryController.setSelectedWarehouseId(selectedWarehouseId);
                    inventoryController.setSelectedWarehouseStationCount(stationCount);
                    robotController.setSelectedWarehouseId(selectedWarehouseId);
                    inventoryController.refreshInventory();
                    
                }
                
                inventoryController.setCotentsInvalidated();
                robotController.setCotentsInvalidated();
                orderController.setCotentsInvalidated();
                
                frame.setDisconnectButtonEnabled(true);
                orderController.actionAfterConnection();
            } else {
                frame.displayConnectionError(true, connection.getAddress(), connection.getPort());
            }
        }
    }
    
    private class DisconnectButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // Do Network Disconnection
            if (DEBUG) {
                Logger.getLogger(Communication.class.getName()).log(Level.INFO,
                        "disconnect from server {0}:{1}",
                        new Object[]{connection.getAddress(), connection.getPort()});
            }
            
            orderController.actionBeforeDisconnection();
            boolean disconnectionResult = connection.disconnect();
            
            if (disconnectionResult) {
                Protocol.getInstance().setConnection(null);
                
                frame.removeTabChangedChangeListener(tabChangedListener);
                frame.removeWarehouseSelectedActionListener(warehouseSelectedListener);
                
                frame.setSelectedWarehouseComboBoxIndex(0);
                frame.setSelectedTabIndex(0);
                
                frame.clearAllItemsWarehouseComboBox();
                inventoryController.clearAllData();
                
                frame.setConnectButtonEnabled(true);
            } else {
                frame.displayConnectionError(false, connection.getAddress(), connection.getPort());
                orderController.actionAfterConnection();
            }
        }
    }
    
    private void setAddress(String address) {
        this.address = address;
    }
    
    private class AddressTextFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateAddress();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateAddress();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateAddress();
        }
        
        private void updateAddress() {
            String address = frame.getTextFieldAddress();
            setAddress(address);
        }
    }
    
    private class WarehouseSelectedActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //doSomething();
            int selectedWarehouseComboBoxIndex = frame.getSelectedWarehouseComboBoxIndex();
            int selectedTabIndex = frame.getSelectedTabIndex();
            int selectedWarehouseId = model.getWarehouseComboBoxModel().getSelectedId(selectedWarehouseComboBoxIndex);
            int stationCount = model.getWarehouseStationCount(selectedWarehouseId);
            
            inventoryController.setSelectedWarehouseId(selectedWarehouseId);
            inventoryController.setSelectedWarehouseStationCount(stationCount);
            robotController.setSelectedWarehouseId(selectedWarehouseId);
            
            System.out.println("Whare house selection: " + frame.getSelectedWarehouseComboBoxIndex());
            System.out.println("Whare house selection: " + frame.getSelectedTabIndex());
            if (selectedTabIndex == 0) {
                inventoryController.refreshInventory();
                
                robotController.setCotentsInvalidated();
                orderController.setCotentsInvalidated();
            } else if (selectedTabIndex == 1) {
                robotController.refreshRobotStatus();
                
                inventoryController.setCotentsInvalidated();
                orderController.setCotentsInvalidated();
            } else if (selectedTabIndex == 2) {
                orderController.refreshOrderStatus();
                
                inventoryController.setCotentsInvalidated();
                robotController.setCotentsInvalidated();
            }
        }
    }
    
    private class TabChangedChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            int tabIndex = frame.getSelectedTabIndex();
            System.out.println("Tab: " + tabIndex);

            switch(tabIndex) {
                case 0:
                    inventoryController.tabSelected();
                    break;
                case 1:
                    robotController.tabSelected();
                    break;
                case 2:
                    orderController.tabSelected();
                    break;
                default:
                    break;
            }
        }
    }
}
