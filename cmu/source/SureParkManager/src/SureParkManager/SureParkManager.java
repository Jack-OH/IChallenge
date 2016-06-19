package sureParkManager;

import sureParkManager.managementService.IManagementFacility;
import sureParkManager.managementService.ManagementFacility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import sureParkManager.common.SureParkConfig;
import sureParkManager.controlService.ControlService;
import sureParkManager.managementService.ManagementServer;
import sureParkManager.noshowService.NoShowManager;

public class SureParkManager {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	        
        SureParkConfig config = SureParkConfig.getInstance();
        config.updateGarageInfo(); // DB update
        
        // Show current List
        config.printGarageList();

        // Create ManagementFacility;
        IManagementFacility mgtFacility = new ManagementFacility();
                
        // Create ControlService
        ControlService ctlService = new ControlService(mgtFacility);
        ctlService.start();
        
        // Start server thread
		ManagementServer mgtServer = ManagementServer.getInstance();
        mgtServer.setControlService(ctlService);
        mgtServer.start();
        
        NoShowManager noshowMgr = new NoShowManager();
        noshowMgr.start();
        
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
            } else if(message.equals("0")) {
                mgtFacility.setFacilityFailure(1001, false);
            } else if(message.equals("1")) {
                mgtFacility.setFacilityFailure(1001, true);
			} else if(message.equals("EXIT")) {
				ctlService.interrupt();
				break;
			}	
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            Thread.sleep(1000); 
        	
        } 
	}
}
