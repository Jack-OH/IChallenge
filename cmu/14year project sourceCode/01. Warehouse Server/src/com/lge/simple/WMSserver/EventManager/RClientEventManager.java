package com.lge.simple.WMSserver.EventManager;

import com.lge.simple.WMSserver.Model.Clients; 
public class RClientEventManager extends ClientEventManager{
	private static RClientEventManager instance = new RClientEventManager();
	
	private RClientEventManager(){
		
	}
	
	public static RClientEventManager getInstance(){
	      return instance;
	}

	@Override
	public void ManageEvent(Clients  clients, int clientkey) {
		try { 
		/*	VirtualSharedMem VSM = VirtualSharedMem.getInstance();
			if (!VSM.q.isEmpty())
				Utility.unicast(clients.get(clientkey), "Robot Event Occured" + VSM.q.remove() );*/
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
	}

}
