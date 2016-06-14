package sureParkManager.managementService;

import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import sureParkManager.common.GarageInfo;

import java.util.ArrayList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

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
			GarageInfo garageInfo = new GarageInfo((int) dbObj.get("garageID"), (String) dbObj.get("garageName"),
					(String) dbObj.get("garageIP"), (int) dbObj.get("slotNumber"));
			garageInfoList.add(garageInfo);
		}

		return garageInfoList;
	}

	public void addNewGarage(String str) throws Exception {
		DBCollection coll = db.getCollection("garages");

		DBObject dbObject = (DBObject) JSON.parse(str);

		coll.insert(dbObject);
	}

	public void addNewReservation(String str) throws Exception {
		DBCollection coll = db.getCollection("reservations");

		DBObject dbObject = (DBObject) JSON.parse(str);

		coll.insert(dbObject);
	}

	public void addNewUser(String str) throws Exception {
		DBCollection coll = db.getCollection("users");

		DBObject dbObject = (DBObject) JSON.parse(str);

		coll.insert(dbObject);
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

	public void disconnectDB() throws Exception {
		mongoClient.close();

		isDBConnected = false;
	}
}
