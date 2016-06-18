package sureParkManager.managementService;

public interface IManagementFacility {

	// CS -> MS
	void updateSlotStatus(int garageID, int slotIdx, int slotStatus) throws Exception;
	
	// CS -> MS 
	void setFacilityFailure(int garageID, boolean isFail) throws Exception;
}
