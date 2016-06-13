package SureParkManager.common;

import java.util.ArrayList;

public class eventManager {
	// MS -> CS
	public void newGarage() {
		System.out.println("EVENT : new garage");
	}
	
	// MS -> CS
	public void newReservation() {
		System.out.println("EVENT : new reseravtion");
	}
	
	// MS -> CS
	public void openDoor(int garageID, int slotNum)
	{
		System.out.println("EVENT : open door");
	}
	
	// CS -> MS
	public void updateSlotStatus(int garageID, int slotIdx, int slotStatus)
	{
		System.out.println("update slot state");
	}
	
	// CS -> MS 
	public void setFacilityFailure(int garageID, boolean isFail)
	{
		System.out.println("EVENT : broken facility");
	}
}
