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
import javax.swing.AbstractButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import models.ResponseErrorResultException;
import models.RobotModel;
import views.MainJFrame;

/**
 *
 * @author sonmapsi
 */
public class RobotController {
    private final boolean DEBUG = true;
    private final RobotModel robotModel;
    private final MainJFrame frame;
    
    private boolean contentsInvalidated;
    
    public RobotController(MainJFrame frame) {
        this.frame = frame;
        robotModel = new RobotModel(1);
        contentsInvalidated = true;
        
        frame.setRobotStatusListTableModel(robotModel.getRobotListModel());
        frame.setHiddenColumnRobotStatusListTable(robotModel.getRobotListModel().getErrorDescriptionColumn());
        frame.addRobotListSelectionListener(new RobotListSelectionListener());
        
        frame.addRobotRefreshButtonActionListener(new RefreshButtonActionListener());
        frame.addRobotManualControlOnOffToggleButtonActionListener(new ManualControlToggleButtonActionListener());
        frame.addRobotMoveFowardButtonActionListener(new ForwardButtonActionListener());
        frame.addRobotMoveBackwardButtonActionListener(new BackwardButtonActionListener());
        frame.addRobotMoveRightTurnButtonActionListener(new RightTurnButtonActionListener());
        frame.addRobotMoveLeftTurnButtonActionListener(new LeftTurnButtonActionListener());
    }
    
    public void setSelectedWarehouseId(int id) {
        robotModel.setWarehouseId(id);
    }
    
    public void tabSelected() {
        if (contentsInvalidated) {
            refreshRobotStatus();
        }
    }
    
    public void refreshRobotStatus() {
        try {
            frame.clearSelectedRowInRobotList();
            frame.setTextFieldSelectedRobotID("");
            frame.setToggleButtonRobotManualControlOnOff(false);
            frame.setRobotControlOnOffButtonEnable(false);
            frame.setRobotControlButtonEnable(false);
            robotModel.refreshRobotStatus();
            
            contentsInvalidated = false;
        } catch (ResponseErrorResultException ex) {
            Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "RefreshRobotStatus", ex);
            frame.displayError("Refresh Robot Status Error\nreason=" + ex.getMessage());
        }
    }
    
    public void setCotentsInvalidated() {
        this.contentsInvalidated = true;
    }
    
    private class RobotListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // Ignore Multiple Event
                int selectedRow = frame.getSelecteRowInRobotList();
                
                if (DEBUG) {
                    System.out.println("Robot List Selected: " + selectedRow);
                }
                
                if (selectedRow >= 0) {
                    String errorMessage = robotModel.getRobotListModel().getErrorMessage(selectedRow);
                    if (errorMessage.length() > 0) {
                        frame.setTextFieldRobotErrorDescription(errorMessage);
                    }
                    
                    int robotId = robotModel.getRobotListModel().getRobotId(selectedRow);
                    boolean manualControl = robotModel.getRobotListModel().getManunalControl(selectedRow);
                    
                    frame.setTextFieldSelectedRobotID(String.valueOf(robotId));
                    frame.setRobotControlOnOffButtonEnable(true);

                    frame.setToggleButtonRobotManualControlOnOff(manualControl);
                    frame.setRobotControlButtonEnable(manualControl);
                }
            }
        }
    }
    
    private class RefreshButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            refreshRobotStatus();
        }
    }
    
    private class ManualControlToggleButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            AbstractButton abstractButton = (AbstractButton) evt.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            int robotId = Integer.parseInt(frame.getTextFieldSelectedRobotID());
            
            try {
                robotModel.setRobotManualControlMode(robotId, selected);
                
                frame.setToggleButtonRobotManualControlOnOff(selected);
                frame.setRobotControlButtonEnable(selected);
                robotModel.getRobotListModel().setManualControl(robotId, selected);
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "ManualControlButtonClicked", ex);
                frame.displayError("Set Manual Mode On/Off Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class ForwardButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int robotId = Integer.parseInt(frame.getTextFieldSelectedRobotID());
            try {
                robotModel.moveRobotManual(robotId, "forward");
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "RobotForwardMove", ex);
                frame.displayError("Robot Manual Move Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class BackwardButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int robotId = Integer.parseInt(frame.getTextFieldSelectedRobotID());
            try {
                robotModel.moveRobotManual(robotId, "backward");
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "RobotBackwardMove", ex);
                frame.displayError("Robot Manual Move Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class RightTurnButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int robotId = Integer.parseInt(frame.getTextFieldSelectedRobotID());
            try {
                robotModel.moveRobotManual(robotId, "right-turn");
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "RobotRightMove", ex);
                frame.displayError("Robot Manual Move Error\nreason=" + ex.getMessage());
            }
        }
    }
    
    private class LeftTurnButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int robotId = Integer.parseInt(frame.getTextFieldSelectedRobotID());
            try {
                robotModel.moveRobotManual(robotId, "left-turn");
            } catch (ResponseErrorResultException ex) {
                Logger.getLogger(RobotController.class.getName()).log(Level.SEVERE, "RobotLeftMove", ex);
                frame.displayError("Robot Manual Move Error\nreason=" + ex.getMessage());
            }
        }
    }
}
