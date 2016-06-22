package sureParkManager.managementService;

import org.json.simple.JSONObject;
import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;

import java.io.IOException;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ManagementFacility implements IManagementFacility {
	private ManagementComm comm = null;
    private IManagementDBTransaction mgtDB = null;
    public ManagementFacility() {
        try {
            comm = new ManagementComm();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mgtDB = ManagementDBTransaction.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// CS -> MS
	public void updateSlotStatus(int garageID, int slotIdx, int slotStatus)  throws Exception
	{
		SureParkConfig config = SureParkConfig.getInstance();

		config.setGarageSlotState(garageID, slotIdx, slotStatus);

		JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
        jsonObject1.put("garageNumber", garageID);

		jsonObject.put("updateSlotStatus", jsonObject1);

		comm.broadcast(jsonObject.toJSONString() + "\n");
		
		System.out.println("update slot state, garageID : " + garageID + ", slot Index : " + slotIdx + ", slot Status : " + slotStatus);
	}
	
	// CS -> MS
	public void setFacilityFailure(int garageID, boolean isFail) throws Exception {
        mgtDB.setFacilityAvailable(garageID, !isFail);

		if (isFail) {
            System.out.println("EVENT : " + garageID + " facility is broken...");

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
            jsonObject1.put("garageNumber", garageID);

            jsonObject.put("detectFailure", jsonObject1);

            System.out.println(jsonObject.toJSONString());

            comm.broadcast(jsonObject.toJSONString() + "\n");
        }
		else {
            System.out.println("EVENT : " + garageID + " facility is fixed...");
        }
	}

    public void updateWrongParking(int garageID, int slotIdx) throws Exception {
        SureParkConfig config = SureParkConfig.getInstance();

        mgtDB.updateWrongParkingSlot(garageID, slotIdx, config.getLastConfirmInfo());

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
        jsonObject1.put("garageNumber", garageID);

        jsonObject.put("wrongParking", jsonObject1);

        System.out.println(jsonObject.toJSONString());

        comm.broadcast(jsonObject.toJSONString() + "\n");
    }

    public void leaveWithParking(int garageID, int slotIdx)  throws Exception {
        SureParkConfig config = SureParkConfig.getInstance();

        // slot status update
        config.setGarageSlotState(garageID, slotIdx, GarageInfo.kGarageInfoSlotStatusOpen);

        // parking fee calculation
        mgtDB.leaveWithParking(garageID, slotIdx);

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
        jsonObject1.put("garageNumber", garageID);

        jsonObject.put("updateSlotStatus", jsonObject1);

        comm.broadcast(jsonObject.toJSONString() + "\n");
    }

    public void leaveWithoutParking(int garageID, int slotIdx)  throws Exception {
        mgtDB.leaveWithoutParking(garageID, slotIdx);

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
        jsonObject1.put("garageNumber", garageID);

        jsonObject.put("updateSlotStatus", jsonObject1);

        comm.broadcast(jsonObject.toJSONString() + "\n");
    }

    public void testUpdateSlotStatus(int garageID)  throws Exception
    {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageName", mgtDB.getGarageName(garageID));
        jsonObject1.put("garageNumber", garageID);

        jsonObject.put("updateSlotStatus", jsonObject1);

        comm.broadcast(jsonObject.toJSONString() + "\n");
    }
}
