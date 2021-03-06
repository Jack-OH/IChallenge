package sureParkManager.common;

import java.util.ArrayList;

public class GarageInfo {
	public static final int kGarageInfoSlotStatusOpen = 0;
	public static final int kGarageInfoSlotStatusOccupied = 1;
	public static final int kGarageInfoSlotStatusBroken = 2;
	public static final int kGarageInfoSlotStatusReserved = 3;

	private static final int kFacilitySlotNumberBase = 0;

	public int id;
	public String name;
	public String ip;
	public int slotNum;
	public ArrayList<Integer> slotStatus;
	
	public GarageInfo() {
	}
	
	public GarageInfo(int id, String name, String ip, int slotNum, ArrayList<Integer> slotStatus) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.slotNum = slotNum;		
		this.slotStatus = slotStatus;
	}
	
	public static String GarageSlotStatusIntToString(int slotStatus) {
		String slotStatusStr = null;
		
		if (	 slotStatus == kGarageInfoSlotStatusOpen) 		slotStatusStr = new String("Open");
		else if (slotStatus == kGarageInfoSlotStatusBroken) 	slotStatusStr = new String("Broken");
		else if (slotStatus == kGarageInfoSlotStatusOccupied) 	slotStatusStr = new String("Occupied");
		else if (slotStatus == kGarageInfoSlotStatusReserved) 	slotStatusStr = new String("Reserved");
		
		return slotStatusStr;
	}
	
	public static int GrageSlotStatusStringToInt(String slotStatus) {
		int slotStatusEnum = -1;
		
		if (	 slotStatus.equals("Open")) 	slotStatusEnum = kGarageInfoSlotStatusOpen;
		else if (slotStatus.equals("Broken")) 	slotStatusEnum = kGarageInfoSlotStatusBroken;
		else if (slotStatus.equals("Occupied")) slotStatusEnum = kGarageInfoSlotStatusOccupied;
		else if (slotStatus.equals("Reserved")) slotStatusEnum = kGarageInfoSlotStatusReserved;
		
		return slotStatusEnum;
	}
	
}
