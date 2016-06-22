package sureParkManager.controlService;

public interface IControlService {
	
	// add new facility
	public void addFacility(int facilityId);
	
	// open entry gate
	public void openEntryGate(int facilityId, int slotIndex );

}
