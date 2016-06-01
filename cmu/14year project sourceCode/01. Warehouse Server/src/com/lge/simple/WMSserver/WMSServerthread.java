package com.lge.simple.WMSserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.lge.simple.WMSserver.EventManager.CClientEventManager;
import com.lge.simple.WMSserver.EventManager.ClientEventManager;
import com.lge.simple.WMSserver.EventManager.RClientEventManager;
import com.lge.simple.WMSserver.EventManager.SClientEventManager;
import com.lge.simple.WMSserver.EventManager.WMSCClientEventManager;
import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.Clients;
import com.lge.simple.WMSserver.MsgHandler.CClientMsgHandler;
import com.lge.simple.WMSserver.MsgHandler.ClientMsgHandler;
import com.lge.simple.WMSserver.MsgHandler.RClientMsgHandler;
import com.lge.simple.WMSserver.MsgHandler.SClientMsgHandler;
import com.lge.simple.WMSserver.MsgHandler.WMSCClientMsgHandler;

public class WMSServerthread extends Thread {

	public Clients clients;
	private ClientMsgHandler msgHandler;
	private ClientEventManager evtManager;

	private int port;

	private ServerSocket ss;

	public WMSServerthread(int port) {
		clients = new Clients();
		this.port = port;
		if (port == WMSServer.CustomerPort) {
			this.msgHandler = CClientMsgHandler.getInstance();
			this.evtManager = CClientEventManager.getInstance();
		} else if (port == WMSServer.SupervisorPort) {
			this.msgHandler = SClientMsgHandler.getInstance();
			this.evtManager = SClientEventManager.getInstance();
		} else if (port == WMSServer.WMSControllerPort) {
			this.msgHandler = WMSCClientMsgHandler.getInstance();
			this.evtManager = WMSCClientEventManager.getInstance();
		} else {
			this.msgHandler = RClientMsgHandler.getInstance();
			this.evtManager = RClientEventManager.getInstance();
		}
	}

	private void ready() {
		try {
			ss = new ServerSocket(port);
			System.out.println("0.ServerSocket(" + port + ") ready...");
			int clientkey;
			while (true) {
				Socket s = ss.accept();
				System.out.println("** Socket accept... IP :" + s.getInetAddress().toString() + ":" + s.getLocalPort());
				Object os;
				Object is;
				if (port == WMSServer.CustomerPort || port == WMSServer.SupervisorPort){
					os = new DataOutputStream(
						s.getOutputStream());
					is= new DataInputStream(
						s.getInputStream());
				}
				else{
					os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		    		is = new BufferedReader( new InputStreamReader( s.getInputStream()));		    		 
				}
					
				clientkey = clients.getNextClientKey();

				clients.put(new Client(s, os, is));
				
				ListenerThread listenerthread = new ListenerThread(clients,
						clientkey, msgHandler, evtManager);

				listenerthread.start();

			}
		} catch (Exception e) {
			System.out.println("0.Exception (ready): " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() {
		ready();
	}
}
