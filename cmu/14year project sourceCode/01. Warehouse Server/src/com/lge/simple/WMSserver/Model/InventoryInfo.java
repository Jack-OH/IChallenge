package com.lge.simple.WMSserver.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class InventoryInfo {
	
	int	wmsId;
	String itemName;
	ArrayList <countInInventoryStation> detailinfo;
		
	public InventoryInfo(int wmsId , String itemName ) {
		this.wmsId = wmsId;
		this.itemName = itemName;
		detailinfo = new ArrayList <countInInventoryStation> ();
			
	}
	
	public void add(int stationId, int count)
	{
		for (countInInventoryStation info : detailinfo)
		{
			if (info.stationId == stationId) {
				info.count = info.count + count;
				return;
			}
		}
		countInInventoryStation info  = new countInInventoryStation();
		info.stationId = stationId;
		info.count = count;
		detailinfo.add(info);		
	}
	
	public HashMap<Integer, Integer> getResult()
	{
		HashMap<Integer, Integer>  result = new HashMap<Integer, Integer> ();
		for (countInInventoryStation info : detailinfo)
		{
			 result.put(info.stationId,info.count);
		}		
		return result;
	}

	class countInInventoryStation{
		int stationId;
		int count;
	}
	
}
