package com.lge.simple.WMSserver.Manager;

import java.util.HashMap;
import java.util.Map.Entry;

import com.lge.simple.WMSserver.WMSServer;
import com.lge.simple.WMSserver.DB.ConstantMessages;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.OrderInfo;
import com.lge.simple.WMSserver.Model.VirtualSharedMem;

public class OrderManager extends Thread {

	public OrderManager() {

	}

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				OrderInfo oi = WMSServer.WMSDB.getOneCandidateOrderInfo();
				if (oi != null) {
					// path 찾기
					String path = "";
					int wmsId = 1;
					for (Entry<String, Integer> ety : oi.items.entrySet()) {
						String key = ety.getKey();
						int val = ety.getValue().intValue();
						HashMap<Integer, Integer> map = WMSServer.WMSDB
								.getInventoryInfo(wmsId, key);
						for (Entry<Integer, Integer> invenety : map.entrySet()) {
							int invenid = invenety.getKey();
							int invenstock = invenety.getValue();
							//System.out.println(invenid +" at " + invenstock);
							if (invenstock > val) {
								path = path + wmsId + ":" + invenid + ":" + key
										+ ":" + val + ",";
							} else {
								path = path + wmsId + ":" + invenid + ":" + key
										+ ":" + invenstock + ",";
								val = val - invenstock;
							}
							//System.out.println();
						}
					}
					if (path.length() != 0)
						path = path.substring(0, path.length() - 1);

					int robotId = WMSServer.WMSDB.findIdleRobot(wmsId);
					
					Client wmscc = WMSServer.WMSCWMSServerthread.clients
							.getClientByClientID(String.valueOf(wmsId));
					Client rc = WMSServer.RWMSServerthread.clients
					.getClientByClientID(String.valueOf(wmsId)
							+ ',' + String.valueOf(robotId));
					if (rc == null)
						WMSServer.WMSDB.updateRobotErrorStatus(wmsId, robotId, "ERROR_CAUSE_WIFI_DISCONECT");

					if (robotId != -1  && wmscc  != null  &&  rc != null) {
						WMSServer.WMSDB.orderStatusChange(
								String.valueOf(oi.orderId),
								ConstantMessages.STATUS_INPROGRESS);
						WMSServer.WMSDB.updateOrderPath(oi.orderId, path);
						WMSServer.WMSDB.updateRobotStatus(wmsId, robotId,
								ConstantMessages.STATUS_INPROGRESS);
						WMSServer.WMSDB.updateRobotPathAndOrderNo(wmsId, robotId, path, oi.orderId); 
						VirtualSharedMem VSM = VirtualSharedMem.getInstance();
						VSM.wmsinfo[wmsId].moveToNextStation.add("0");
					}
				}
				try {
					sleep(2500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		} finally {

		}
	}
}
