package com.lge.simple.WMSserver.MsgHandler;

import com.lge.simple.WMSserver.WMSServer;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.RobotMoveMsgInfo;
import com.lge.simple.WMSserver.Model.VirtualSharedMem;

public class RClientMsgHandler extends ClientMsgHandler {
	private static RClientMsgHandler instance = new RClientMsgHandler();

	// private static final int ASCII_LF = 0x0A;

	public static final int MSGSRC_FROM_WMSSERVER = 0x01;
	private static final int MSGSRC_FROM_ROBOT = 0x02;

	private static final int ERROR_CAUSE_WIFI_DISCONECT = 0x00;
	private static final int ERROR_CAUSE_ROBOT_FLOATING = 0x01;
	private static final int ERROR_CAUSE_UNKNOWN = 0x10;

	public static final int CMD_MOVE_TO_NEXT_STATION = 0x11;
	private static final int CMD_NOTIFY_ROBOT_STATUS = 0x12;
	private static final int CMD_NOTIFY_ROBOT_ERROR = 0x13;
	public static final int CMD_MANUALMODE_ONOFF = 0x14;
	public static final int CMD_MANUALMODE_CONTROL = 0x15;
	private static final int CMD_READY = 0x16;
	private static final int CMD_ACKTOREADY = 0x17;
	private static final int CMD_PING = 0x18;
	private static final int CMD_ECHO = 0x19;

	private static final int MSG_OFFSET = 4;
	private static final int HEADER_SEQ = 0;

	private RClientMsgHandler() {

	}

	public static RClientMsgHandler getInstance() {
		return instance;
	}

	public String HandleMessage(String recvString, Client client) {
		try {

			String returenMsg = "";

			int[] recvint = new int[recvString.length() / 2];

			int tempindex = 0;
			for (int i = 0; i < recvString.length(); i = i + 2) {
				String oneint = recvString.substring(i, i + 2);
				recvint[tempindex++] = Integer.parseInt(oneint, 16);
			}

			if (recvint[1] == MSGSRC_FROM_WMSSERVER) {
				VirtualSharedMem VSM = VirtualSharedMem.getInstance();

				System.out.println("In VM : "
						+ VSM.wmsinfo[1].robotMessageQueue.msg);
				if (VSM.wmsinfo[1].robotMessageQueue.dst.equals("1,1")
						&& VSM.wmsinfo[1].robotMessageQueue.msg
								.equals(recvString)) {
					System.out.println("Deleted msg : " + recvString);
					VSM.wmsinfo[1].robotMessageQueue.dst = "";
					VSM.wmsinfo[1].robotMessageQueue.msg = "";
					return "";
				}

			} else if (recvint[1] == MSGSRC_FROM_ROBOT
					&& recvint[3] + MSG_OFFSET == recvint.length) {
				switch (recvint[2]) {
				/*
				 * case CMD_MOVE_TO_NEXT_STATION: break;
				 */
				case CMD_NOTIFY_ROBOT_STATUS: // arrived
				{
					/*
					 * 일단 아무 작업 안함 int warehouseId = recvint[MSG_OFFSET + 0];
					 * int robotId = recvint[MSG_OFFSET + 1]; int stationId =
					 * recvint[MSG_OFFSET + 2];
					 */
					// WMSServer.WMSDB.updateRobotLocation(warehouseId, robotId,
					// stationId, stationId);
					System.out.println("BOT RCV BOTARRIVED " + recvString);
				}
					break;
				case CMD_NOTIFY_ROBOT_ERROR: {
					int warehouseId = recvint[MSG_OFFSET + 0];
					int robotId = recvint[MSG_OFFSET + 1];
					int errorCause = recvint[MSG_OFFSET + 2];
					String errorCauseString = "";
					if (errorCause == ERROR_CAUSE_WIFI_DISCONECT)
						errorCauseString = "ERROR_CAUSE_WIFI_DISCONECT";
					else if (errorCause == ERROR_CAUSE_ROBOT_FLOATING)
						errorCauseString = "ERROR_CAUSE_ROBOT_FLOATING";
					else if (errorCause == ERROR_CAUSE_UNKNOWN)
						errorCauseString = "ERROR_CAUSE_UNKNOWN";
					WMSServer.WMSDB.updateRobotErrorStatus(warehouseId,
							robotId, errorCauseString);
					System.out.println("BOT RCV ERROR " + recvString);

				}
					break;
				case CMD_READY: {
					String macid = recvString.substring(8,
							recvString.length() - 2);
					System.out.println("BOT RCV READY " + recvString);
					String clientID = WMSServer.WMSDB.findRobotId(macid);
					client.setClientID(clientID);
					int warehouseId = Integer.valueOf(clientID.substring(0,
							clientID.indexOf(',')));
					int robotId = Integer.valueOf(clientID.substring(clientID
							.indexOf(',') + 1));
					returenMsg = AckToReady(recvint[HEADER_SEQ], warehouseId,
							robotId);

					System.out.println("BOT SND ACKREADY" + returenMsg);
					WMSServer.WMSDB.updateRobotErrorStatus(warehouseId,
							robotId, "");
					// WMSServer.WMSDB.updateRobotStatus(warehouseId, robotId,
					// ConstantMessages.ROBOT_IDLE);
				}
					break;
				case CMD_PING: {
					int warehouseId = recvint[MSG_OFFSET + 0];
					int robotId = recvint[MSG_OFFSET + 1];
					System.out.println("BOT RCV PING" + recvString);
					returenMsg = MakeEcho(recvint[HEADER_SEQ], warehouseId,
							robotId);

					System.out.println("BOT SND ECHO" + returenMsg);
				}
				default:
					break;
				}
				// System.out.println(returenMsg);
			} else {
				returenMsg = ReturnMsgBroken(recvint[HEADER_SEQ]);
			}

			return returenMsg;
		} catch (Exception e) {
			System.out.println("Gabage? :" + e.toString());
			return "";
		}

	}

	private String MakeEcho(int seq, int warehouseId, int robotId) {
		int[] sendint = new int[7];
		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_ECHO;
		sendint[3] = 2;
		sendint[4] = warehouseId;
		sendint[5] = robotId;
		sendint[6] = makeCRC(sendint);
		return makeintTypeString(sendint);
	}

	private String AckToReady(int seq, int warehouseId, int robotId) {
		int[] sendint = new int[7];

		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_ACKTOREADY;
		sendint[3] = 2;
		sendint[4] = warehouseId;
		sendint[5] = robotId;
		sendint[6] = makeCRC(sendint);
		return makeintTypeString(sendint);
	}

	private String ReturnMsgBroken(int seq) {
		int[] sendint = new int[5];
		sendint[0] = seq;
		sendint[1] = MSGSRC_FROM_WMSSERVER;
		sendint[2] = CMD_NOTIFY_ROBOT_ERROR;
		sendint[3] = 0;
		sendint[4] = makeCRC(sendint);
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
