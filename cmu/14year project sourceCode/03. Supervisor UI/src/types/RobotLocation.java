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
public final class RobotLocation {
    private final int source;
    private final int destination;
    
    public RobotLocation(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }
    
    @Override
    public String toString() {
        String location = "";
        if (source == destination) {
            if (source == 0) {
                location = "at shipping station";
            } else {
                location = "at station " + source;
            }
        } else if (source != destination) {
            if (source == 0) {
                location = "from shipping station to station " + destination;
            } else if (destination == 0) {
                location = "from station " + source + "to shipping station";
            } else {
                location = "from station " + source + "to station " + destination;
            }
        }
        
        return location;
    }
}
