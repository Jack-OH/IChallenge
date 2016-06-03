package com.lge.simple.WMSserver.Model;

import java.util.ArrayList;

public class Clients {

	ArrayList<Client> clients = new ArrayList<Client>();
	// Dictionary<Integer, Client> clients = new Hashtable<Integer, Client>();

	int clientkey;

	public Clients() {
		clientkey = 0;
	}

	public void put(Client c) {
		clients.add(c);
		c.setClientkey(clientkey);
		clientkey++;
	}

	public int getNextClientKey() {
		return clientkey;
	}

	public Client get(int key) {
		for (Client c : clients) {
			if (c.getClientkey() == key)
				return c;
		}
		return null;
	}

	public ArrayList<Client> getClientList() {
		return clients;
	}

	public boolean remove(Client c) {
		clients.remove(c);
		return true;
	}

	public int getClientkeyByClientID(String clientid) {
		for (Client c : clients) {
			if (c != null &&
					c.getClientID() != null  && 
					c.getClientID().equals(clientid))
				return c.getClientkey();
		}
		return -1;
	}

	public Client getClientByClientID(String clientid) {
		//System.out.println("GET REQ ID : " + clientid);
		try {
			for (Client c : clients) {
//				if (c != null)
//				{System.out.println("ID : " + c.getClientID() + " / KEY : " + c.getClientkey());
//				}
//				else
//					System.out.println("Client is Null");
				if (c != null &&
						c.getClientID() != null && 
						c.getClientID().equals(clientid))
					return c;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
