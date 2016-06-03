/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package views;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import models.OrderFilterEnum;

/**
 *
 * @author sonmapsi
 */
public class MainJFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        initComponents();
    }
    
    // Main Frame    
    public void addWindowCloseActionListener(WindowAdapter listener) {
        this.addWindowListener(listener);
        System.out.println("window listener is added");
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
    
    public void addConnectButtonActionListener(ActionListener listener) {
        jButtonConnect.addActionListener(listener);
    }
    
    public void addDisconnectButtonActionListener(ActionListener listener) {
        jButtonDisconnect.addActionListener(listener);
    }
    
    public void addWarehouseSelectedActionListener(ActionListener listener) {
        jComboBoxWarehouse.addActionListener(listener);
    }
    
    public void removeWarehouseSelectedActionListener(ActionListener listener) {
        jComboBoxWarehouse.removeActionListener(listener);
    }
    
    public void setWarehouseComboBoxModel(DefaultComboBoxModel model) {
        jComboBoxWarehouse.setModel(model);
    }
    
    public void setSelectedWarehouseComboBoxIndex(int index) {
        jComboBoxWarehouse.setSelectedIndex(index);
    }
    
    public int getSelectedWarehouseComboBoxIndex() {
        return jComboBoxWarehouse.getSelectedIndex();
    }
    
    public void clearAllItemsWarehouseComboBox() {
        jComboBoxWarehouse.removeAllItems();
    }
    
    public void setSelectedTabIndex(int index) {
        jTabbedPaneMain.setSelectedIndex(index);
    }
    
    public int getSelectedTabIndex() {
        return jTabbedPaneMain.getSelectedIndex();
    }
    
    public void addTabChangedChangeListener(ChangeListener listener) {
        jTabbedPaneMain.addChangeListener(listener);
    }
    
    public void removeTabChangedChangeListener(ChangeListener listener) {
        jTabbedPaneMain.removeChangeListener(listener);
    }
    
    public void setConnectButtonEnabled(boolean enabled) {
        // if enabled, disconnected status
        // if disabled, connected status
        jButtonDisconnect.setEnabled(!enabled);
        jButtonConnect.setEnabled(enabled);
        
        jTabbedPaneMain.setEnabled(!enabled);
        jTextFieldAddress.setEnabled(enabled);
        jComboBoxWarehouse.setEnabled(!enabled);
        
        // Inventory Tab
        jTableInventory.setEnabled(!enabled);
        jTableInventoryCurrentItems.setEnabled(!enabled);
        jButtonRefreshInventory.setEnabled(!enabled);
        //jTextFieldInventoryName.setEnabled(!enabled);
        jTextFieldInventoryName.setEnabled(false);
        jTextAreaInventoryDescription.setEditable(!enabled);
        jTextFieldInventoryPrice.setEditable(!enabled);
        jTableInventoryUpdateItems.setEnabled(!enabled);
        jButtonCleanInventory.setEnabled(!enabled);
        jButtonUpdateInventory.setEnabled(!enabled);
        
        // Robot Tab
        jTableRobotStatusList.setEnabled(!enabled);
        jButtonRobotRefresh.setEnabled(!enabled);
        jToggleButtonRobotManualControlOnOff.setEnabled(!enabled);
        jButtonRobotMoveFoward.setEnabled(!enabled);
        jButtonRobotMoveBackward.setEnabled(!enabled);
        jButtonRobotMoveLeftTurn.setEnabled(!enabled);
        jButtonRobotMoveRightTurn.setEnabled(!enabled);
        
        // Order Tab
        jRadioButtonOrderAll.setEnabled(!enabled);
        jRadioButtonOrderPending.setEnabled(!enabled);
        jRadioButtonInProgress.setEnabled(!enabled);
        jRadioButtonOrderBackordered.setEnabled(!enabled);
        jRadioButtonOrderComplete.setEnabled(!enabled);
        jTableOrderList.setEnabled(!enabled);
        jTableOrderItems.setEnabled(!enabled);
        jButtonRefreshOrderList.setEnabled(!enabled);
    }
    
    public void setDisconnectButtonEnabled(boolean enabled) {
        // if enabled, connected status
        // if disabled, disconnected status
        jButtonDisconnect.setEnabled(enabled);
        jButtonConnect.setEnabled(!enabled);
        
        jTabbedPaneMain.setEnabled(enabled);
        jTextFieldAddress.setEnabled(!enabled);
        jComboBoxWarehouse.setEnabled(enabled);
        
        // Inventory Tab
        jTableInventory.setEnabled(enabled);
        jTableInventoryCurrentItems.setEnabled(enabled);
        jButtonRefreshInventory.setEnabled(enabled);
        //jTextFieldInventoryName.setEnabled(enabled);
        jTextFieldInventoryName.setEnabled(false);
        jTextAreaInventoryDescription.setEditable(enabled);
        jTextFieldInventoryPrice.setEditable(enabled);
        jTableInventoryUpdateItems.setEnabled(enabled);
        jButtonCleanInventory.setEnabled(enabled);
        jButtonUpdateInventory.setEnabled(enabled);
        
        // Robot Tab
        jTableRobotStatusList.setEnabled(enabled);
        jButtonRobotRefresh.setEnabled(enabled);
        jToggleButtonRobotManualControlOnOff.setEnabled(enabled);
        jButtonRobotMoveFoward.setEnabled(enabled);
        jButtonRobotMoveBackward.setEnabled(enabled);
        jButtonRobotMoveLeftTurn.setEnabled(enabled);
        jButtonRobotMoveRightTurn.setEnabled(enabled);
        
        // Order Tab
        jRadioButtonOrderAll.setEnabled(enabled);
        jRadioButtonOrderPending.setEnabled(enabled);
        jRadioButtonInProgress.setEnabled(enabled);
        jRadioButtonOrderBackordered.setEnabled(enabled);
        jRadioButtonOrderComplete.setEnabled(enabled);
        jTableOrderList.setEnabled(enabled);
        jTableOrderItems.setEnabled(enabled);
        jButtonRefreshOrderList.setEnabled(enabled);
    }
    
    // Inventory Tab
    public void setInventoryListTableModel(DefaultTableModel model) {        
        jTableInventory.setModel(model);
    }
       
    public void addInventoryListSelectionListener(ListSelectionListener listener) {
        jTableInventory.getSelectionModel().addListSelectionListener(listener);
    }
    
    public int getSelecteRowInInventoryList() {
        return jTableInventory.getSelectedRow();
    }
    
    public void setSelectedRowInInventoryList(int rowIndex) {
        jTableInventory.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
    }
    
    public void setFocusToInventoryList() {
        jTableInventory.requestFocusInWindow();
    }
    
    public void clearSelectedRowInInventoryList() {
        jTableInventory.getSelectionModel().clearSelection();
    }
    
    public void setHiddenColumnInventoryListTable(int columnIndex) {
        jTableInventory.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        jTableInventory.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
    }
    
    public void setInventoryItemTableModel(DefaultTableModel model) {
        jTableInventoryCurrentItems.setModel(model);
    }
    
    public void addRefreshInventoryButtonActionListener(ActionListener listener) {
        jButtonRefreshInventory.addActionListener(listener);
    }
    
    public String getTextFieldInventoryName() {
        return jTextFieldInventoryName.getText().trim();
    }
    
    public void setTextFieldInventoryName(String inventoryName) {
        jTextFieldInventoryName.setText(inventoryName);
    }
    
    public void addInventoryNameTextFieldActionListener(DocumentListener listener) {
        jTextFieldInventoryName.getDocument().addDocumentListener(listener);
    }
    
    public String getTextAreaInventoryDescription() {
        return jTextAreaInventoryDescription.getText().trim();
    }
    
    public void setTextAreaInventoryDescription(String inventoryName) {
        jTextAreaInventoryDescription.setText(inventoryName);
    }
    
    public void addInventoryDescriptionTextAreaActionListener(DocumentListener listener) {
        jTextAreaInventoryDescription.getDocument().addDocumentListener(listener);
    }
    
    public String getTextFieldInventoryPrice() {
        return jTextFieldInventoryPrice.getText().trim();
    }
    
    public void setTextFieldInventoryPrice(String inventoryPrice) {
        jTextFieldInventoryPrice.setText(inventoryPrice);
    }
    
    public void addInventoryPriceTextFieldActionListener(DocumentListener listener) {
        jTextFieldInventoryPrice.getDocument().addDocumentListener(listener);
    }
     
    public void setInventoryUdateItemTableModel(DefaultTableModel model) {
        jTableInventoryUpdateItems.setModel(model);
    }
    
    public void addCleanInventoryButtonActionListener(ActionListener listener) {
        jButtonCleanInventory.addActionListener(listener);
    }
    
    public void addUpdateInventoryButtonActionListener(ActionListener listener) {
        jButtonUpdateInventory.addActionListener(listener);
    }
    
    // Robot Tab
    public void setRobotStatusListTableModel(DefaultTableModel model) {
        jTableRobotStatusList.setModel(model);
    }
    
    public void addRobotListSelectionListener(ListSelectionListener listener) {
        jTableRobotStatusList.getSelectionModel().addListSelectionListener(listener);
    }
    
    public void clearSelectedRowInRobotList() {
        jTableRobotStatusList.getSelectionModel().clearSelection();
    }
    
    public void setHiddenColumnRobotStatusListTable(int columnIndex) {
        jTableRobotStatusList.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        jTableRobotStatusList.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
    }
     
    public int getSelecteRowInRobotList() {
        return jTableRobotStatusList.getSelectedRow();
    }
    
    public void setTextFieldRobotErrorDescription(String errorDescription) {
        jTextFieldRobotErrorDescription.setText(errorDescription);
    }
    
    public void addRobotRefreshButtonActionListener(ActionListener listener) {
        jButtonRobotRefresh.addActionListener(listener);
    }
    
    public void setTextFieldSelectedRobotID(String selectedRobotID) {
        jTextFieldSelectedRobotID.setText(selectedRobotID);
    }
    
    public String getTextFieldSelectedRobotID() {
        return jTextFieldSelectedRobotID.getText();
    }
    
    public void addRobotManualControlOnOffToggleButtonActionListener(ActionListener listener) {
        jToggleButtonRobotManualControlOnOff.addActionListener(listener);
    }
    
    public void setToggleButtonRobotManualControlOnOff(boolean isOn) {
        jToggleButtonRobotManualControlOnOff.setSelected(isOn);
        jToggleButtonRobotManualControlOnOff.setText(isOn ? "Off" : "On");
    }
    
    public void setRobotControlOnOffButtonEnable(boolean enable) {
        jToggleButtonRobotManualControlOnOff.setEnabled(enable);
    }
    
    public void setRobotControlButtonEnable(boolean enable) {
        jButtonRobotMoveFoward.setEnabled(enable);
        jButtonRobotMoveBackward.setEnabled(enable);
        jButtonRobotMoveLeftTurn.setEnabled(enable);
        jButtonRobotMoveRightTurn.setEnabled(enable);
    }
    
    public void setRobotManualControlOnOffToggleButtonEnabled(boolean enabled) {
        jToggleButtonRobotManualControlOnOff.setEnabled(enabled);
    }
    
    public void addRobotMoveFowardButtonActionListener(ActionListener listener) {
        jButtonRobotMoveFoward.addActionListener(listener);
    }
    
    public void addRobotMoveBackwardButtonActionListener(ActionListener listener) {
        jButtonRobotMoveBackward.addActionListener(listener);
    }
    
    public void addRobotMoveLeftTurnButtonActionListener(ActionListener listener) {
        jButtonRobotMoveLeftTurn.addActionListener(listener);
    }
    
    public void addRobotMoveRightTurnButtonActionListener(ActionListener listener) {
        jButtonRobotMoveRightTurn.addActionListener(listener);
    }
    
    // Order Tab
    public void setOrderFilterButtonGroupVisible(boolean visible) {
        jRadioButtonOrderAll.setVisible(visible);
        jRadioButtonOrderPending.setVisible(visible);
        jRadioButtonInProgress.setVisible(visible);
        jRadioButtonOrderBackordered.setVisible(visible);
        jRadioButtonOrderComplete.setVisible(visible);
    }
    
    public void setOrderFilterButtonGroupActionCommand(OrderFilterEnum[] filters) {
        jRadioButtonOrderAll.setActionCommand(filters[0].toString());
        jRadioButtonOrderPending.setActionCommand(filters[1].toString());
        jRadioButtonInProgress.setActionCommand(filters[2].toString());
        jRadioButtonOrderBackordered.setActionCommand(filters[3].toString());
        jRadioButtonOrderComplete.setActionCommand(filters[4].toString());
    }
    
    public void addOrderFilterButtonGroupActionListener(ActionListener listener) {
        jRadioButtonOrderAll.addActionListener(listener);
        jRadioButtonOrderPending.addActionListener(listener);
        jRadioButtonInProgress.addActionListener(listener);
        jRadioButtonOrderBackordered.addActionListener(listener);
        jRadioButtonOrderComplete.addActionListener(listener);
    }
    
    public void setOrderListTableModel(DefaultTableModel model) {
        jTableOrderList.setModel(model);
    }
    
    public void setOrderItemsTableModel(DefaultTableModel model) {
        jTableOrderItems.setModel(model);
    }
    
    public void addOrderListSelectionListener(ListSelectionListener listener) {
        jTableOrderList.getSelectionModel().addListSelectionListener(listener);
    }
    
    public void clearSelectedRowInOrderList() {
        jTableOrderList.getSelectionModel().clearSelection();
    }
    
    public void setHiddenColumnOrderListTable(int columnIndex) {
        jTableOrderList.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        jTableOrderList.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
    }
     
    public int getSelecteRowInOrderList() {
        return jTableOrderList.getSelectedRow();
    }
    
    public void setSelectedRowInOrderList(int rowIndex) {
        jTableOrderList.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
    }
    
    public void addRefreshOrderListButtonActionListener(ActionListener listener) {
        jButtonRefreshOrderList.addActionListener(listener);
    }
    
    public void displayConnectionError(boolean isConnect, String address, int port) {
        String msg = (isConnect ? "connection to" : "disconnection from")
                + " address=" + address
                + ", port=" + port + " fail";
        
        JOptionPane.showMessageDialog(this, msg, "connection/disconnection", JOptionPane.ERROR_MESSAGE);
    }
    
    public void displayError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "supervisor", JOptionPane.ERROR_MESSAGE);
    }
    
    public void displayWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "supervisor", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOrderFilter = new javax.swing.ButtonGroup();
        buttonGroupInventoryUpdateAdd = new javax.swing.ButtonGroup();
        jButtonConnect = new javax.swing.JButton();
        jButtonDisconnect = new javax.swing.JButton();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelInventory = new javax.swing.JPanel();
        jPanelInventoryList = new javax.swing.JPanel();
        jLabelInventoryList = new javax.swing.JLabel();
        jScrollPaneInventoryList = new javax.swing.JScrollPane();
        jTableInventory = new javax.swing.JTable();
        jButtonRefreshInventory = new javax.swing.JButton();
        jScrollPaneInventoryItems1 = new javax.swing.JScrollPane();
        jTableInventoryCurrentItems = new javax.swing.JTable();
        jPanelInventoryUpdate = new javax.swing.JPanel();
        jLabelInventoryName = new javax.swing.JLabel();
        jLabelInventoryUpdate = new javax.swing.JLabel();
        jLabelInventoryDescription = new javax.swing.JLabel();
        jScrollPaneInventoryDescription = new javax.swing.JScrollPane();
        jTextAreaInventoryDescription = new javax.swing.JTextArea();
        jLabelInventoryPrice = new javax.swing.JLabel();
        jLabelInventoryPriceUnit = new javax.swing.JLabel();
        jLabelInventroyItems = new javax.swing.JLabel();
        jScrollPaneInventoryItems = new javax.swing.JScrollPane();
        jTableInventoryUpdateItems = new javax.swing.JTable();
        jButtonUpdateInventory = new javax.swing.JButton();
        jButtonCleanInventory = new javax.swing.JButton();
        jTextFieldInventoryName = new javax.swing.JTextField();
        jTextFieldInventoryPrice = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelRobot = new javax.swing.JPanel();
        jPanelRobotStatus = new javax.swing.JPanel();
        jLabelRobotStatus = new javax.swing.JLabel();
        jScrollPaneList = new javax.swing.JScrollPane();
        jTableRobotStatusList = new javax.swing.JTable();
        jButtonRobotRefresh = new javax.swing.JButton();
        jLabelRobotErrorDescription = new javax.swing.JLabel();
        jTextFieldRobotErrorDescription = new javax.swing.JTextField();
        jPanelRobotManualControl = new javax.swing.JPanel();
        jLabelManaulRobotControl = new javax.swing.JLabel();
        jToggleButtonRobotManualControlOnOff = new javax.swing.JToggleButton();
        jPanelRobotMove = new javax.swing.JPanel();
        jButtonRobotMoveLeftTurn = new javax.swing.JButton();
        jButtonRobotMoveFoward = new javax.swing.JButton();
        jButtonRobotMoveBackward = new javax.swing.JButton();
        jButtonRobotMoveRightTurn = new javax.swing.JButton();
        jLabelRobotSelect = new javax.swing.JLabel();
        jTextFieldSelectedRobotID = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelOrder = new javax.swing.JPanel();
        jPanelOrderList = new javax.swing.JPanel();
        jLabelOrderList = new javax.swing.JLabel();
        jScrollPaneOrderList = new javax.swing.JScrollPane();
        jTableOrderList = new javax.swing.JTable();
        jButtonRefreshOrderList = new javax.swing.JButton();
        jPanelOrderListFilter = new javax.swing.JPanel();
        jRadioButtonOrderPending = new javax.swing.JRadioButton();
        jRadioButtonInProgress = new javax.swing.JRadioButton();
        jRadioButtonOrderBackordered = new javax.swing.JRadioButton();
        jRadioButtonOrderComplete = new javax.swing.JRadioButton();
        jRadioButtonOrderAll = new javax.swing.JRadioButton();
        jLabelOrderItems = new javax.swing.JLabel();
        jLabelOrderID = new javax.swing.JLabel();
        jScrollPaneOrderItems = new javax.swing.JScrollPane();
        jTableOrderItems = new javax.swing.JTable();
        jLabelWarehouse = new javax.swing.JLabel();
        jComboBoxWarehouse = new javax.swing.JComboBox();
        jTextFieldAddress = new javax.swing.JTextField();
        jLabelAddress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonConnect.setText("Connect");

        jButtonDisconnect.setText("Disconnect");

        jLabelInventoryList.setText("Inventory List");

        jTableInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableInventory.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableInventory.getTableHeader().setReorderingAllowed(false);
        jScrollPaneInventoryList.setViewportView(jTableInventory);

        jButtonRefreshInventory.setText("Refresh");

        jTableInventoryCurrentItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableInventoryCurrentItems.setRowSelectionAllowed(false);
        jTableInventoryCurrentItems.getTableHeader().setReorderingAllowed(false);
        jScrollPaneInventoryItems1.setViewportView(jTableInventoryCurrentItems);

        javax.swing.GroupLayout jPanelInventoryListLayout = new javax.swing.GroupLayout(jPanelInventoryList);
        jPanelInventoryList.setLayout(jPanelInventoryListLayout);
        jPanelInventoryListLayout.setHorizontalGroup(
            jPanelInventoryListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryListLayout.createSequentialGroup()
                .addComponent(jLabelInventoryList)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInventoryListLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelInventoryListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonRefreshInventory)
                    .addGroup(jPanelInventoryListLayout.createSequentialGroup()
                        .addComponent(jScrollPaneInventoryList, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneInventoryItems1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanelInventoryListLayout.setVerticalGroup(
            jPanelInventoryListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryListLayout.createSequentialGroup()
                .addComponent(jLabelInventoryList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelInventoryListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneInventoryItems1, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(jScrollPaneInventoryList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRefreshInventory))
        );

        jLabelInventoryName.setText("Name:");

        jLabelInventoryUpdate.setText("Inventory Update");

        jLabelInventoryDescription.setText("Description:");

        jTextAreaInventoryDescription.setColumns(15);
        jTextAreaInventoryDescription.setRows(5);
        jScrollPaneInventoryDescription.setViewportView(jTextAreaInventoryDescription);

        jLabelInventoryPrice.setText("Price:");

        jLabelInventoryPriceUnit.setText("$");

        jLabelInventroyItems.setText("Items:");

        jTableInventoryUpdateItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableInventoryUpdateItems.setRowSelectionAllowed(false);
        jTableInventoryUpdateItems.getTableHeader().setReorderingAllowed(false);
        jScrollPaneInventoryItems.setViewportView(jTableInventoryUpdateItems);

        jButtonUpdateInventory.setText("Update");

        jButtonCleanInventory.setText("Clear");

        jTextFieldInventoryName.setEnabled(false);

        javax.swing.GroupLayout jPanelInventoryUpdateLayout = new javax.swing.GroupLayout(jPanelInventoryUpdate);
        jPanelInventoryUpdate.setLayout(jPanelInventoryUpdateLayout);
        jPanelInventoryUpdateLayout.setHorizontalGroup(
            jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                .addComponent(jLabelInventoryUpdate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jLabelInventoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldInventoryName))
                    .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jLabelInventoryPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldInventoryPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelInventoryPriceUnit))
                    .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jLabelInventoryDescription)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneInventoryDescription)))
                .addGap(12, 12, 12)
                .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jButtonCleanInventory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonUpdateInventory))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jLabelInventroyItems)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneInventoryItems, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanelInventoryUpdateLayout.setVerticalGroup(
            jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                .addComponent(jLabelInventoryUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelInventoryName)
                            .addComponent(jLabelInventroyItems)
                            .addComponent(jTextFieldInventoryName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelInventoryDescription)
                            .addComponent(jScrollPaneInventoryDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabelInventoryPrice)
                                    .addComponent(jTextFieldInventoryPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(49, 49, 49))
                            .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelInventoryPriceUnit)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanelInventoryUpdateLayout.createSequentialGroup()
                        .addComponent(jScrollPaneInventoryItems, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelInventoryUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonCleanInventory)
                            .addComponent(jButtonUpdateInventory))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanelInventoryLayout = new javax.swing.GroupLayout(jPanelInventory);
        jPanelInventory.setLayout(jPanelInventoryLayout);
        jPanelInventoryLayout.setHorizontalGroup(
            jPanelInventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelInventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelInventoryList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInventoryUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanelInventoryLayout.setVerticalGroup(
            jPanelInventoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInventoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInventoryList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelInventoryUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneMain.addTab("Inventory", jPanelInventory);

        jLabelRobotStatus.setText("Robot Status");

        jTableRobotStatusList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableRobotStatusList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableRobotStatusList.getTableHeader().setReorderingAllowed(false);
        jScrollPaneList.setViewportView(jTableRobotStatusList);

        jButtonRobotRefresh.setText("Refresh");

        jLabelRobotErrorDescription.setText("Error Description:");

        jTextFieldRobotErrorDescription.setEditable(false);
        jTextFieldRobotErrorDescription.setFocusable(false);

        javax.swing.GroupLayout jPanelRobotStatusLayout = new javax.swing.GroupLayout(jPanelRobotStatus);
        jPanelRobotStatus.setLayout(jPanelRobotStatusLayout);
        jPanelRobotStatusLayout.setHorizontalGroup(
            jPanelRobotStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotStatusLayout.createSequentialGroup()
                .addComponent(jLabelRobotStatus)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPaneList, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRobotStatusLayout.createSequentialGroup()
                .addComponent(jLabelRobotErrorDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldRobotErrorDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRobotRefresh))
        );
        jPanelRobotStatusLayout.setVerticalGroup(
            jPanelRobotStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotStatusLayout.createSequentialGroup()
                .addComponent(jLabelRobotStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneList, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRobotStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRobotRefresh)
                    .addComponent(jLabelRobotErrorDescription)
                    .addComponent(jTextFieldRobotErrorDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabelManaulRobotControl.setText("Manual Robot Control");

        jToggleButtonRobotManualControlOnOff.setText("On");

        jButtonRobotMoveLeftTurn.setText("Left-turn");
        jButtonRobotMoveLeftTurn.setEnabled(false);

        jButtonRobotMoveFoward.setText("Forward");
        jButtonRobotMoveFoward.setEnabled(false);
        jButtonRobotMoveFoward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRobotMoveFowardActionPerformed(evt);
            }
        });

        jButtonRobotMoveBackward.setText("Backward");
        jButtonRobotMoveBackward.setEnabled(false);

        jButtonRobotMoveRightTurn.setText("Right-turn");
        jButtonRobotMoveRightTurn.setEnabled(false);

        javax.swing.GroupLayout jPanelRobotMoveLayout = new javax.swing.GroupLayout(jPanelRobotMove);
        jPanelRobotMove.setLayout(jPanelRobotMoveLayout);
        jPanelRobotMoveLayout.setHorizontalGroup(
            jPanelRobotMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotMoveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRobotMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRobotMoveLayout.createSequentialGroup()
                        .addComponent(jButtonRobotMoveLeftTurn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                        .addComponent(jButtonRobotMoveRightTurn))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRobotMoveLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonRobotMoveFoward, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRobotMoveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRobotMoveBackward, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109))
        );
        jPanelRobotMoveLayout.setVerticalGroup(
            jPanelRobotMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotMoveLayout.createSequentialGroup()
                .addComponent(jButtonRobotMoveFoward, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRobotMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRobotMoveLeftTurn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRobotMoveRightTurn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRobotMoveBackward, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
        );

        jLabelRobotSelect.setText("Robot:");

        jTextFieldSelectedRobotID.setEditable(false);
        jTextFieldSelectedRobotID.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldSelectedRobotID.setActionCommand("<Not Set>");
        jTextFieldSelectedRobotID.setEnabled(false);
        jTextFieldSelectedRobotID.setFocusable(false);

        javax.swing.GroupLayout jPanelRobotManualControlLayout = new javax.swing.GroupLayout(jPanelRobotManualControl);
        jPanelRobotManualControl.setLayout(jPanelRobotManualControlLayout);
        jPanelRobotManualControlLayout.setHorizontalGroup(
            jPanelRobotManualControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotManualControlLayout.createSequentialGroup()
                .addGroup(jPanelRobotManualControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRobotManualControlLayout.createSequentialGroup()
                        .addGroup(jPanelRobotManualControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelManaulRobotControl)
                            .addGroup(jPanelRobotManualControlLayout.createSequentialGroup()
                                .addComponent(jLabelRobotSelect)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSelectedRobotID, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonRobotManualControlOnOff))
                    .addComponent(jPanelRobotMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanelRobotManualControlLayout.setVerticalGroup(
            jPanelRobotManualControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotManualControlLayout.createSequentialGroup()
                .addComponent(jLabelManaulRobotControl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRobotManualControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRobotSelect)
                    .addComponent(jToggleButtonRobotManualControlOnOff)
                    .addComponent(jTextFieldSelectedRobotID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelRobotMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelRobotLayout = new javax.swing.GroupLayout(jPanelRobot);
        jPanelRobot.setLayout(jPanelRobotLayout);
        jPanelRobotLayout.setHorizontalGroup(
            jPanelRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelRobotStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelRobotManualControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanelRobotLayout.setVerticalGroup(
            jPanelRobotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRobotLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelRobotStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelRobotManualControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneMain.addTab("Robot", jPanelRobot);

        jLabelOrderList.setText("Order List");

        jTableOrderList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableOrderList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableOrderList.getTableHeader().setReorderingAllowed(false);
        jScrollPaneOrderList.setViewportView(jTableOrderList);

        jButtonRefreshOrderList.setText("Refresh");

        buttonGroupOrderFilter.add(jRadioButtonOrderPending);
        jRadioButtonOrderPending.setText("pending");
        jRadioButtonOrderPending.setEnabled(false);

        buttonGroupOrderFilter.add(jRadioButtonInProgress);
        jRadioButtonInProgress.setText("in-progress");
        jRadioButtonInProgress.setEnabled(false);

        buttonGroupOrderFilter.add(jRadioButtonOrderBackordered);
        jRadioButtonOrderBackordered.setText("backordered");
        jRadioButtonOrderBackordered.setEnabled(false);

        buttonGroupOrderFilter.add(jRadioButtonOrderComplete);
        jRadioButtonOrderComplete.setText("complete");
        jRadioButtonOrderComplete.setEnabled(false);

        buttonGroupOrderFilter.add(jRadioButtonOrderAll);
        jRadioButtonOrderAll.setSelected(true);
        jRadioButtonOrderAll.setText("all");
        jRadioButtonOrderAll.setEnabled(false);

        javax.swing.GroupLayout jPanelOrderListFilterLayout = new javax.swing.GroupLayout(jPanelOrderListFilter);
        jPanelOrderListFilter.setLayout(jPanelOrderListFilterLayout);
        jPanelOrderListFilterLayout.setHorizontalGroup(
            jPanelOrderListFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOrderListFilterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButtonOrderAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonOrderPending)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonInProgress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonOrderBackordered)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonOrderComplete))
        );
        jPanelOrderListFilterLayout.setVerticalGroup(
            jPanelOrderListFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOrderListFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jRadioButtonOrderAll)
                .addComponent(jRadioButtonOrderPending)
                .addComponent(jRadioButtonInProgress)
                .addComponent(jRadioButtonOrderBackordered)
                .addComponent(jRadioButtonOrderComplete))
        );

        jLabelOrderItems.setText("Items for Order ID");

        jLabelOrderID.setText("          ");

        jTableOrderItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableOrderItems.setRowSelectionAllowed(false);
        jTableOrderItems.getTableHeader().setReorderingAllowed(false);
        jScrollPaneOrderItems.setViewportView(jTableOrderItems);

        javax.swing.GroupLayout jPanelOrderListLayout = new javax.swing.GroupLayout(jPanelOrderList);
        jPanelOrderList.setLayout(jPanelOrderListLayout);
        jPanelOrderListLayout.setHorizontalGroup(
            jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOrderListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelOrderItems)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonRefreshOrderList, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOrderListLayout.createSequentialGroup()
                        .addComponent(jLabelOrderID, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneOrderItems, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(jScrollPaneOrderList)
            .addGroup(jPanelOrderListLayout.createSequentialGroup()
                .addGroup(jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelOrderList)
                    .addComponent(jPanelOrderListFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelOrderListLayout.setVerticalGroup(
            jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOrderListLayout.createSequentialGroup()
                .addComponent(jLabelOrderList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOrderListFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneOrderList, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelOrderListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelOrderItems)
                        .addComponent(jLabelOrderID))
                    .addComponent(jScrollPaneOrderItems, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRefreshOrderList))
        );

        javax.swing.GroupLayout jPanelOrderLayout = new javax.swing.GroupLayout(jPanelOrder);
        jPanelOrder.setLayout(jPanelOrderLayout);
        jPanelOrderLayout.setHorizontalGroup(
            jPanelOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelOrderList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelOrderLayout.setVerticalGroup(
            jPanelOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelOrderList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPaneMain.addTab("Order", jPanelOrder);

        jLabelWarehouse.setText("Warehouse:");

        jLabelAddress.setText("Address:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDisconnect))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelWarehouse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxWarehouse, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jTabbedPaneMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConnect)
                    .addComponent(jButtonDisconnect)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelWarehouse)
                    .addComponent(jComboBoxWarehouse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneMain))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRobotMoveFowardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRobotMoveFowardActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonRobotMoveFowardActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupInventoryUpdateAdd;
    private javax.swing.ButtonGroup buttonGroupOrderFilter;
    private javax.swing.JButton jButtonCleanInventory;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonDisconnect;
    private javax.swing.JButton jButtonRefreshInventory;
    private javax.swing.JButton jButtonRefreshOrderList;
    private javax.swing.JButton jButtonRobotMoveBackward;
    private javax.swing.JButton jButtonRobotMoveFoward;
    private javax.swing.JButton jButtonRobotMoveLeftTurn;
    private javax.swing.JButton jButtonRobotMoveRightTurn;
    private javax.swing.JButton jButtonRobotRefresh;
    private javax.swing.JButton jButtonUpdateInventory;
    private javax.swing.JComboBox jComboBoxWarehouse;
    private javax.swing.JLabel jLabelAddress;
    private javax.swing.JLabel jLabelInventoryDescription;
    private javax.swing.JLabel jLabelInventoryList;
    private javax.swing.JLabel jLabelInventoryName;
    private javax.swing.JLabel jLabelInventoryPrice;
    private javax.swing.JLabel jLabelInventoryPriceUnit;
    private javax.swing.JLabel jLabelInventoryUpdate;
    private javax.swing.JLabel jLabelInventroyItems;
    private javax.swing.JLabel jLabelManaulRobotControl;
    private javax.swing.JLabel jLabelOrderID;
    private javax.swing.JLabel jLabelOrderItems;
    private javax.swing.JLabel jLabelOrderList;
    private javax.swing.JLabel jLabelRobotErrorDescription;
    private javax.swing.JLabel jLabelRobotSelect;
    private javax.swing.JLabel jLabelRobotStatus;
    private javax.swing.JLabel jLabelWarehouse;
    private javax.swing.JPanel jPanelInventory;
    private javax.swing.JPanel jPanelInventoryList;
    private javax.swing.JPanel jPanelInventoryUpdate;
    private javax.swing.JPanel jPanelOrder;
    private javax.swing.JPanel jPanelOrderList;
    private javax.swing.JPanel jPanelOrderListFilter;
    private javax.swing.JPanel jPanelRobot;
    private javax.swing.JPanel jPanelRobotManualControl;
    private javax.swing.JPanel jPanelRobotMove;
    private javax.swing.JPanel jPanelRobotStatus;
    private javax.swing.JRadioButton jRadioButtonInProgress;
    private javax.swing.JRadioButton jRadioButtonOrderAll;
    private javax.swing.JRadioButton jRadioButtonOrderBackordered;
    private javax.swing.JRadioButton jRadioButtonOrderComplete;
    private javax.swing.JRadioButton jRadioButtonOrderPending;
    private javax.swing.JScrollPane jScrollPaneInventoryDescription;
    private javax.swing.JScrollPane jScrollPaneInventoryItems;
    private javax.swing.JScrollPane jScrollPaneInventoryItems1;
    private javax.swing.JScrollPane jScrollPaneInventoryList;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JScrollPane jScrollPaneOrderItems;
    private javax.swing.JScrollPane jScrollPaneOrderList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTable jTableInventory;
    private javax.swing.JTable jTableInventoryCurrentItems;
    private javax.swing.JTable jTableInventoryUpdateItems;
    private javax.swing.JTable jTableOrderItems;
    private javax.swing.JTable jTableOrderList;
    private javax.swing.JTable jTableRobotStatusList;
    private javax.swing.JTextArea jTextAreaInventoryDescription;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldInventoryName;
    private javax.swing.JTextField jTextFieldInventoryPrice;
    private javax.swing.JTextField jTextFieldRobotErrorDescription;
    private javax.swing.JTextField jTextFieldSelectedRobotID;
    private javax.swing.JToggleButton jToggleButtonRobotManualControlOnOff;
    // End of variables declaration//GEN-END:variables
}
