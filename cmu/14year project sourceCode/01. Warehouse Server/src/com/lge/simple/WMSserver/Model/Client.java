package com.lge.simple.WMSserver.Model;

import java.net.Socket;

public class Client {
	private Socket s;
	private Object os;
	private Object is;
	private String clientID;
	private int clientkey;

	public int getClientkey() {
		return clientkey;
	}

	public void setClientkey(int clientkey) {
		this.clientkey = clientkey;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
		//System.out.println(clientID);
	}

	long lastMessageExchangeTime;

	public long getLastMessageExchangeTime() {
		return lastMessageExchangeTime;
	}

	public void setLastMessageExchangeTime(long lastMessageExchangeTime) {
		this.lastMessageExchangeTime = lastMessageExchangeTime;
	}

	public Client(Socket s, Object os, Object is) {
		this.s = s;
		this.os = os;
		this.is = is;
		lastMessageExchangeTime = System.currentTimeMillis();
	}

	public Socket getSocket() {
		return s;
	}

	public Object getOs() {
		return os;
	}

	public Object getIs() {
		return is;
	}
}
