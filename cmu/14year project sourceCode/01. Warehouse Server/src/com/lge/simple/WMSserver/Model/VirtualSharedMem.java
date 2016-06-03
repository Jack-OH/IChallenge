package com.lge.simple.WMSserver.Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class VirtualSharedMem {

	private static VirtualSharedMem instance = new VirtualSharedMem();
 	public VSM_WMSInfo [] wmsinfo = new VSM_WMSInfo [4];
 	
	
	private VirtualSharedMem() {
		wmsinfo[0] = new VSM_WMSInfo();
		wmsinfo[1] = new VSM_WMSInfo();

		wmsinfo[2] = new VSM_WMSInfo();
		wmsinfo[3] = new VSM_WMSInfo();
 	}
	
	public static VirtualSharedMem getInstance() {
		return instance;
	}
	
	public class VSM_WMSInfo{ 
		int botcnt;
		int stationcnt;
		//public boolean [] arrivedetectFromController = new boolean [10]; //max 10 station
		//public boolean [][] arrivenotityFromBot = new boolean [4][10];   //max 4EA, 10 Station
		public Queue<String> moveToNextStation = new LinkedList<String>(); 
	 	public  RobotMoveMsgInfo  robotMessageQueue = new  RobotMoveMsgInfo ();
	}
	

}
