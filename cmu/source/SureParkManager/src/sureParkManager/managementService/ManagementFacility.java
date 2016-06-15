package sureParkManager.managementService;

import org.json.simple.JSONObject;
import sureParkManager.common.SureParkConfig;

public class ManagementFacility extends AbstractManagementFacility {
	ServerSender serverSender = null;

	public ManagementFacility(){
		serverSender = new ServerSender();
	}

	// CS -> MS
	public void updateSlotStatus(int garageID, int slotIdx, int slotStatus)  throws Exception
	{
		SureParkConfig config = SureParkConfig.getInstance();
		config.setGarageSlotState(garageID, slotIdx, slotStatus);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("updateSlotStatus", new JSONObject().put("garageID", garageID));

		serverSender.write(jsonObject.toJSONString());
		
		System.out.println("update slot state, garageID : " + garageID + ", slot Index : " + slotIdx + ", slot Status : " + slotStatus);
	}
	
	// CS -> MS
	public void setFacilityFailure(int garageID, boolean isFail) throws Exception {
		if (isFail)
			System.out.println("EVENT : " + garageID +" facility is broken...");
		else
			System.out.println("EVENT : " + garageID +" facility is fixed...");

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("detectFailure", new JSONObject().put("garageID", garageID));

		serverSender.write(jsonObject.toJSONString());

	}
}
