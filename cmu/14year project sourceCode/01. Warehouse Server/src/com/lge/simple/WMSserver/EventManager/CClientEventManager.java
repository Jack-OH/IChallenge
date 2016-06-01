package com.lge.simple.WMSserver.EventManager;
   
import com.lge.simple.WMSserver.Model.Clients;

public class CClientEventManager extends ClientEventManager{

	private static CClientEventManager instance = new CClientEventManager();
	
	private CClientEventManager(){
		
	}
	
	public static CClientEventManager getInstance(){
	      return instance;
	}
	
	@Override
	public void ManageEvent(Clients clients, int clientkey) {
		try {
			//Utility.unicast(clients.get(clientkey), "Client Event Occured" ); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	

}
