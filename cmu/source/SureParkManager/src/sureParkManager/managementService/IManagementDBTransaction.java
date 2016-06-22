package sureParkManager.managementService;

import sureParkManager.common.GarageInfo;
import sureParkManager.common.ReservationInfo;

import java.util.ArrayList;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public interface IManagementDBTransaction {

    boolean isDBConnected();

    ArrayList<GarageInfo> getGaragesInfo() throws Exception;

    ArrayList<ReservationInfo> getReservationInfo();

    int getEmptyGarageID(String garageName);

    int getEmptyGarageSlotNum(int garageID);

    String getGarageName(int garageID);

    void addNewGarage(String str) throws Exception;

    void addNewReservation(String str) throws Exception;

    void cancelReservation(String confirmInformation);

    void addNewUser(String str) throws Exception;

    void updateGarageSlot(int garageID, int slotIndex, int slotStatus);

    void updateWrongParkingSlot(int garageID, int slot, String lastConfirmInfo) throws Exception;

    boolean parkingCar(String str, int garageID, int slot);

    void leaveWithParking(int garageID, int slot);

    void leaveWithoutParking(int garageID, int slot);

    void setFacilityAvailable(int garageID, boolean isAvail);

    void disconnectDB() throws Exception;
}
