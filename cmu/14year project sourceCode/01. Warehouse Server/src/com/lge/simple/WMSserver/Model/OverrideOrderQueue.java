package com.lge.simple.WMSserver.Model;

import java.util.LinkedList;
import java.util.Queue;

public class OverrideOrderQueue {
	private static OverrideOrderQueue instance = new OverrideOrderQueue();
	private OverrideOrderQueue() {
 	}	
	public static OverrideOrderQueue getInstance() {
		return instance;
	}
	public Queue<String> command = new LinkedList<String>(); 
}
