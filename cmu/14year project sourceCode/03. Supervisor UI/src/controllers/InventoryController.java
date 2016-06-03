/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import models.InventoryItemModel;
import models.InventoryModel;
import models.InventoryUpdateItemModel;
import models.ResponseErrorResultException;
import types.InventoryInfo;
import views.MainJFrame;

/**
 *
 * @author sonmapsi
 */
public class InventoryController {
    private final boolean DEBUG = true;
    
    private final InventoryModel inventoryModel;
    private final MainJFrame frame;
    private boolean contentsInvalidated;
    
    public InventoryController(MainJFrame frame) {
        this.frame = frame;
        contentsInvalidated = false;
        inventoryModel = new InventoryModel(1);
        
        this.frame.setInventoryListTableModel(inventoryModel.getInventoryListModel());
        this.frame.setHiddenColumnInventoryListTable(inventoryModel.getInventoryListModel().getItemsColumn());
        this.frame.addInventoryListSelectionListener(new InventoryListSelectionListener());
        
        this.frame.setInventoryItemTableModel(inventoryModel.getInventoryItemModel());
        
        this.frame.addRefreshInventoryButtonActionListener(new RefreshButtonActionListener());
        
        this.frame.addInventoryNameTextFieldActionListener(new InventoryNameTextFieldDocumentListener());
        this.frame.addInventoryDescriptionTextAreaActionListener(new InventoryDescriptionTextAreaDocumentListener());
        this.frame.addInventoryPriceTextFieldActionListener(new InventoryPriceTextFieldDocumentListener());
        this.frame.setInventoryUdateItemTableModel(inventoryModel.getInventoryUpdateItemModel());
        inventoryModel.getInventoryUpdateItemModel().addTableModelListener(new updateItemTableModelListener());
        
        this.frame.addCleanInventoryButtonActionListener(new ClearButtonActionListener());
        this.frame.addUpdateInventoryButtonActionListener(new UpdateButtonActionListener());
    }
    
    public void clearAllData() {
        inventoryModel.getInventoryListModel().clearItems();
        inventoryModel.getInventoryItemModel().clearItems();
        inventoryModel.getInventoryUpdateItemModel().clearItems();
        
        this.frame.setTextFieldInventoryName("");
        this.frame.setTextAreaInventoryDescription("");
        this.frame.setTextFieldInventoryPrice("");
    }
    
    public void tabSelected() {
        if (contentsInvalidated) {
            refreshInventory();
        }
        
        contentsInvalidated = false;
    }
    
    public void setCotentsInvalidated() {
        this.contentsInvalidated = true;
    }
    
    public void setSelectedWarehouseId(int id) {
        inventoryModel.setWarehouseId(id);
    }
    
    public void setSelectedWarehouseStationCount(int stationCount) {
        inventoryModel.setStationCount(stationCount);
    }
    
    private class InventoryListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // Ignore Multiple Event
                int selectedRow = frame.getSelecteRowInInventoryList();
                
                if (selectedRow < 0) {
                    return;
                }
                
                if (DEBUG) {
                    System.out.println("Inventory List Selected: " + selectedRow);
                }
                
