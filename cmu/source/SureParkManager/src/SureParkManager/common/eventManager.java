package SureParkManager.common;

import java.util.ArrayList;

public class eventManager {
	public void updateSlotStatus(int garageID, int slotNum, int slotStatus)
	{
		System.out.println("update slot state");
	}
	
	public void notifyWrongParking(int garageID, int slotNum)
	{
		// 현재 들어온 드라이버의 예약 슬롯을 업데이트 해야한다.
		System.out.println("notify wrong pakring");
	}
	
	public void notifyDriverGetOff(int garageID, int slotNum)
	{
		System.out.println("notify driver get off");
	}
	
	public void openDoor() 
	{
		System.out.println("open door");
	}
	
	public void updateGarageSlotStatus(int garageID, ArrayList<Integer> slotStatus) 
	{
		System.out.println("update garage slotstatus");
	}
	
	public void brokenFacility(int garageID) 
	{
		System.out.println("Broken facility");
	}
}
