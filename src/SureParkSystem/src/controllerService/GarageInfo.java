package controllerService;

import java.util.ArrayList;

public class GarageInfo {

	public int id;
	public String name;
	public String ip;
	public int port;
	public int slotNum;
	public ArrayList<Integer> slotStatus;
	
	public GarageInfo(int id, String name, String ip, int port, int slotNum) {
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.slotNum = slotNum;		
		this.slotStatus = new ArrayList<Integer>();
		
		for( int i = 0 ; i < this.slotNum ; i++ )
			this.slotStatus.add(0);		
	}

	

}
