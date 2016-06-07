package controllerService;

import java.util.HashMap;
import java.util.Map;

public class Config {
	
	private static Config instance = null;
	private Map<Integer, GarageInfo> garageMap;
	
	private Config() {
		garageMap = new HashMap<Integer, GarageInfo>();		
	}
	
	public static Config getInstance() {
		if( instance == null ) {
			synchronized (Config.class) {
				if( instance == null ) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
	public void putGarageInfo(int id, GarageInfo info) {
		garageMap.put(id, info);
	}	
	
	public int getGarageNum() {
		return garageMap.size();
	}
	
	public GarageInfo getGarageInfo(int id) {
		return garageMap.get(id);		
	}
	

}
