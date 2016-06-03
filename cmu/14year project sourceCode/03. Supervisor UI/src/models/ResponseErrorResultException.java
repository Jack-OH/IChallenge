/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author sonmapsi
 */
public class ResponseErrorResultException extends Exception {
    private final String action;
    private final String request;
    private final String response;
    private final String message;
    
    public ResponseErrorResultException(String action, String msg) {
        this.action = action;
        this.request = "";
        this.response = "";
        if ("".equals(msg)) {
            msg = "Unknown reason";
        }
        this.message = msg;
    }
    
    public ResponseErrorResultException(String action, String request, String response, String msg) {
        this.action = action;
        this.request = request;
        this.response = response;
        if ("".equals(msg)) {
            msg = "Unknown reason";
        }
        this.message = msg;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        String msg = "[ResultError] action=" + action + ",reason=" + message;
        if (!"".equals(request) || !"".equals(response))
        {
            msg += "\n";
            msg += "request=" + request + "\n";
            msg += "response=" + response + "\n";
        }
        
        return msg;
    }
}
