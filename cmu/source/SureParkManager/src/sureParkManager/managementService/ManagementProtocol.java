package sureParkManager.managementService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sureParkManager.common.GarageInfo;
import sureParkManager.common.SureParkConfig;
import sureParkManager.controlService.ControlService;
import sureParkManager.controlService.IControlService;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ManagementProtocol implements IManagementProtocol {
    private IControlService ctlService = null;
    private IManagementDBTransaction mgtDBtr;

    public ManagementProtocol(IControlService ctlService) {
        this.ctlService = ctlService;
        try {
            mgtDBtr = ManagementDBTransaction.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseJson(String inputLine) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(inputLine);
        JSONObject jsonObject = (JSONObject) obj;

        System.out.println("\nnow json parsing...\n");
        System.out.println("JSON data : " + inputLine);

        // json parsing...
        JSONObject subJsonObject;
        JSONObject retJsonObj = new JSONObject();
        subJsonObject = (JSONObject) jsonObject.get("newGarage");

        //System.out.println(subJsonObject.toJSONString());

        if (subJsonObject != null) {
            mgtDBtr.addNewGarage(subJsonObject.toJSONString());
            retJsonObj.put("newGarage", "OK");

            // Update Config.
            SureParkConfig config = SureParkConfig.getInstance();
            config.updateGarageInfo(); // update from DB

            // Notify Garage Info updated.
            try {
                ctlService.addFacility(Integer.parseInt((String) subJsonObject.get("garageNumber")));
            } catch (Exception e){
                System.out.println("Cannot add facility");
            }
        }

        subJsonObject = (JSONObject) jsonObject.get("newReservation");
        if (subJsonObject != null) {
            int garageID, slot;

            garageID = mgtDBtr.getEmptyGarageID((String)subJsonObject.get("usingGarage"));
            if (garageID < 0) {
                retJsonObj.put("parkingCar", "FAIL");
            } else {

                slot = mgtDBtr.getEmptyGarageSlotNum(garageID);
                if (slot < 0) {
                    retJsonObj.put("parkingCar", "FAIL");
                } else {

                    mgtDBtr.updateGarageSlot(garageID, slot, GarageInfo.kGarageInfoSlotStatusReserved);

                    mgtDBtr.addNewReservation(subJsonObject.toJSONString(), garageID, slot);
                    retJsonObj.put("newReservation", "OK");
                }
            }
        }

        subJsonObject = (JSONObject) jsonObject.get("newUser");
        if (subJsonObject != null) {
            mgtDBtr.addNewUser(subJsonObject.toJSONString());
            retJsonObj.put("newUser", "OK");
        }

        subJsonObject = (JSONObject) jsonObject.get("cancelReservation");
        if (subJsonObject != null) {
            retJsonObj.put("cancelReservation", "OK");
        }

        subJsonObject = (JSONObject) jsonObject.get("parkingCar");
        if (subJsonObject != null) {
            int garageID, slot;

            garageID = mgtDBtr.getReservedGarageID((String)subJsonObject.get("confirmInformation"));
            if (garageID < 0) {
                retJsonObj.put("parkingCar", "FAIL");
            } else {

                slot = mgtDBtr.getReservedGarageSlotNum((String)subJsonObject.get("confirmInformation"));
                if (slot < 0) {
                    retJsonObj.put("parkingCar", "FAIL");
                } else {
                    // DB Update
                    boolean ret = mgtDBtr.parkingCar(subJsonObject.toJSONString());

                    if (ret) {
                        System.out.println("Garage ID " + garageID + ", Empty slot number is " + slot);

                        SureParkConfig config = SureParkConfig.getInstance();
                        config.setLastConfirmInfo((String)subJsonObject.get("confirmInformation"));

                        // open gate
                        try {
                            ctlService.openEntryGate(garageID, slot);

                            retJsonObj.put("parkingCar", "OK");
                        } catch (Exception e) {
                            System.err.println("Cannot control facility!!!");
                            retJsonObj.put("parkingCar", "FAIL");
                        }
                    }
                    else
                    {
                        retJsonObj.put("parkingCar", "FAIL");
                    }
                }
            }
        }

        // Send response
        if (!retJsonObj.isEmpty()) {
            String outStr = retJsonObj.toJSONString() + "\n";
            System.out.println(outStr);
            return outStr;
        }
        else
            return retJsonObj.toJSONString();
    }
}
