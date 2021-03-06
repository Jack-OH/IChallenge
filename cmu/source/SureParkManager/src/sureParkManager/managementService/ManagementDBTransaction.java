package sureParkManager.managementService;

import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import sureParkManager.common.GarageInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import sureParkManager.common.ReservationInfo;
import sureParkManager.common.SureParkConfig;

/**
 * Created by jaeheonkim on 2016. 6. 14..
 */
public class ManagementDBTransaction implements IManagementDBTransaction{

    private static final String simpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static ManagementDBTransaction instance = null;

	private static MongoClient mongoClient = null;
	private static DB db = null;

	private static boolean isDBConnected;

	public boolean isDBConnected() {
		return isDBConnected;
	}

	public ManagementDBTransaction() throws Exception {
		// To connect to mongodb server
		mongoClient = new MongoClient("localhost", 27017);

		// Now connect to your databases
		db = mongoClient.getDB("SurePark");
		System.out.println("Connect to database successfully");

		isDBConnected = true;
	}

	public static ManagementDBTransaction getInstance() throws Exception {
		if (instance == null) {
			synchronized (ManagementDBTransaction.class) {
				if (instance == null) {
					instance = new ManagementDBTransaction();
				}
			}
		}
		return instance;
	}

	public ArrayList<GarageInfo> getGaragesInfo() throws Exception {

		DBCollection coll = db.getCollection("garages");
		ArrayList<GarageInfo> garageInfoList = new ArrayList<GarageInfo>();

		System.out.println("Collection garage selected successfully! count is " + coll.count());

		DBCursor cursor = coll.find();
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
            ArrayList<Integer> slotStatus = new ArrayList<>();

            BasicDBList dbList = (BasicDBList)dbObj.get("slotStatus");
            for(int i=0; i<(int)dbObj.get("slotNumber");i++) {
                slotStatus.add(GarageInfo.GrageSlotStatusStringToInt((String)dbList.get(i)));
            }

			GarageInfo garageInfo = new GarageInfo((int) dbObj.get("garageNumber"), (String) dbObj.get("garageName"),
					(String) dbObj.get("garageIP"), (int) dbObj.get("slotNumber"), slotStatus);

			garageInfoList.add(garageInfo);
		}

