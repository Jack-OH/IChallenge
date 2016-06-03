package com.lge.simple.WMSserver;

import com.lge.simple.WMSserver.EventManager.ClientEventManager;
import com.lge.simple.WMSserver.Model.Clients;

public class ServerManagerThread extends Thread {

	ClientEventManager cManager;
	int clientkey;
	//WMSServerthread wmsServerthread;
	Clients clients;
	public ServerManagerThread()
	{
	
	}

	public ServerManagerThread (Clients clients, int clientkey,ClientEventManager cManager) {
			//WMSServerthread wmsServerthread, 
		// this.oos = getOosInClients(clientkey);
		this.clientkey = clientkey;
		//this.wmsServerthread = wmsServerthread;
		this.cManager = cManager;
		this.clients = clients;

	}

	public void run() {
		try{
			while (!Thread.currentThread().isInterrupted()) {
			 	if (clients.get(clientkey).getLastMessageExchangeTime() + 5000 > System
						.currentTimeMillis()) { // 500ms �� ����� ���� ���
												// ���� ���ᳪ �̷��� ���߿� todo
					clients.get(clientkey).setLastMessageExchangeTime(System.currentTimeMillis());
				} else {
					cManager.ManageEvent(clients, clientkey);
				}

				try {
					sleep(10000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

			}
		}catch (Exception e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}finally 
		{
		
		}
	}
}
