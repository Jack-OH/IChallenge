/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author sonmapsi
 */
public class OrderInfo {
    private final Map<String, Integer> orders;
    
    public OrderInfo() {
        orders = new LinkedHashMap<>();
    }
    
    public void put(String name, int count) {
        orders.put(name, count);
    }
    
    public Map getOrderInfo() {
        return orders;
    }
}
