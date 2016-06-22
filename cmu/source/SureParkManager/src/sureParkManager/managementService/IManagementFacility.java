package sureParkManager.managementService;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public interface IManagementFacility {

	// CS -> MS
	void updateSlotStatus(int garageID, int slotIdx, int slotStatus) throws Exception;
	
	// CS -> MS 
	void setFacilityFailure(int garageID, boolean isFail) throws Exception;

	void leaveWithParking(int garageID, int slotIdx) throws Exception;

    void leaveWithoutParking(int garageID, int slotIdx)  throws Exception;

	void updateWrongParking(int garageID, int slot) throws Exception;

    void testUpdateSlotStatus(int garageID)  throws Exception;
}
