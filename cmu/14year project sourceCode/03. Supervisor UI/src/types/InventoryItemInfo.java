/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package types;

/**
 *
 * @author sonmapsi
 */
public final class InventoryItemInfo {
    private final String stationId;
    private int count;
    private int changedCount;
    
    public InventoryItemInfo(String stationId, int count) {
        this.stationId = stationId;
        this.count = count;
        this.changedCount = 0;
    }
    
    public String getStationId() {
        return this.stationId;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public void setChangedCount(int changedCount) {
        this.changedCount = changedCount;
    }
    
    public int getChangedCount() {
        return this.changedCount;
    }
}
