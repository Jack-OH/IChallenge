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

public class ManagementDBTransaction {

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
			GarageInfo garageInfo = new GarageInfo((int) dbObj.get("garageNumber"), (String) dbObj.get("garageName"),
					(String) dbObj.get("garageIP"), (int) dbObj.get("slotNumber"));
			garageInfoList.add(garageInfo);
		}

		return garageInfoList;
	}

	public void addNewGarage(String str) throws Exception {
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
            updateTimeList.add(new Date());
        }
        doc.append("updateTime", updateTimeList);
        doc.append("gracePeriod", Integer.parseInt((String)dbObject.get("gracePeriod")));
        doc.append("parkingFee", 5);
        doc.append("garageIP", dbObject.get("garageIP"));
        doc.append("isAvailable", dbObject.get("isAvailable"));

		coll.insert(doc);
	}

	public void addNewReservation(String str) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat, Locale.US);
		DBCollection coll = db.getCollection("reservations");

		DBObject dbObject = (DBObject) JSON.parse(str);
		BasicDBObject doc = new BasicDBObject();

        doc.append("userID", dbObject.get("userID"));

        String strDate = (String)dbObject.get("reservationTime");
        System.out.println(strDate);
        Date date = format.parse(strDate);
        doc.append("reservationTime", date);
        doc.append("cardInfo", dbObject.get("cardInfo"));
        doc.append("confirmInformation", dbObject.get("confirmInformation"));
        doc.append("gracePeriod", Integer.parseInt((String)dbObject.get("gracePeriod")));
        doc.append("reservationStatus", "waiting");
        doc.append("parkingFee", Integer.parseInt((String)dbObject.get("parkingFee")));
        doc.append("usingGarage", dbObject.get("usingGarage"));
        doc.append("usingGarageNumber", -1);
        doc.append("usingSlot", -1);
        doc.append("parkingTime", "null");
        doc.append("leaveTime", "null");
        doc.append("chargingFee", 0);

		coll.insert(doc);
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
        doc.append("registerationTime", new Date());
        doc.append("displayName", dbObject.get("displayName"));

		coll.insert(doc);
	}

	public void updateGarageSlot(int garageID, int slotIndex, int slotStatus) {
		DBCollection coll = db.getCollection("garages");
		GarageInfo garageInfo = new GarageInfo();

        BasicDBObject whereQuery = new BasicDBObject("garageNumber", garageID);

        DBCursor cursor = coll.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            BasicDBList slotStatusList = new BasicDBList();
            BasicDBList updateTimeList = new BasicDBList();

            slotStatusList.add(dbObj.get("slotStatus"));
            slotStatusList.add(slotIndex, garageInfo.GarageSlotStatusIntToString(slotStatus));

            updateTimeList.add(dbObj.get("updateTime"));
            updateTimeList.add(slotIndex, new Date());

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("slotStatus", slotStatusList);
            updateObj.append("updateTime", updateTimeList);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);
        }
	}

    public ArrayList<ReservationInfo> getReservationInfo() {
        DBCollection coll = db.getCollection("reservations");
        BasicDBObject whereQuery = new BasicDBObject();

        ArrayList<ReservationInfo> reservationInfoList = new ArrayList<ReservationInfo>();

        whereQuery.put("reservationStatus", "waiting");

        DBCursor cursor = coll.find(whereQuery);

        while(cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat, Locale.US);

			String formatted = format.format(dbObj.get("reservationTime"));

            try {
                Date date = format.parse(formatted);

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

    public boolean parkingCar(String str, int garageID, int slot) {
        boolean ret = false;


        DBCollection coll = db.getCollection("reservations");
        DBObject dbObject = (DBObject) JSON.parse(str);
        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", dbObject.get("confirmInformation"));

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("reservationStatus", "parked");
            updateObj.append("parkingTime", new Date());
            updateObj.append("usingGarageNumber", garageID);
            updateObj.append("usingSlot", slot);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);

            ret = true;
        }

        return ret;
    }

    public void cancelReservation(String confirmInformation)
    {
        DBCollection coll = db.getCollection("reservations");

        BasicDBObject whereQuery = new BasicDBObject("confirmInformation", confirmInformation);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            BasicDBObject updateQuery= new BasicDBObject("$set", new BasicDBObject("reservationStatus", "cancelled"));

            coll.update(whereQuery, updateQuery);
        }
    }

    public void leaveWithParking(int garageID, int slot) {
        DBCollection coll = db.getCollection("reservations");

        SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat, Locale.US);

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("reservationStatus", "parked");
        whereQuery.append("usingGarageNumber", garageID);
        whereQuery.append("usingSlot", slot);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            DBObject dbObj = cursor.next();

            String formatted = format.format(dbObj.get("parkingTime"));

            try {
                Date parkingTime = format.parse(formatted);
                Date leaveTime = new Date();
                double parkingFee = (int)dbObj.get("parkingFee");

                long diff = leaveTime.getTime() - parkingTime.getTime();
                long diffHour = diff / (30 * 60 * 1000); // Calculate a half hour unit.

                int chargingFee = (int)(parkingFee/2 * (diffHour+1)); // per 30 min..

                BasicDBObject updateQuery = new BasicDBObject();
                BasicDBObject updateObj = new BasicDBObject();

                updateObj.append("reservationStatus", "leaved");
                updateObj.append("leaveTime", leaveTime);
                updateObj.append("chargingFee", chargingFee);

                updateQuery.append("$set", updateObj);

                coll.update(whereQuery, updateQuery);

                System.out.println("Charging Fee is $"+chargingFee);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void leaveWithoutParking(int garageID, int slot) {
        DBCollection coll = db.getCollection("reservations");

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("reservationStatus", "parked");
        whereQuery.append("usingGarageNumber", garageID);
        whereQuery.append("usingSlot", slot);

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            Date leaveTime = new Date();

            BasicDBObject updateQuery = new BasicDBObject();
            BasicDBObject updateObj = new BasicDBObject();

            updateObj.append("reservationStatus", "cancelled");
            updateObj.append("leaveTime", leaveTime);
            updateObj.append("chargingFee", 0);

            updateQuery.append("$set", updateObj);

            coll.update(whereQuery, updateQuery);
        }
    }

    public void updatePakingSlot(int garageID, int slot) throws Exception {
        DBCollection coll = db.getCollection("reservations");
        SureParkConfig config = SureParkConfig.getInstance();

        BasicDBObject whereQuery = new BasicDBObject();

        whereQuery.append("reservationStatus", "parked");
        whereQuery.append("usingGarageNumber", garageID);
        whereQuery.append("confirmInformation", config.getLastConfirmInfo());

        DBCursor cursor = coll.find(whereQuery);

        if (cursor.hasNext()) {
            BasicDBObject updateQuery = new BasicDBObject("$set", new BasicDBObject("usingSlot", slot));

            coll.update(whereQuery, updateQuery);
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
