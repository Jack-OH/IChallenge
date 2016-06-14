package SureParkManager.common;

import java.util.ArrayList;

public class GarageInfo {
	public static final int kGarageInfoSlotStatusOpen = 0;
	public static final int kGarageInfoSlotStatusOccupied = 1;
	public static final int kGarageInfoSlotStatusBroken = 2;
	public static final int kGarageInfoSlotStatusReserved = 3;



	public int id;
	public String name;
	public String ip;
	public int slotNum;
	public ArrayList<Integer> slotStatus;
	
	public GarageInfo() {
	}
	
	public GarageInfo(int id, String name, String ip, int slotNum) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.slotNum = slotNum;		
		this.slotStatus = new ArrayList<Integer>();
		
		for( int i = 0 ; i < this.slotNum ; i++ )
			this.slotStatus.add(0);
	}
	
	public String GarageSlotStatusIntToString(int slotStatus) {
		String slotStatusStr = null;
		
		if (	 slotStatus == kGarageInfoSlotStatusOpen) 		slotStatusStr = new String("Open");
		else if (slotStatus == kGarageInfoSlotStatusBroken) 	slotStatusStr = new String("Broken");
		else if (slotStatus == kGarageInfoSlotStatusOccupied) 	slotStatusStr = new String("Occpied");
		else if (slotStatus == kGarageInfoSlotStatusReserved) 	slotStatusStr = new String("Reserved");
		
		return slotStatusStr;
	}
	
	public int GrageSlotStatusStringToInt(String slotStatus) {
		int slotStatusEnum = -1;
		
		if (	 slotStatus.equals("Open")) 	slotStatusEnum = kGarageInfoSlotStatusOpen;
		else if (slotStatus.equals("Broken")) 	slotStatusEnum = kGarageInfoSlotStatusBroken;
		else if (slotStatus.equals("Occpied")) 	slotStatusEnum = kGarageInfoSlotStatusOccupied;
		else if (slotStatus.equals("Reserved")) slotStatusEnum = kGarageInfoSlotStatusReserved;
		
		return slotStatusEnum;
	}
	
}
