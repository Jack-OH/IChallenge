package sureParkManager.managementService;

import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;

import java.io.IOException;

public class ManagementFacility implements IManagementFacility {
	ManagementComm comm = null;

	public ManagementFacility() throws IOException {
		comm = new ManagementComm();
	}

	// CS -> MS
	public void updateSlotStatus(int garageID, int slotIdx, int slotStatus)  throws Exception
	{
		SureParkConfig config = SureParkConfig.getInstance();
		config.setGarageSlotState(garageID, slotIdx, slotStatus);

		JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();

        jsonObject1.put("garageID", garageID);

		jsonObject.put("updateSlotStatus", jsonObject1);

		comm.broadcast(jsonObject.toJSONString() + "\n");
		
		System.out.println("update slot state, garageID : " + garageID + ", slot Index : " + slotIdx + ", slot Status : " + slotStatus);
	}

    public void leaveSlotStatus(int garageID, int slotIdx)  throws Exception
    {
        SureParkConfig config = SureParkConfig.getInstance();
        ManagementDBTransaction mgtDB = ManagementDBTransaction.getInstance();

        // slot status update
        config.setGarageSlotState(garageID, slotIdx, GarageInfo.kGarageInfoSlotStatusOpen);

        // parking fee calculation
        mgtDB.leaveCar(garageID, slotIdx);

    }
	
	// CS -> MS
	public void setFacilityFailure(int garageID, boolean isFail) throws Exception {
		if (isFail)
			System.out.println("EVENT : " + garageID +" facility is broken...");
		else
			System.out.println("EVENT : " + garageID +" facility is fixed...");

		JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("garageNumber", garageID);

		jsonObject.put("detectFailure", jsonObject1);

        System.out.println(jsonObject.toJSONString());

		comm.broadcast(jsonObject.toJSONString() + "\n");

	}

    public void updateWrongParking(int garageID, int slotIdx) {

    }
}
