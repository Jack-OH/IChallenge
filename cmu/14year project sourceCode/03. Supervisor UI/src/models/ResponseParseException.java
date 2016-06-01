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
public class ResponseParseException extends Exception {
    private final String message;
    
    public ResponseParseException(String msg) {
        message = msg;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
