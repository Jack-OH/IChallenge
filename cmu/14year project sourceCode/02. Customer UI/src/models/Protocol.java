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
import types.OrderInfo;

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
    
    public ArrayList<String> getItemList() throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getItemList();
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getItemList();
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getItemList", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getItemList", ex);
            return null;
        }
    }
    
    public InventoryInfo getItemInfo(String name) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.getItemInfo(name);
        
        if (DEBUG) {
            System.out.println("request: " + jsonRequest);
        }
        
        // Send command and parsing return
        try {
            String jsonResponse = connection.sendCommandSync(jsonRequest);
            Response response = request.getResponse(jsonResponse);
            if (response.getResult()) {
                return response.getItemInfo();
            } else {
                throw new ResponseErrorResultException(response.getAction(), response.getReson());
            }
        } catch (IOException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getItemInfo", ex);
            return null;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "getItemInfo", ex);
            return null;
        }
    }
    
    public boolean makeOrder(String userID, OrderInfo orderInfo) throws ResponseErrorResultException {
        Request request = new Request();
        String jsonRequest = request.makeOrder(userID, orderInfo);
        
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
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "makeOrder", ex);
            return false;
        } catch (ResponseParseException ex) {
            Logger.getLogger(Protocol.class.getName()).log(Level.SEVERE, "makeOrder", ex);
            return false;
        }
    }
}


