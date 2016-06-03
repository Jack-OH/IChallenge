/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author sonmapsi
 */
public final class WarehouseComboBoxModel extends DefaultComboBoxModel {
    private final boolean DEBUG = true;
    
    private ArrayList<Warehouse> warehouseItems;
    
    private String selection;

    public WarehouseComboBoxModel(ArrayList<Warehouse> warehouseList) {
        warehouseItems = warehouseList;
        selection = null;
        
        Collections.sort(warehouseItems, new Comparator<Warehouse>() {
            @Override
            public int compare(Warehouse warehouse1, Warehouse warehouse2) {

                if (warehouse1.getId() > warehouse2.getId()) {
                    return 1;
                } else if (warehouse1.getId() == warehouse2.getId()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        
        if (DEBUG) {
            System.out.println(this.toString());
        }
    }
    
    public void clearAllItems() {
        warehouseItems = new ArrayList<>();
        removeAllElements();
    }
    
    @Override
    public String toString() {
        String str;
        
        str = "[Warehouse]\n";
        for (Warehouse warehouse: warehouseItems) {
            str += "id: " + warehouse.getId()
                    + ", name: " + warehouse.getName()
                    + ", location: " + warehouse.getLocation()
                    + ", station count: " + warehouse.getStationCount() + "\n";
        }
        
        return str;
    }

    @Override
    public int getSize() {
        return warehouseItems.size();
    }

    @Override
    public Object getElementAt(int index) {
        return warehouseItems.get(index).getName();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (String) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }
    
    public int getSelectedId(int index) {
        if (selection == null) {
            return -1;
        } else {
            return warehouseItems.get(index).getId();
        }
    }
}
