package sureParkManager.managementService;

import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import sureParkManager.common.GarageInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class ManagementDBTransaction {
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
        doc.append("garageNumber", dbObject.get("garageNumber"));
        doc.append("slotNumber", dbObject.get("slotNumber"));
        BasicDBList docList = new BasicDBList();
        for(int i=0; i < (int)dbObject.get("slotNumber"); i++) {
            docList.add("Open");
        }
        doc.append("slotStatus", docList);
        doc.append("updateTime", new Date());
        doc.append("gracePeriod", dbObject.get("gracePeriod"));
        doc.append("parkingFee", 5);
        doc.append("garageIP", dbObject.get("garageIP"));
        doc.append("isAvailable", dbObject.get("isAvailable"));

		coll.insert(doc);
	}

	public void addNewReservation(String str) throws Exception {
		DBCollection coll = db.getCollection("reservations");

		DBObject dbObject = (DBObject) JSON.parse(str);
		BasicDBObject doc = new BasicDBObject();

        doc.append("userID", dbObject.get("userID"));
        doc.append("reservationTime", new Date());
        doc.append("cardInfo", dbObject.get("cardInfo"));
        doc.append("confirmInformation", dbObject.get("confirmInformation"));
        doc.append("gracePeriod", dbObject.get("gracePeriod"));
        doc.append("reservationStatus", "waiting");
        doc.append("parkingFee", 5);
        doc.append("usingGarage", dbObject.get("usingGarage"));
        doc.append("usingSlot", dbObject.get("usingSlot"));
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

	public void updateGarageSlot(int garageID, ArrayList<Integer> slotStatus) {
		DBCollection coll = db.getCollection("garages");
		GarageInfo garageInfo = new GarageInfo();
		BasicDBObject updateObj= new BasicDBObject();
        BasicDBList slotStatusList = new BasicDBList();

    	for(int i=0; i<slotStatus.size();i++) {
    		slotStatusList.add(garageInfo.GarageSlotStatusIntToString(slotStatus.get(i)));
    	}
    	
    	updateObj.append("$set", new BasicDBObject().append("slotStatus", slotStatusList));

		coll.update(new BasicDBObject().append("garageID", garageID), updateObj);
	}

    public ArrayList<ReservationInfo> getReservationInfo() {
        ArrayList<ReservationInfo> reservationInfoList = null;

        // TODO: implement this function.

        return reservationInfoList;
    }

	public void disconnectDB() throws Exception {
		mongoClient.close();

		isDBConnected = false;
	}
}
