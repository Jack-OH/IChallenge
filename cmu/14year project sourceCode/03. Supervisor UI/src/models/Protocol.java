/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import types.InventoryInfo;
import types.OrderStatus;
import types.RobotStatus;

/**
 *
 * @author sonmapsi
 */
public class Protocol {
    private final boolean DEBUG = true;
    private Communication connection = null;
    
    private Protocol(){
    }
    
    private static class SingletonHolder{
        static final Protocol single = new Protocol();
    }
    
    public static Protocol getInstance(){
        return SingletonHolder.single;
    }
    
    public void setConnection(Communication connection) {
        this.connection = connection;
    }
    
    public ArrayList<Warehouse> getWarehouseList() throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getWarehouseList();
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            
            if (response.getResult()) {
                return response.getWarehouseList();
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getWarehouseList-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getWarehouseList-Pase", ex);
            return null;
        }
    }
    
    public ArrayList<String> getInventoryList(int warehouseId) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getInventoryList(warehouseId);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            
            if (response.getResult()) {
                return response.getInventoryList(warehouseId);
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getInventoryList-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getInventoryList-Parse", ex);
            return null;
        }
    }
    
    public InventoryInfo getInventoryInfo(int warehouseId, String name) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getInventoryInfo(warehouseId, name);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getInventoryInfo(warehouseId, name);
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getInventoryInfo-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getInventoryInfo-Parse", ex);
            return null;
        }
    }
    
    public boolean updateInventory(InventoryInfo inventory) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.updateInventory(inventory);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return true;
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "updateInventory-IO", ex);
            return false;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "updateInventory-Parse", ex);
            return false;
        }
    }
    
    public ArrayList<RobotStatus> getRobotStatus(int warehouseId) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getRobotStatus(warehouseId);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getRobotStatus(warehouseId);
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getRobotStatus-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getRobotStatus-Parse", ex);
            return null;
        }
    }
    
    public boolean setRobotManualControlMode(int warehouseId, int robotId, String onOff) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.setRobotManualControlMode(warehouseId, robotId, onOff);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return true;
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "setRobotManualControlMode-IO", ex);
            return false;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "setRobotManualControlMode-Parse", ex);
            return false;
        }
    }
    
    public boolean moveRobotManual(int warehouseId, int robotId, String direction) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.moveRobotManual(warehouseId, robotId, direction);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return true;
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "moveRobotManual-IO", ex);
            return false;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "moveRobotManual-Parse", ex);
            return false;
        }
    }
    
    public ArrayList<Integer> getOrderList(String filter) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getOrderList(filter);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getOrderList();
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getOrderList-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getOrderList-Parse", ex);
            return null;
        }
    }
    
    public OrderStatus getOrderStatus(int id) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getOrderStatus(id);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getOrderStatus(id);
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getOrderStatus-IO", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getOrderStatus-Parse", ex);
            return null;
        }
    }
    
    public boolean checkBackOrdered() throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.checkBackOrdered();
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.checkBackOrdered();
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "checkBackOrdered-IO", ex);
            return false;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "checkBackOrdered-Parse", ex);
            return false;
        }
    }
}


