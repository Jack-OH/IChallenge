package com.lge.simple.WMSserver.MsgHandler;

import com.lge.simple.WMSserver.WMSServer;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.VirtualSharedMem;

public class WMSCClientMsgHandler extends ClientMsgHandler {
	private static WMSCClientMsgHandler instance = new WMSCClientMsgHandler();

	// private static final int ASCII_LF = 0x0A;

	private static final int MSGSRC_FROM_WMSSERVER = 0x01;
	private static final int MSGSRC_FROM_WMSC = 0x03;

	private static final int ERROR_CAUSE_WIFI_DISCONECT = 0x00;
	private static final int ERROR_CAUSE_STATION_DETECT = 0x01;
	private static final int ERROR_CAUSE_SWITCH_DETECT = 0x02;
	private static final int ERROR_CAUSE_UNKNOWN = 0x10;

	// private static final int ERROR_SRC_FROM_ROBOT = 0x00;
	// private static final int ERROR_SRC_FROM_WAREHOUSE_CTRL = 0x01;

	private static final int CMD_READY = 0x01;
	private static final int CMD_ACKTOREADY = 0x02;
	private static final int CMD_ROBOTARRIVE = 0x03;
	private static final int CMD_STATIONJOBCOMPLETE = 0x04;
	private static final int CMD_PING = 0x05;
	private static final int CMD_ECHO = 0x06;

	private static final int CMD_ERROR = 0x07;

	private static final int MSG_OFFSET = 4;
	private static final int HEADER_SEQ = 0;

	private WMSCClientMsgHandler() {

	}

	public static WMSCClientMsgHandler getInstance() {
		return instance;
	}

	public String HandleMessage(String recvString, Client client) {


		/*
		 * VirtualSharedMem VSM = VirtualSharedMem.getInstance();
		 * VSM.q.add(recvString); if (true) return "";
		 */

		String returenMsg = "";

		int[] recvint = new int[recvString.length() / 2];

		int tempindex = 0;
		for (int i = 0; i < recvString.length(); i = i + 2) {
			String oneint = recvString.substring(i, i + 2);
			recvint[tempindex++] = Integer.parseInt(oneint, 16);
		}

		// recvint.length
		// recvint[0] : msg sequence number
		// recvint[1] : msg source
		// recvint[2] : msg id
		// recvint[3] : msg bodylength

		if (recvint[1] == MSGSRC_FROM_WMSC
				&& recvint[3] + MSG_OFFSET == recvint.length) {
			switch (recvint[2]) {
			case CMD_READY: {
				String macid = recvString.substring(8, recvString.length() - 2);
				String clientID = WMSServer.WMSDB.findWMSId(macid);
				System.out.println("WHC RCV READY :" + recvString);
				client.setClientID(clientID);
				returenMsg = AckToReady(recvint[HEADER_SEQ],
						Integer.valueOf(clientID));	
				System.out.println("WHC RCV ACKREADY :" + returenMsg);
			}
				break;
			case CMD_ROBOTARRIVE: {
				int warehouseId = recvint[MSG_OFFSET + 0];
				int stationId = recvint[MSG_OFFSET + 1];

				System.out.println("WHC RCV ARRIVED :" + recvString);

				// robot db에 위치 변경해서 기록
				if (WMSServer.WMSDB
						.setRobotStopLocation(warehouseId, stationId)) { //제대로 감지되었으면
					if (stationId != 0
							&& (WMSServer.WMSDB.findRobotLoadItemRequired(
									warehouseId, stationId)) == false) {
						VirtualSharedMem VSM = VirtualSharedMem.getInstance();
						VSM.wmsinfo[warehouseId].moveToNextStation.add(String
								.valueOf(stationId));
					}
				}
				// 실어야할 것 있으면 기다리고 없으면 바로 고
				// 로봇 DB에서 위치 수정
			}
				break;
			case CMD_STATIONJOBCOMPLETE: {
				int warehouseId = recvint[MSG_OFFSET + 0];
				int stationId = recvint[MSG_OFFSET + 1];

				System.out.println("WHC RCV JOBDONE :" + recvString);
				if (stationId == 0)
					WMSServer.WMSDB.setShippingCompleteJob(warehouseId);
				else {
					WMSServer.WMSDB.setLoadingCompleteJob(warehouseId,
							stationId);
					VirtualSharedMem VSM = VirtualSharedMem.getInstance();
					VSM.wmsinfo[warehouseId].moveToNextStation.add(String
							.valueOf(stationId));
				}
			}
				break;
			case CMD_ERROR: {
				int warehouseId = recvint[MSG_OFFSET + 0];
				int errorCause = recvint[MSG_OFFSET + 1];

				System.out.println("WHC RCV ERROR :" + recvString);
				String errorCauseString = "";
				if (errorCause == ERROR_CAUSE_WIFI_DISCONECT)
					errorCauseString = "ERROR_CAUSE_WIFI_DISCONECT";
				else if (errorCause == ERROR_CAUSE_STATION_DETECT)
					errorCauseString = "ERROR_CAUSE_STATION_DETECT";
				else if (errorCause == ERROR_CAUSE_SWITCH_DETECT)
					errorCauseString = "ERROR_CAUSE_SWITCH_DETECT";
				else if (errorCause == ERROR_CAUSE_UNKNOWN)
					errorCauseString = "ERROR_CAUSE_UNKNOWN";

				WMSServer.WMSDB.updateWMSControllerStatus(warehouseId,
						errorCauseString);
			}
				break;
			case CMD_PING: {

				System.out.println("WHC RCV PING :" + recvString);
				int warehouseId = recvint[4];
				returenMsg = MakeEcho(recvint[HEADER_SEQ], warehouseId);
				System.out.println("WHC SND ECHO :" + returenMsg);
			}
				break;
			default:
				break;
			}
			// returenMsg = "WMSController Client Doing : " + recvString + "\n"
			// + "Length : " + recvint.length + "\n tostring :" +
			// recvint.toString() + "\n";

			// System.out.println(returenMsg);
		} else {
			returenMsg = ReturnMsgBroken(recvint[HEADER_SEQ]);
		}
 

		return returenMsg;

	}

	private String MakeEcho(int seq, int warehouseId) {
		int[] sendint = new int[6];
		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_ECHO;
		sendint[3] = 2;
		sendint[4] = warehouseId;
		sendint[5] = makeCRC(sendint);
		return makeintTypeString(sendint);
	}

	private String ReturnMsgBroken(int seq) {
		int[] sendint = new int[5];
		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_ERROR;
		sendint[3] = 1;
		sendint[4] = makeCRC(sendint);
		return makeintTypeString(sendint);

	}

	private String AckToReady(int seq, int data) {
		int[] sendint = new int[6];
		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_ACKTOREADY;
		sendint[3] = 2;
		sendint[4] = data;
		sendint[5] = makeCRC(sendint);
		return makeintTypeString(sendint);

	}

	private String makeintTypeString(int[] sendint) {
		String returnStr = "";
		for (int b : sendint) {
			returnStr = returnStr + String.format("%02X", b);
		}
		return returnStr;
	}

	private int makeCRC(int[] data) {
		return 0;
	}
}
