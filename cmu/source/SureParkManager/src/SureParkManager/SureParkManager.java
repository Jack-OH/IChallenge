package sureParkManager;

import sureParkManager.managementService.AbstractManagementFacility;
import sureParkManager.managementService.ManagementFacility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import sureParkManager.common.SureParkConfig;
import sureParkManager.controlService.ControlService;
import sureParkManager.managementService.ServerReceiver;

public class SureParkManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	        
        SureParkConfig config = SureParkConfig.getInstance();
        config.updateGarageInfo(); // DB update
        
        // Show current List
        config.printGarageList();

        // Create ManagementFacility;
        AbstractManagementFacility mgrFacility = new ManagementFacility();
                
        // Create ControlService
        ControlService ctlService = new ControlService(mgrFacility);
        
        // Start server thread
		ServerReceiver commRecv = new ServerReceiver(ctlService);
        commRecv.start();
        
        // Example for update status
        /* ArrayList<Integer> slotStatus = new ArrayList<Integer>();
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusOpen);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusBroken);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusOccupied);
        slotStatus.add(GarageInfo.kGarageInfoSlotStatusReserved);
        
        config.setGarageSlotState(1001, slotStatus);
        
        config.printGarageList();
        */
        
        // It's test for Open gate
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
        	
			String message = in.readLine();
			
			if( message.equals("g") ) {
				ctlService.openEntryGate(1001, 1);
			} else if(message.equals("EXIT")) {
				break;
			}	
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            Thread.sleep(1000); 
        	
        } 
	}
}
