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
public class InventoryInfo {
    private final String name;
    private final String description;
    private final float price;
    private final int count;
    
    public InventoryInfo(String name, String description, float price, int count) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.count = count;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public float getPrice() {
        return price;
    }
    
    public int getCount() {
        return count;
    }
}
