/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.Observable;
import models.OrderModel;

/**
 *
 * @author sonmapsi
 */
public class BackorderCheckThread extends Observable implements Runnable {
    private final int LONG_SLEEP_TIMER = 60000; // 60s
    private final OrderModel orderModel;
    private boolean stopRequested;
    private final int period;
    private boolean backordered;
    
    public BackorderCheckThread(OrderModel orderModel, int period_ms) {
        this.orderModel = orderModel;
        this.stopRequested = false;
        this.period = period_ms;
        this.backordered = false;
    }
    
    public void setStop() {
        stopRequested = true;
    }
    
    public boolean isBackordered() {
        return backordered;
    }
    
    @Override
    public void run() {
        while(!stopRequested) {
            // Check Backordered
            //backordered = orderModel.checkBackordered();
            
            // if backorder is exist, notify to subscriber
            if (backordered) {
                setChanged();
                notifyObservers();
            }
            
            // Sleep 
            try {
                if (backordered) {
                    Thread.sleep(LONG_SLEEP_TIMER);
                } else {
                    Thread.sleep(period);
                }
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
