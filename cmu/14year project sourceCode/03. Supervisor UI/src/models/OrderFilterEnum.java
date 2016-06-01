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
public enum OrderFilterEnum {
    ALL("all"),
    PENDING("pending"),
    INPROGRESS("in-progress"),
    BACKORDERED("backordered"),
    COMPLETE("complete");
    
    String name;
    
    OrderFilterEnum(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
