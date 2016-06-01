package com.lge.simple.WMSserver;

import com.lge.simple.WMSserver.DB.Database;
import com.lge.simple.WMSserver.Manager.OrderManager;
import com.lge.simple.WMSserver.Manager.RobotManager;

public class WMSServer {
	public static final int SupervisorPort = 9001;
	public static final int CustomerPort = 9000;
	public static final int WMSControllerPort = 9002;
	public static final int RobotPort = 9003;
	public static Database WMSDB = new Database("WMS.db");	
	
	static WMSServerthread CWMSServerthread;
	static WMSServerthread SWMSServerthread;
	public static WMSServerthread WMSCWMSServerthread;
	public static WMSServerthread RWMSServerthread;
	
	
	public static void main(String[] args) {
		WMSDB.init();

		CWMSServerthread = new WMSServerthread(CustomerPort);
		SWMSServerthread = new WMSServerthread(SupervisorPort);
		WMSCWMSServerthread = new WMSServerthread(WMSControllerPort);
		RWMSServerthread = new WMSServerthread(RobotPort);
		
		CWMSServerthread.start();
		SWMSServerthread.start();
		WMSCWMSServerthread.start();
		RWMSServerthread.start();
		
		new OrderManager().start();
		new RobotManager().start();
		//WMSDB.close();
	}
}
