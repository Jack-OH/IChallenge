package com.lge.simple.WMSserver.MsgHandler;

import com.lge.simple.WMSserver.Model.Client;

public abstract class ClientMsgHandler {
	public abstract String HandleMessage(String recvString, Client client);
}
