package com.lge.simple.WMSserver.Manager;

import java.io.BufferedWriter;
import java.io.IOException;

import com.lge.simple.WMSserver.WMSServer;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.OverrideOrderQueue;
import com.lge.simple.WMSserver.Model.RobotMoveInfo;
import com.lge.simple.WMSserver.Model.RobotMoveMsgInfo;
import com.lge.simple.WMSserver.Model.VirtualSharedMem;
import com.lge.simple.WMSserver.MsgHandler.RClientMsgHandler;

public class RobotManager extends Thread {

	int seq = 0;

	private static final int MANUAL_MODE_ON = 0x00;
	private static final int MANUAL_MODE_OFF = 0x01;
	private static final int MOVE_TO_FORWARD = 0x00;
	private static final int MOVE_TO_SLIGHT_RIGHT_TURN = 0x01;
	private static final int MOVE_TO_SLIGHT_LEFT_TURN = 0x02;
	private static final int MOVE_TO_BACKWARD = 0x03;

	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				OverrideCheckAndSend();
				MoveRequestCheckAndSend();
				RobotMovoRequestSend();
				try {
					sleep(400);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			;
		} finally {

		}
	}

	private void RobotMovoRequestSend() {
		try {
			VirtualSharedMem VSM = VirtualSharedMem.getInstance();

			if (VSM.wmsinfo[1].robotMessageQueue.dst != "") {
				Client robotClient = WMSServer.RWMSServerthread.clients
						.getClientByClientID(VSM.wmsinfo[1].robotMessageQueue.dst);
				if (robotClient != null) {
					((BufferedWriter) (robotClient).getOs())
							.write(VSM.wmsinfo[1].robotMessageQueue.msg);
					((BufferedWriter) (robotClient).getOs()).newLine();
					((BufferedWriter) (robotClient).getOs()).flush();
					System.out.println("BOT SND Move :"
							+ VSM.wmsinfo[1].robotMessageQueue.msg);
				}
			}
			// else
			// WMSServer.WMSDB.updateRobotErrorStatus(rmi.warehouseId,
			// rmi.robotId, "ERROR_CAUSE_WIFI_DISCONECT");
			// todo :: r.dst에서 , 단위로 wmsid, botid 분리
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void MoveRequestCheckAndSend() {
		try {
			VirtualSharedMem VSM = VirtualSharedMem.getInstance();
			String target = VSM.wmsinfo[1].moveToNextStation.poll();
			if (target != null) {
				System.out.println("Goto :" + target + "-->");

				RobotMoveInfo rmi = WMSServer.WMSDB.getRobotMoveInfo(1, target);
				if (rmi != null) {
					// System.out.println("get client:"
					// + String.valueOf(rmi.warehouseId) + ','
					// + String.valueOf(rmi.robotId));

					Client robotClient = WMSServer.RWMSServerthread.clients
							.getClientByClientID(String
									.valueOf(rmi.warehouseId)
									+ ','
									+ String.valueOf(rmi.robotId));

					if (robotClient == null)
						WMSServer.WMSDB.updateRobotErrorStatus(rmi.warehouseId,
								rmi.robotId, "ERROR_CAUSE_WIFI_DISCONECT");

					if (WMSServer.WMSCWMSServerthread.clients
							.getClientByClientID(String
									.valueOf(rmi.warehouseId)) != null
							&& robotClient != null) {

						String resqMsg = "";
						resqMsg = MakeMovePacket(rmi.warehouseId, rmi.robotId,
								rmi.dstStationId);

						VSM.wmsinfo[1].robotMessageQueue.dst = String
								.valueOf(rmi.warehouseId)
								+ ','
								+ String.valueOf(rmi.robotId);
						VSM.wmsinfo[1].robotMessageQueue.msg = resqMsg;
						WMSServer.WMSDB.SetRobotMoveInfo(rmi);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String MakeMovePacket(int warehouseId, int robotId, int dstStationId) {
		int[] sendint = new int[8];
		sendint[0] = (int) seq;
		sendint[1] = RClientMsgHandler.MSGSRC_FROM_WMSSERVER;
		sendint[2] = RClientMsgHandler.CMD_MOVE_TO_NEXT_STATION;
		sendint[3] = 4;
		sendint[4] = warehouseId;
		sendint[5] = robotId;
		sendint[6] = dstStationId;
		sendint[7] = makeCRC(sendint);
		return makeintTypeString(sendint);
	}

	private void OverrideCheckAndSend() {
		try {
			OverrideOrderQueue ooq = OverrideOrderQueue.getInstance();
			String ordertext = ooq.command.poll();
			if (ordertext != null) {
				int wmsId = Integer.valueOf(ordertext.substring(0,
						ordertext.indexOf(',')));
				ordertext = ordertext.substring(ordertext.indexOf(',') + 1);
				int robotId = Integer.valueOf(ordertext.substring(0,
						ordertext.indexOf(',')));
				String detailOrder = ordertext
						.substring(ordertext.indexOf(',') + 1);

				Client robotClient = WMSServer.RWMSServerthread.clients
						.getClientByClientID(String.valueOf(wmsId) + ','
								+ String.valueOf(robotId));

				if (robotClient == null)
					WMSServer.WMSDB.updateRobotErrorStatus(wmsId, robotId,
							"ERROR_CAUSE_WIFI_DISCONECT");

				if (WMSServer.WMSCWMSServerthread.clients
						.getClientByClientID(String.valueOf(wmsId)) != null
						&& robotClient != null) {
					// 로봇에게 명령 전달
					String resqMsg = "";
					resqMsg = MakeRobotManualModeOnOffPacket(wmsId, robotId,
							detailOrder);
					// override가 off이면 에러 복구
					if (detailOrder != null && detailOrder.equals("off")) {
						WMSServer.WMSDB.updateRobotErrorStatus(wmsId, robotId,
								"");
					}

					((BufferedWriter) robotClient.getOs()).write(resqMsg);
					((BufferedWriter) robotClient.getOs()).newLine();
					((BufferedWriter) robotClient.getOs()).flush();
					System.out.println("BOT SND Overide :" + resqMsg);
				} else {
					// todo : 명령 전달하려 뺐는데 없으면...
				}
			}
		} catch (Exception e) {

		}
	}

	private String MakeRobotManualModeOnOffPacket(int wmsId, int robotId,
			String detailOrder) {
		int[] sendint = new int[8];
		sendint[0] = (int) seq;
		sendint[1] = RClientMsgHandler.MSGSRC_FROM_WMSSERVER;
		if ((detailOrder != null)
				&& (detailOrder.equals("on") || detailOrder.equals("off")))
			sendint[2] = RClientMsgHandler.CMD_MANUALMODE_ONOFF;
		else
			sendint[2] = RClientMsgHandler.CMD_MANUALMODE_CONTROL;
		sendint[3] = 4;
		sendint[4] = (int) wmsId;
		sendint[5] = (int) robotId;
		switch (detailOrder) {
		case "on":
			sendint[6] = MANUAL_MODE_ON;
			break;
		case "off":
			sendint[6] = MANUAL_MODE_OFF;
			break;
		case "forward":
			sendint[6] = MOVE_TO_FORWARD;
			break;
		case "backward":
			sendint[6] = MOVE_TO_BACKWARD;
			break;
		case "right-turn":
			sendint[6] = MOVE_TO_SLIGHT_RIGHT_TURN;
			break;
		case "left-turn":
			sendint[6] = MOVE_TO_SLIGHT_LEFT_TURN;
			break;
		}
		sendint[7] = makeCRC(sendint);
		return makeintTypeString(sendint);

	}

	private int makeCRC(int[] data) {
		return 0;
	}

	private String makeintTypeString(int[] sendint) {
		String returnStr = "";
		for (int b : sendint) {
			returnStr = returnStr + String.format("%02X", b);
		}
		return returnStr;
	}
}
