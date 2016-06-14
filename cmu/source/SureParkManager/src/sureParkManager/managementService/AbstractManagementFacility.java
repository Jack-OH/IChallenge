package sureParkManager.managementService;

public abstract class AbstractManagementFacility{

	// CS -> MS
	public abstract void updateSlotStatus(int garageID, int slotIdx, int slotStatus) throws Exception;
	
	// CS -> MS 
	public abstract void setFacilityFailure(int garageID, boolean isFail);
}
