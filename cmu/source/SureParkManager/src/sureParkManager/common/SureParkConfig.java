package sureParkManager.common;

import sureParkManager.managementService.ManagementDBTransaction;
import java.util.ArrayList;

public class SureParkConfig {
	private static SureParkConfig instance = null;
	private ArrayList<GarageInfo> garageArray;

    public String getLastConfirmInfo() {
        return lastConfirmInfo;
    }

    public void setLastConfirmInfo(String lastConfirmInfo) {
        this.lastConfirmInfo = lastConfirmInfo;
    }

    private String lastConfirmInfo = null;

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

	public int getGarageSlotState(int garageID, int slotIdx) throws Exception {
        int ret = -1;
        updateGarageInfo();

        for (GarageInfo info : this.garageArray) {
            if ( info.id == garageID ) {
                ret = info.slotStatus.get(slotIdx);
            }
        }

        return ret;
    }
	
	public void setGarageSlotState(int garageID, int slotIdx, int slotStatus) throws Exception {
		ManagementDBTransaction mgtDB = ManagementDBTransaction.getInstance();
		
		for (GarageInfo info : this.garageArray) {
			if ( info.id == garageID ) {
					info.slotStatus.set(slotIdx, slotStatus);
				
				mgtDB.updateGarageSlot(garageID, slotIdx, slotStatus);
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
