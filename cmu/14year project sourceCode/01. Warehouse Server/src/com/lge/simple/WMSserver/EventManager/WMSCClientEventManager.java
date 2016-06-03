package com.lge.simple.WMSserver.EventManager;
 
import com.lge.simple.WMSserver.Model.Clients; 
public class WMSCClientEventManager extends ClientEventManager{
	private static WMSCClientEventManager instance = new WMSCClientEventManager();
	
	private WMSCClientEventManager(){
		
	}
	
	public static WMSCClientEventManager getInstance(){
	      return instance;
	}

	@Override
	public void ManageEvent(Clients  clients, int clientkey) {
		try { 
			//Utility.unicast(clients.get(clientkey), "WMSC Event Occured" );
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
	}

}
