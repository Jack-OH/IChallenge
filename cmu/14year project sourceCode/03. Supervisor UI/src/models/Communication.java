/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sonmapsi
 */
public class Communication {
    private final String MODULE_TAG = "COMMUNICATION: ";
    private final boolean DEBUG = true;
    
    private final String address;
    private final int port;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private boolean connected;
    
    public Communication(String address, int port) {
        this.address = address;
        this.port = port;
        
        this.connected = false;
    }
    
    public String getAddress() {
        return address;
    }
    
    public int getPort() {
        return port;
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(address, port);
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "Connection error", ex);
            return false;
        }
        
        try {
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "Can't get output steam", ex);
            return false;
        }
        try {
            inStream = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "Can't get input stream", ex);
            return false;
        }
        
        connected = true;
        return true;
    }
    
    public boolean disconnect() {
        if (!socket.isClosed()) {
            if (socket.isConnected()) {
                try {
                    System.out.println("disconnect");
                    socket.close();
                    socket = null;
                } catch (IOException ex) {
                    Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "Disconnection error", ex);
                    return false;
                }
            }
        }
        
        connected = false;
        
        return true;
    }
    
    public synchronized String sendCommandSync(String command) throws IOException {
        try {
            sendCommand(command, true);
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
                    "Command: " + command + "\n"
                    + "Send async command fail", ex);
            throw new IOException(ex);
        }
        
        String result = "";
        
        try {
            result = getResult();
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE,
                    "Command: " + command + "\n"
                    + "Get result fail", ex);
            throw new IOException(ex);
        }
        
        return result;
    }
    
    public synchronized boolean sendCommandAsync(String command) {
        // TODO
        try {
            sendCommand(command, true);
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "Send async command fail", ex);
            return false;
        }
        
        return true;
    }
    
    private void sendCommand(String command, boolean isAsync) throws IOException {
        outStream.writeUTF(command);
    }
    
    private String getResult() throws IOException {
        String result = inStream.readUTF();
        return result;
    }
    
}