		return garageInfoList;
	}

    public ArrayList<ReservationInfo> getReservationInfo() {
        DBCollection coll = db.getCollection("reservations");
        BasicDBObject whereQuery = new BasicDBObject();

        ArrayList<ReservationInfo> reservationInfoList = new ArrayList<ReservationInfo>();

        whereQuery.put("reservationStatus", "waiting");

        DBCursor cursor = coll.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);

            String reservationTime = (String)dbObj.get("reservationTime");

            try {
                Date date = dateFormat.parse(reservationTime);

//                System.out.println(date.toString());
//                System.out.println((int)dbObj.get("gracePeriod"));
//                System.out.println((String)dbObj.get("confirmInformation"));

                ReservationInfo info = new ReservationInfo(
                        date,
                        (int)dbObj.get("gracePeriod"),
                        (String)dbObj.get("confirmInformation"));

                reservationInfoList.add(info);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return reservationInfoList;
    }

    public int getEmptyGarageID(String garageName) {
        int ret = -1;

        DBCollection garageColl = db.getCollection("garages");

        BasicDBObject whereQuery = new BasicDBObject("garageName", garageName);
        DBCursor cursor = garageColl.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            BasicDBList dbList = (BasicDBList)dbObj.get("slotStatus");

            int index = dbList.indexOf("Open");

            if (index < 0)
                continue;

            ret = (int)dbObj.get("garageNumber");
            break;
        }

        return ret;
    }

    public int getReservedGarageID(String confirmInformation) {
        int ret = -1;

        DBCollection garageColl = db.getCollection("reservations");

        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", confirmInformation);
        DBCursor cursor = garageColl.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            ret = (int) dbObj.get("usingGarageNumber");
        }

        return ret;
    }

    public int getEmptyGarageSlotNum(int garageID) {
        int ret = -1;

        DBCollection garageColl = db.getCollection("garages");

        BasicDBObject whereQuery = new BasicDBObject("garageNumber", garageID);
        DBCursor cursor = garageColl.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            BasicDBList dbList = (BasicDBList)dbObj.get("slotStatus");

            int index = dbList.indexOf("Open");

            if (index < 0)
                continue;

            ret = index;
            break;
        }

        return ret;
    }

    public int getReservedGarageSlotNum(String confirmInformation) {
        int ret = -1;

        DBCollection garageColl = db.getCollection("reservations");

        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", confirmInformation);
        DBCursor cursor = garageColl.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            ret = (int) dbObj.get("usingSlot");
        }

        return ret;
    }

    public String getGarageName(int garageID) {
        String ret = null;

        DBCollection coll = db.getCollection("garages");

        BasicDBObject whereQuery = new BasicDBObject("garageNumber", garageID);
        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            ret = (String) dbObj.get("garageName");
        }

        return ret;
    }

	public void addNewGarage(String str) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);
		DBCollection coll = db.getCollection("garages");

		DBObject dbObject = (DBObject) JSON.parse(str);
        BasicDBObject doc = new BasicDBObject();
        doc.append("garageName", dbObject.get("garageName"));
        doc.append("garageNumber", Integer.parseInt((String)dbObject.get("garageNumber")));
        doc.append("slotNumber", Integer.parseInt((String)dbObject.get("slotNumber")));
        BasicDBList slotStatusList = new BasicDBList();
        for(int i=0; i < Integer.parseInt((String)dbObject.get("slotNumber")); i++) {
            slotStatusList.add("Open");
        }
        doc.append("slotStatus", slotStatusList);
        BasicDBList updateTimeList = new BasicDBList();
        for(int i=0; i < Integer.parseInt((String)dbObject.get("slotNumber")); i++) {
            String updateTime = dateFormat.format(new Date());
            updateTimeList.add(updateTime);
        }
        doc.append("updateTime", updateTimeList);
        doc.append("gracePeriod", Integer.parseInt((String)dbObject.get("gracePeriod")));
        doc.append("parkingFee", 5);
        doc.append("garageIP", dbObject.get("garageIP"));
        doc.append("isAvailable", dbObject.get("isAvailable"));

		coll.insert(doc);
	}

	public void addNewReservation(String str, int garageID, int slot) throws Exception {
        DBCollection coll = db.getCollection("reservations");

		DBObject dbObject = (DBObject) JSON.parse(str);
		BasicDBObject doc = new BasicDBObject();

        doc.append("userID", dbObject.get("userID"));
        doc.append("reservationTime", dbObject.get("reservationTime"));
        doc.append("cardInfo", dbObject.get("cardInfo"));
        doc.append("confirmInformation", dbObject.get("confirmInformation"));
        doc.append("gracePeriod", Integer.parseInt((String)dbObject.get("gracePeriod")));
        doc.append("reservationStatus", "waiting");
        doc.append("parkingFee", Integer.parseInt((String)dbObject.get("parkingFee")));
        doc.append("usingGarage", dbObject.get("usingGarage"));
        doc.append("usingGarageNumber", garageID);
        doc.append("usingSlot", slot);
        doc.append("parkingTime", "null");
        doc.append("leaveTime", "null");
        doc.append("chargingFee", 0);

		coll.insert(doc);
	}

    public void cancelReservation(String confirmInformation)
    {
        DBCollection coll = db.getCollection("reservations");

        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", confirmInformation);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            BasicDBObject updateQuery= new BasicDBObject("$set", new BasicDBObject("reservationStatus", "cancelled"));

            coll.update(whereQuery, updateQuery);

            updateGarageSlot((int)dbObj.get("usingGarageNumber"), (int)dbObj.get("usingSlot"), GarageInfo.kGarageInfoSlotStatusOpen);
        }
    }

	public void addNewUser(String str) throws Exception {
		DBCollection coll = db.getCollection("users");

		DBObject dbObject = (DBObject) JSON.parse(str);
        BasicDBObject doc = new BasicDBObject();

        doc.append("userID", dbObject.get("userID"));
        doc.append("userPassword", dbObject.get("userPassword"));
        doc.append("userType", dbObject.get("userType"));
        doc.append("userName", dbObject.get("userName"));
        doc.append("userEmail", dbObject.get("userEmail"));
        doc.append("registerationTime", new Date().toString());
        doc.append("displayName", dbObject.get("displayName"));

		coll.insert(doc);
	}

	public void updateGarageSlot(int garageID, int slotIndex, int slotStatus) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);
		DBCollection coll = db.getCollection("garages");

        BasicDBObject whereQuery = new BasicDBObject("garageNumber", garageID);

        DBCursor cursor = coll.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();
            BasicDBList slotStatusList;
            BasicDBList updateTimeList;

            slotStatusList = (BasicDBList)dbObj.get("slotStatus");
            slotStatusList.put(slotIndex, GarageInfo.GarageSlotStatusIntToString(slotStatus));

            updateTimeList = (BasicDBList)dbObj.get("updateTime");
            String updateTime = dateFormat.format(new Date());
            updateTimeList.put(slotIndex, updateTime);

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("slotStatus", slotStatusList);
            updateObj.append("updateTime", updateTimeList);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);
        }
	}

    public void updateWrongParkingSlot(int garageID, int slot, String lastConfirmInfo) throws Exception {
        DBCollection resvColl   = db.getCollection("reservations");
        DBCollection garageColl = db.getCollection("garages");

        BasicDBObject resvWhereQuery = new BasicDBObject();

        resvWhereQuery.append("reservationStatus",  "parked");
        resvWhereQuery.append("usingGarageNumber",  garageID);
        resvWhereQuery.append("confirmInformation", lastConfirmInfo);

        DBCursor resvCursor = resvColl.find(resvWhereQuery);

        if (resvCursor.hasNext()) {
            DBObject resvDbObj = resvCursor.next();

            BasicDBObject garageWhereQuery = new BasicDBObject("garageNumber", garageID);

            DBCursor garageCursor = garageColl.find(garageWhereQuery);

            if (garageCursor.hasNext()) {
                DBObject garageDbObj = garageCursor.next();
                BasicDBList slotStatusList;

                slotStatusList = (BasicDBList) garageDbObj.get("slotStatus");

                int oldSlot = (int) resvDbObj.get("usingSlot");     // original designate slot.
                int oldStatus = GarageInfo.GrageSlotStatusStringToInt((String) slotStatusList.get(slot)); // wrong parking slot's old status

                updateGarageSlot(garageID, oldSlot, oldStatus); // original designate slot status update to wrong parking slot's old status

                // 이전 예약을 찾는다
                BasicDBObject oldWhereQuery = new BasicDBObject();
                oldWhereQuery.append("reservationStatus", "waiting");
                oldWhereQuery.append("usingGarageNumber", garageID);
                oldWhereQuery.append("usingSlot", slot);

                DBCursor cursor = resvColl.find(oldWhereQuery);

                if (cursor.hasNext()) {
                    BasicDBObject oldQuery = new BasicDBObject();
                    BasicDBObject oldObj = new BasicDBObject();

                    oldObj.append("usingGarageNumber", garageID);
                    oldObj.append("usingSlot", oldSlot);

                    oldQuery.append("$set", oldObj);

                    resvColl.update(oldWhereQuery, oldQuery);
                }

                // Update
                BasicDBObject updateQuery = new BasicDBObject();
                BasicDBObject updateObj = new BasicDBObject();

                updateObj.append("usingGarageNumber", garageID);
                updateObj.append("usingSlot", slot);

                updateQuery.append("$set", updateObj);

                resvColl.update(resvWhereQuery, updateQuery);
            }
        }
    }

    public boolean parkingCar(String str) {
        boolean ret = false;

        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);

        DBCollection coll = db.getCollection("reservations");
        DBObject dbObject = (DBObject) JSON.parse(str);
        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", dbObject.get("confirmInformation"));

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("reservationStatus", "parked");
            String parkingTime = dateFormat.format(new Date());
            updateObj.append("parkingTime", parkingTime);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);

            ret = true;
        }

        return ret;
    }

    public void leaveWithParking(int garageID, int slot) {
        DBCollection coll = db.getCollection("reservations");

        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("reservationStatus", "parked");
        whereQuery.append("usingGarageNumber", garageID);
        whereQuery.append("usingSlot", slot);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            String strParkingTime = (String)dbObj.get("parkingTime");

            try {
                Date parkingTime = dateFormat.parse(strParkingTime);
                Date leaveTime = new Date();
                double parkingFee = (int)dbObj.get("parkingFee");

                long diff = leaveTime.getTime() - parkingTime.getTime();
                long diffMin = diff / (60 * 1000); // Calculate a minute unit.
                double chargingFee = 0;

                if ((diffMin % 30) != 0) {
                    chargingFee = (double) (parkingFee / 2 * ((diffMin/30)+1)); // per 30 min..
                } else {
                    chargingFee = (double) (parkingFee / 2 * (diffMin/30)); // per 30 min..
                }

                BasicDBObject updateQuery = new BasicDBObject();
                BasicDBObject updateObj = new BasicDBObject();

                updateObj.append("reservationStatus", "leaved");
                String strLeaveTime = dateFormat.format(leaveTime);
                updateObj.append("leaveTime", strLeaveTime);
                updateObj.append("chargingFee", chargingFee);

                updateQuery.append("$set", updateObj);

                coll.update(whereQuery, updateQuery);

                System.out.println("==========================");
                System.out.println("     Sure Park Garage");
                System.out.println("  Charging Fee is $" + chargingFee);
                System.out.println("==========================");

//                MailService mail = new MailService();

//                try {
//                    mail.sendCharginFeeMail("acmoleg@gmailc.com", chargingFee);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void leaveWithoutParking(int garageID, int slot) {
        DBCollection coll = db.getCollection("reservations");

        SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("reservationStatus", "parked");
        whereQuery.append("usingGarageNumber", garageID);
        whereQuery.append("usingSlot", slot);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            Date leaveTime = new Date();

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("reservationStatus", "refused");
            String strLeaveTime = dateFormat.format(leaveTime);
            updateObj.append("leaveTime", strLeaveTime);
            updateObj.append("chargingFee", 0);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);

            updateGarageSlot(garageID, slot, GarageInfo.kGarageInfoSlotStatusOpen);
        }
    }

    public void setFacilityAvailable(int garageID, boolean isAvail) {
        DBCollection coll = db.getCollection("garages");

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("garageNumber", garageID);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            BasicDBObject updateQuery = new BasicDBObject("$set", new BasicDBObject("isAvailable", isAvail));

            coll.update(whereQuery, updateQuery);
        }
    }

	public void disconnectDB() throws Exception {
		mongoClient.close();

		isDBConnected = false;
	}
}
