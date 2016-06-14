package sureParkManager.common;

import sureParkManager.managementService.ManagementDBTransaction;
import java.util.ArrayList;

public class SureParkConfig {

	private static SureParkConfig instance = null;
	private ArrayList<GarageInfo> garageArray;

	private SureParkConfig() throws Exception {
		garageArray = new ArrayList<GarageInfo>();
	}

	public static SureParkConfig getInstance() throws Exception {
		if (instance == null) {
			synchronized (SureParkConfig.class) {
				if (instance == null) {
					instance = new SureParkConfig();
				}
			}
		}
		return instance;
	}

	public void putGarageInfo(GarageInfo info) {
		garageArray.add(info);
	}

	public int getGarageNum() {
		return garageArray.size();
	}

	public GarageInfo getGarageInfoFromIndex(int index) {
		return garageArray.get(index);
	}

	public GarageInfo getGarageInfoFromGarageID(int garageID) {
		for (GarageInfo info : this.garageArray) {
			if ( info.id == garageID ) {
				return info;
			}
		}
		return null;
	}

	public void updateGarageInfo() throws Exception {
		ManagementDBTransaction mgtDB = ManagementDBTransaction.getInstance();

		System.out.println("update Config");

		if (mgtDB.isDBConnected()) {
			this.garageArray.clear();
			this.garageArray = mgtDB.getGaragesInfo();
		}
	}
	
	public void setGarageSlotState(int garageID, int slotIdx, int slotStatus) throws Exception {
		ManagementDBTransaction mgtDB = ManagementDBTransaction.getInstance();
		
		for (GarageInfo info : this.garageArray) {
			if ( info.id == garageID ) {
					info.slotStatus.set(slotStatus, slotStatus);
				
				mgtDB.updateGarageSlot(info.id, info.slotStatus);
			}
		}
	}	
	
	public void setGarageSlotState(int garageID, ArrayList<Integer> slotStatus) throws Exception {
		ManagementDBTransaction mgtDB = ManagementDBTransaction.getInstance();
		
		for (GarageInfo info : this.garageArray) {
			if ( info.id == garageID ) {
				for(int i=0; i<info.slotNum; i++) {
					info.slotStatus.set(i, slotStatus.get(i));
				}
				
				mgtDB.updateGarageSlot(info.id, info.slotStatus);
			}
		}
	}

	public void printGarageList() {
		for (GarageInfo info : this.garageArray) {
			System.out.println("Garage ID : " + info.id);
			System.out.println("Garage Name : " + info.name);
			System.out.println("Garage IP : " + info.ip);
			System.out.println("Garage slot Numer : " + info.slotNum);
			System.out.print("Grage slot status : ");
			for( Integer slotStatus : info.slotStatus) {
				System.out.print( info.GarageSlotStatusIntToString(slotStatus) + " ");
			}
			System.out.println();
		}
	}
}
