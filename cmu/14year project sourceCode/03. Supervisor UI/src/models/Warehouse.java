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
public class Warehouse {
    private final int id;
    private final String name;
    private final String location;
    private final int stationCount;
    
    public Warehouse(int id, String name, String location, int stationCount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.stationCount = stationCount;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public int getStationCount() {
        return stationCount;
    }
    
    @Override
    public String toString() {
        return "[Warehouse] id: " + id
                + "name: " + name
                + "location: " + location
                + "station count: " + stationCount;
    }
}
