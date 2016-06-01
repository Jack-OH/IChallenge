package com.lge.simple.WMSserver;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException; 
import java.util.ArrayList;

import com.lge.simple.WMSserver.Model.Client;
import com.lge.simple.WMSserver.Model.Clients;

public class Utility {
	public static void broadcast(String msg, Clients clients) {

		ArrayList<Client> clientlist = clients.getClientList();

		try {
			for (Client c : clientlist) {
				System.out.println(msg);
				System.out.println(c.toString());

				Object os = c.getOs();
				if (os instanceof DataOutputStream) {					
					if (((DataOutputStream) os) != null) {
						((DataOutputStream) os).writeUTF(msg);
						((DataOutputStream) os).flush();
					}
				} else
				{ 
					if (((BufferedWriter) os) != null) {
						((BufferedWriter) os).write(msg);
						((BufferedWriter) os).flush();
					}
				}
				c.setLastMessageExchangeTime(System.currentTimeMillis());
			}
		} catch (IOException e) {
			System.out.println("0.(broadcast): " + e.getMessage());
		}

	}

	public static void unicast(Client client, String msg) {
		try {
			Object os = client.getOs();
			if (os instanceof DataOutputStream) {
				if ((DataOutputStream) os != null) {
					((DataOutputStream) os).writeUTF(msg);
					((DataOutputStream) os).flush();
				}
			} else
			{ 
				if (((BufferedWriter) os) != null) {
//					msg = msg + "\n";
					((BufferedWriter) os).write(msg);
					((BufferedWriter) os).newLine();
					((BufferedWriter) os).flush();
				}
			}
			client.setLastMessageExchangeTime(System.currentTimeMillis());
		} catch (IOException e) {
			System.out.println("0.(Unicast): " + e.getMessage());
		}
	}

	public static void removeUser(Clients clients, int clientkey) {
		try {
			System.out.println("** Closing... IP :"+clients.get(clientkey).getSocket().getInetAddress().toString()
					+":" + clients.get(clientkey).getSocket().getLocalPort());
			clients.get(clientkey).getSocket().close();
			clients.remove(clients.get(clientkey));
		} catch (IOException e) {

		}
	}
}
