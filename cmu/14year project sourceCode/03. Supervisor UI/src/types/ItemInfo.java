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
public final class ItemInfo {
    private final String itemName;
    private final int count;
    
    public ItemInfo(String itemName, int count) {
        this.itemName = itemName;
        this.count = count;
    }
    
    public String getItemName() {
        return this.itemName;
    }
    
    public int getCount() {
        return this.count;
    }
}
