package com.lge.simple.WMSserver;

import java.io.BufferedReader;
import java.io.DataInputStream; 

import com.lge.simple.WMSserver.EventManager.ClientEventManager;
import com.lge.simple.WMSserver.Model.Clients;
import com.lge.simple.WMSserver.MsgHandler.ClientMsgHandler;
import com.lge.simple.WMSserver.Utility; 

class ListenerThread extends Thread {
	Object is;
	int clientkey;
	ClientMsgHandler msgHandler;
	Clients clients;
	ServerManagerThread serverManagerThread;
	public ListenerThread(Clients clients, int clientkey,
			ClientMsgHandler msgHandler, ClientEventManager evtManager) {
		this.clients = clients;
		is = clients.get(clientkey).getIs();
		this.clientkey = clientkey;
		this.msgHandler = msgHandler; 
		this.serverManagerThread = new ServerManagerThread(clients, clientkey, evtManager);
	}

	public void run() {
		try {
			serverManagerThread.start();
			while (true) {
				//System.out.println("waitmsg");
				String recvmsg = "";
				if (is instanceof DataInputStream)
				  recvmsg = ((DataInputStream) is).readUTF();
				else
					recvmsg = ((BufferedReader) is).readLine();

				//System.out.println("recvmsg");
				clients.get(clientkey).setLastMessageExchangeTime(
						System.currentTimeMillis());
				//System.out.println(recvmsg);

				String response = msgHandler.HandleMessage(recvmsg, clients.get(clientkey));
				if (response != "") {
					Utility.unicast(clients.get(clientkey), response);
//					Utility.broadcast("Server did one single job", clients);
				}
			}
		} catch (Exception e) {
			//System.out.println(this.toString());
			//System.out.println(e.toString());
			//e.printStackTrace();
			this.serverManagerThread.interrupt();
			Utility.removeUser(clients, clientkey);
		}
	}

}
