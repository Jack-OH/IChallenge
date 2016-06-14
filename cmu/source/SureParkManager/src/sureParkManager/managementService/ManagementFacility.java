package sureParkManager.managementService;

import sureParkManager.common.SureParkConfig;

public class ManagementFacility extends AbstractManagementFacility {
	// CS -> MS
	public void updateSlotStatus(int garageID, int slotIdx, int slotStatus)  throws Exception
	{
		SureParkConfig config = SureParkConfig.getInstance();
		config.setGarageSlotState(garageID, slotIdx, slotStatus);
		
		System.out.println("update slot state");
	}
	
	// CS -> MS
	public void setFacilityFailure(int garageID, boolean isFail)
	{
		if (isFail)
			System.out.println("EVENT : " + garageID +" facility is broken...");
		else
			System.out.println("EVENT : " + garageID +" facility is fixed...");

	}
}
