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
public class OrderItemInfo {
    private final String name;
    private final float price;
    private int count;
    
    public OrderItemInfo(String name, float price) {
        this.name = name;
        this.price = price;
        this.count = 1;
    }
    
    public OrderItemInfo(String name, float price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
    
    public String getName() {
        return this.name;
    }
    
    public float getPrice() {
        return this.price;
    }
    
    public float getSubTotal() {
        return this.price * this.count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getCount() {
        return this.count;
    }
}
