/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package views;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sonmapsi
 */
public class MainFrame extends javax.swing.JFrame {
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
    }
    
    public void setInventoryListTableModel(DefaultTableModel model, Action action, int orderColumnIndex) {        
        jTableInventoryList.setModel(model);
        
        ButtonColumn buttonColumnAdd;
        buttonColumnAdd = new ButtonColumn(jTableInventoryList, action, orderColumnIndex);
        buttonColumnAdd.setMnemonic(KeyEvent.VK_A);
    }
    
    public void setOrderListTableModel(DefaultTableModel model, Action action, int deleteColumnIndex) {        
        jTableOrderList.setModel(model);
        
        ButtonColumn buttonColumnDelete;
        buttonColumnDelete = new ButtonColumn(jTableOrderList, action, deleteColumnIndex);
        buttonColumnDelete.setMnemonic(KeyEvent.VK_D);
    }
    
    public void addUserIdTextFieldActionListener(DocumentListener listener) {
        jTextFieldUserID.getDocument().addDocumentListener(listener);
    }
    
    public void addConnectButtonActionListener(ActionListener listener) {
        jButtonConnect.addActionListener(listener);
    }
    
    public void addDisconnectButtonActionListener(ActionListener listener) {
        jButtonDisconnect.addActionListener(listener);
    }
    
    public void addRefreshButtonActionListener(ActionListener listener) {
        jButtonInventoryListRefresh.addActionListener(listener);
    }
    
    public void addClearOrdersButtonActionListener(ActionListener listener) {
        jButtonClearOrders.addActionListener(listener);
    }
    
    public void addSubmitOrdersButtonActionListener(ActionListener listener) {
        jButtonSubmitOrders.addActionListener(listener);
    }
    
    public void addWindowCloseActionListener(WindowAdapter listener) {
        this.addWindowListener(listener);
        System.out.println("window listener is added");
    }
    
    public void setDisconnectButtonEnabled(boolean enabled) {
        jButtonDisconnect.setEnabled(enabled);
        jButtonConnect.setEnabled(!enabled);
        
        jTableInventoryList.setEnabled(enabled);
        jButtonInventoryListRefresh.setEnabled(enabled);
        jTableOrderList.setEnabled(enabled);
        jButtonClearOrders.setEnabled(enabled);
        jButtonSubmitOrders.setEnabled(enabled);
        jTextFieldAddress.setEnabled(!enabled);
    }
    
    public void setConnectButtonEnabled(boolean enabled) {
        jButtonConnect.setEnabled(enabled);
        jButtonDisconnect.setEnabled(!enabled);
        
        jTableInventoryList.setEnabled(!enabled);
        jButtonInventoryListRefresh.setEnabled(!enabled);
        jTableOrderList.setEnabled(!enabled);
        jButtonClearOrders.setEnabled(!enabled);
        jButtonSubmitOrders.setEnabled(!enabled);
        jTextFieldAddress.setEnabled(enabled);
    }
    
    public void setClearButtonEnabled(boolean enabled) {
        jButtonClearOrders.setEnabled(enabled);
    }
    
    public void setSubmitButtonEnabled(boolean enabled) {
        jButtonSubmitOrders.setEnabled(enabled);
    }
    
    public String getTextFieldUserID() {
        return jTextFieldUserID.getText().trim();
    }
    
    public void setTextFieldUserID(String userId) {
        jTextFieldUserID.setText(userId);
    }
    
    public void setTextFieldTotalPrice(float totalPrice) {
        jTextFieldTotalPrice.setText(String.valueOf(totalPrice));
    }
    
    public void setTextFieldAddress(String address) {
        jTextFieldAddress.setText(address);
    }
    
    public String getTextFieldAddress() {
        return jTextFieldAddress.getText().trim();
    }
    
    public void addAddressTextFieldActionListener(DocumentListener listener) {
        jTextFieldAddress.getDocument().addDocumentListener(listener);
    }
    
    public void displayConnectionError(boolean isConnect, String address, int port) {
        String msg = (isConnect ? "connection to" : "disconnection from")
                + " address=" + address
                + ", port=" + port + " fail";
        
        JOptionPane.showMessageDialog(this, msg, "connection/disconnection", JOptionPane.ERROR_MESSAGE);
    }
    
    public void displayError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "connection/disconnection", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelUserID = new javax.swing.JLabel();
        jTextFieldUserID = new javax.swing.JTextField();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jLayeredPaneOrderTab = new javax.swing.JLayeredPane();
        jLabelCurrentInventory = new javax.swing.JLabel();
        jButtonInventoryListRefresh = new javax.swing.JButton();
        jLabelCurrentOrders = new javax.swing.JLabel();
        jScrollPaneOrderList = new javax.swing.JScrollPane();
        jTableOrderList = new javax.swing.JTable();
        jButtonClearOrders = new javax.swing.JButton();
        jButtonSubmitOrders = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelTotalPrice = new javax.swing.JLabel();
        jTextFieldTotalPrice = new javax.swing.JTextField();
        jScrollPaneInventoryList = new javax.swing.JScrollPane();
        jTableInventoryList = new javax.swing.JTable();
        jLabelPriceUnit = new javax.swing.JLabel();
        jButtonConnect = new javax.swing.JButton();
        jButtonDisconnect = new javax.swing.JButton();
        jTextFieldAddress = new javax.swing.JTextField();
        jLabelAddress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Customer (Lucky Guys WMS)");
        setBounds(new java.awt.Rectangle(0, 22, 614, 500));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(614, 500));
        setPreferredSize(new java.awt.Dimension(614, 500));

        jLabelUserID.setText("User ID:");

        jTextFieldUserID.setText("Lucky Guys");

        jLabelCurrentInventory.setText("Current Inventory:");

        jButtonInventoryListRefresh.setText("Refresh");

        jLabelCurrentOrders.setText("Current Orders:");

        jTableOrderList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableOrderList.setRowSelectionAllowed(false);
        jTableOrderList.getTableHeader().setReorderingAllowed(false);
        jScrollPaneOrderList.setViewportView(jTableOrderList);

        jButtonClearOrders.setText("Clear");

        jButtonSubmitOrders.setText("Submit");

        jLabelTotalPrice.setText("Total Price:");

        jTextFieldTotalPrice.setEditable(false);
        jTextFieldTotalPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldTotalPrice.setText("0");
        jTextFieldTotalPrice.setFocusable(false);
        jTextFieldTotalPrice.setName(""); // NOI18N
        jTextFieldTotalPrice.setRequestFocusEnabled(false);
        jTextFieldTotalPrice.setVerifyInputWhenFocusTarget(false);

        jTableInventoryList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableInventoryList.setRowSelectionAllowed(false);
        jTableInventoryList.getTableHeader().setReorderingAllowed(false);
        jScrollPaneInventoryList.setViewportView(jTableInventoryList);

        jLabelPriceUnit.setText("$");

        javax.swing.GroupLayout jLayeredPaneOrderTabLayout = new javax.swing.GroupLayout(jLayeredPaneOrderTab);
        jLayeredPaneOrderTab.setLayout(jLayeredPaneOrderTabLayout);
        jLayeredPaneOrderTabLayout.setHorizontalGroup(
            jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneOrderTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneOrderList, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneOrderTabLayout.createSequentialGroup()
                        .addGap(0, 478, Short.MAX_VALUE)
                        .addComponent(jButtonInventoryListRefresh))
                    .addGroup(jLayeredPaneOrderTabLayout.createSequentialGroup()
                        .addGroup(jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCurrentOrders)
                            .addComponent(jLabelCurrentInventory))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneOrderTabLayout.createSequentialGroup()
                        .addComponent(jLabelTotalPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTotalPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPriceUnit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonClearOrders)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSubmitOrders))
                    .addComponent(jScrollPaneInventoryList, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jLayeredPaneOrderTabLayout.setVerticalGroup(
            jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPaneOrderTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCurrentInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneInventoryList, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPaneOrderTabLayout.createSequentialGroup()
                        .addComponent(jButtonInventoryListRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelCurrentOrders)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneOrderList, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addGroup(jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonSubmitOrders)
                            .addComponent(jButtonClearOrders)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPaneOrderTabLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jLayeredPaneOrderTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelTotalPrice)
                            .addComponent(jTextFieldTotalPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPriceUnit))))
                .addContainerGap())
        );
        jLayeredPaneOrderTab.setLayer(jLabelCurrentInventory, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jButtonInventoryListRefresh, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jLabelCurrentOrders, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jScrollPaneOrderList, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jButtonClearOrders, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jButtonSubmitOrders, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jSeparator1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jLabelTotalPrice, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jTextFieldTotalPrice, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jScrollPaneInventoryList, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPaneOrderTab.setLayer(jLabelPriceUnit, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jTabbedPaneMain.addTab("Order", jLayeredPaneOrderTab);

        jButtonConnect.setText("Connect");

        jButtonDisconnect.setText("Disconnect");
        jButtonDisconnect.setEnabled(false);
        jButtonDisconnect.setFocusable(false);

        jLabelAddress.setText("Address:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelUserID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldUserID, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDisconnect)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUserID)
                    .addComponent(jTextFieldUserID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConnect)
                    .addComponent(jButtonDisconnect)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(jTabbedPaneMain, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClearOrders;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonDisconnect;
    private javax.swing.JButton jButtonInventoryListRefresh;
    private javax.swing.JButton jButtonSubmitOrders;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JLabel jLabelCurrentInventory;
    private javax.swing.JLabel jLabelCurrentOrders;
    private javax.swing.JLabel jLabelPriceUnit;
    private javax.swing.JLabel jLabelTotalPrice;
    private javax.swing.JLabel jLabelUserID;
    private javax.swing.JLayeredPane jLayeredPaneOrderTab;
    private javax.swing.JScrollPane jScrollPaneInventoryList;
    private javax.swing.JScrollPane jScrollPaneOrderList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTable jTableInventoryList;
    private javax.swing.JTable jTableOrderList;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldTotalPrice;
    private javax.swing.JTextField jTextFieldUserID;
    // End of variables declaration//GEN-END:variables

}