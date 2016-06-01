package com.lge.simple.WMSserver.EventManager;
 
import com.lge.simple.WMSserver.Model.Clients;  

public class SClientEventManager extends ClientEventManager {
	private static SClientEventManager instance = new SClientEventManager();

	private SClientEventManager() {

	}

	public static SClientEventManager getInstance() {
		return instance;
	}

	@Override
	public void ManageEvent(Clients  clients, int clientkey) {
		try {
			//Utility.unicast(clients.get(clientkey), "Server Event Occured");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
