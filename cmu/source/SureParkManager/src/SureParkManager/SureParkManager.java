package SureParkManager;

import java.util.ArrayList;

import SureParkManager.common.GarageInfo;
import SureParkManager.common.SureParkConfig;
import SureParkManager.managementService.ManagementServer;

public class SureParkManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ManagementServer mgrServer = new ManagementServer();
        
        SureParkConfig config = SureParkConfig.getInstance();
        config.updateGarageInfo(); // DB update
        
        // Show current List
        config.printGarageList();
        
        // Start server thread
        mgrServer.start();
        
        // Example for update status
        ArrayList<Integer> slotStatus = new ArrayList<Integer>();
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusOpen);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusBroken);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusOccupied);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusReserved);
        
        config.setGarageSlotState(1001, slotStatus);
        
        config.printGarageList();
                    
        while (true) {
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            Thread.sleep(1000); 
        	
        } 
	}
}
