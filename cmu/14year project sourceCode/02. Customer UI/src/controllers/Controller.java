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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.Communication;
import models.Protocol;
import models.ResponseErrorResultException;
import views.MainFrame;

/**
 *
 * @author sonmapsi
 */
public final class Controller {
    private final boolean DEBUG = true;
    private final String DEFAULT_ADDRESS = "127.0.0.1";
    private final int PORT = 9000;
    
    private MainFrame frame;
    private Communication connection;
    private final OrderController orderController;
    private String address;
    
    public Controller() {
        this.frame = new MainFrame();
        
        frame.addAddressTextFieldActionListener(new AddressTextFieldDocumentListener());
        frame.addConnectButtonActionListener(new ConnectButtonActionListener());
        frame.addDisconnectButtonActionListener(new DisconnectButtonActionListener());

        orderController = new OrderController(frame);
        
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
        connection = new Communication(address, PORT);
        
        frame.addWindowCloseActionListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (connection.isConnected()) {
                    connection.disconnect();
                }
                e.getWindow().dispose();
            }
        });
    }
    
    private class ConnectButtonActionListener implements ActionListener {
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
                    orderController.inventoryModel.refreshInventory();
                } catch (ResponseErrorResultException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    frame.displayError("Refresh Inventory Error\nreason=" + ex.getMessage());
                }
                orderController.orderModel.deleteAllOrderItems();
                frame.setDisconnectButtonEnabled(true);
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
            boolean disconnectionResult = connection.disconnect();
            
            if (disconnectionResult) {
                Protocol.getInstance().setConnection(null);
                frame.setConnectButtonEnabled(true);
                orderController.inventoryModel.clearItems();
                orderController.orderModel.deleteAllOrderItems();
            } else {
                frame.displayConnectionError(false, connection.getAddress(), connection.getPort());
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
}