                if (selectedRow >= 0) {
                    inventoryModel.refreshInventoryItemList(selectedRow);
                    
                    InventoryInfo inventoryInfo = inventoryModel.getInventoryListModel().getSelectedInventory(selectedRow);
                    inventoryModel.setInventoryInfo(inventoryInfo);
                    if (!frame.getTextFieldInventoryName().equals(inventoryInfo.getName())) {
                        frame.setTextFieldInventoryName(inventoryInfo.getName());
                    }
                    frame.setTextAreaInventoryDescription(inventoryInfo.getDescription());
                    frame.setTextFieldInventoryPrice(Float.toString(inventoryInfo.getPrice()));
                    inventoryModel.getInventoryUpdateItemModel().setItems(inventoryInfo.getItems());
                }
            }
        }
    }
    
    public void refreshInventory() {
        try {
            inventoryModel.refreshInventoryList();

            if (inventoryModel.getInventoryListModel().getRowCount() > 0) {
                frame.setSelectedRowInInventoryList(0);
            }
        } catch (ResponseErrorResultException ex) {
            Logger.getLogger(InventoryController.class.getName()).log(Level.SEVERE, "InventoryListRefresh", ex);
            frame.displayError("Refresh Inventory Information Error\nreason=" + ex.getMessage());
        }
    }
    
    private class RefreshButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            frame.clearSelectedRowInInventoryList();
            refreshInventory();
        }
    }
    
    private class InventoryNameTextFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateInventoryName();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateInventoryName();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateInventoryName();
        }
        
        private void updateInventoryName() {
            String newInventoryName = frame.getTextFieldInventoryName();
            inventoryModel.setInventoryName(newInventoryName);
        }
    }
    
    private class InventoryDescriptionTextAreaDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateInventoryDescription();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateInventoryDescription();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateInventoryDescription();
        }
        
        private void updateInventoryDescription() {
            String newInventoryDescription = frame.getTextAreaInventoryDescription();
            inventoryModel.setInventoryDescription(newInventoryDescription);
        }
    }
    
    private class InventoryPriceTextFieldDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateInventoryPrice();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateInventoryPrice();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateInventoryPrice();
        }
        
        private void updateInventoryPrice() {
            String newInventoryPrice = frame.getTextFieldInventoryPrice();
            if (newInventoryPrice.length() == 0) {
                inventoryModel.setInventoryPrice(0.0f);
            } else {
                inventoryModel.setInventoryPrice(Float.parseFloat(newInventoryPrice));
            }
        }
    }
    
    private class updateItemTableModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getColumn() == inventoryModel.getInventoryUpdateItemModel().getCountColumnIndex()
                    && e.getType() == TableModelEvent.UPDATE) {
                if (DEBUG) {
                    System.out.println("update item table updated row=" + e.getFirstRow() + ", lastrow=" + e.getLastRow());
                }
                
                InventoryUpdateItemModel inventoryUpdateItemModel = inventoryModel.getInventoryUpdateItemModel();
                InventoryItemModel inventoryItemModel = inventoryModel.getInventoryItemModel();
                
                int firstRow = e.getFirstRow();
                int lastRow = e.getLastRow();
                for (int i = firstRow; i <= lastRow; i++) {
                    String stationId = inventoryUpdateItemModel.getStationId(i);
                    int currentCount = inventoryItemModel.getCurrentCount(stationId);
                    int changedCount = inventoryUpdateItemModel.calculateChangedCount(i, currentCount);
                    
                    inventoryModel.changeInventoryItemChangedCount(i, changedCount);
                }
            }
        }
    }
    
    private class ClearButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            int selectedRow = frame.getSelecteRowInInventoryList();

            InventoryInfo inventoryInfo = inventoryModel.getInventoryListModel().getSelectedInventory(selectedRow);
            inventoryInfo.clearAllItemChangedCount();
            inventoryModel.setInventoryInfo(inventoryInfo);
            
            frame.setTextFieldInventoryName(inventoryInfo.getName());
            frame.setTextAreaInventoryDescription(inventoryInfo.getDescription());
            frame.setTextFieldInventoryPrice(Float.toString(inventoryInfo.getPrice()));
            
            inventoryModel.getInventoryUpdateItemModel().setItems(inventoryInfo.getItems());
        }
    }
    
    private class UpdateButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            int selectedIndex = frame.getSelecteRowInInventoryList();
            
            try {
                inventoryModel.updateInventory();
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(InventoryController.class.getName()).log(Level.SEVERE, "UpdateButtonClicked", ex);
                frame.displayError("Update/Add Inventory Error\nreason=" + ex.getMessage());
            }
            
            frame.clearSelectedRowInInventoryList();
            refreshInventory();
            frame.setSelectedRowInInventoryList(selectedIndex);
        }
    }
}
