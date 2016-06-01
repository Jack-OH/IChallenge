package com.lge.simple.jsonclient;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/*
import java.util.Dictionary;
import java.util.Hashtable;*/

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import org.json.simple.JSONObject;

public class Client_TestClient implements ActionListener{
	String ip="127.0.0.1";//"192.168.10.41";
	int port=9000;
	 
	ObjectInputStream  ois;
	ObjectOutputStream oos;
	
	JFrame f;
	JTextArea ta;
	JScrollPane sp;
	JPanel p,bottomPanel;
	JButton bs, be, btnConnect;
	JTextField textfieldMsg,textfieldIP, textfieldPort
		,textfieldCMD, textfieldSender;
	private Socket s;
	
	public Client_TestClient(){
		f=new JFrame("Test Client");
		ta=new JTextArea();
		sp=new JScrollPane(ta);
		p=new JPanel();
		bottomPanel = new JPanel();
		bs=new JButton("Send");
		be=new JButton("Exit");
		btnConnect = new JButton("Connect");
		textfieldMsg=new JTextField();
		textfieldSender = new JTextField();
		textfieldCMD = new JTextField();
		textfieldIP = new JTextField(ip.toString());
		textfieldPort = new JTextField(Integer.toString(port));
	}
	public void connect(String ip,int port){
		try{
			append("Connect to " + ip +":"+port);
			s = new Socket(ip, port);
			append("1.Socket����");
			ois=new ObjectInputStream(s.getInputStream());
			oos=new ObjectOutputStream(s.getOutputStream());
			append("2.Stream����");
			new ChatClientThread().start();
			append("3.ChatClientThread����");
		}catch(Exception e){
			append("����IP, PORT�� Ȯ���ϼ���");
		}
	}
	class ChatClientThread extends Thread{
		public void run(){
			try{
				while(true){
					String msg=(String)ois.readObject();
					System.out.println(msg);
					append(msg);
				}
			}catch(Exception e) {
				append("�޼��� �б� ����");
			}
		}
	}
	public void display(){
		bs.addActionListener(this);
		textfieldMsg.addActionListener(this);
		be.addActionListener(this);
		btnConnect.addActionListener(this);
		
		
		
		p.setLayout(new GridLayout(5, 1));
		p.add(textfieldIP);
		p.add(textfieldPort);
		p.add(btnConnect);
		p.add(bs);
		p.add(be);
		
		ta.setFocusable(false);
		
		bottomPanel.setLayout(new GridLayout(3, 1));
		bottomPanel.add(textfieldSender);
		bottomPanel.add(textfieldCMD);
		bottomPanel.add(textfieldMsg);
		
		textfieldMsg.requestFocus();
		
		f.getContentPane().add(sp, BorderLayout.CENTER);
		f.getContentPane().add(p, BorderLayout.EAST);
		f.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		//f.getContentPane().add(textfieldIP, BorderLayout.SOUTH);
		//f.getContentPane().add(textfieldPort, BorderLayout.SOUTH);
		
		f.setBounds(200, 200, 500, 400);
		f.setVisible(true);
	}
	public void append(String msg){
		ta.append(msg+"\n");
		ta.setCaretPosition(ta.getText().length());
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==be) 
			System.exit(0);
		else if (e.getSource() == btnConnect)
		{
			ip = textfieldIP.getText();
			port  = Integer.parseInt(textfieldPort.getText());
			
			connect(ip,port);
 		}
		else if (e.getSource() == bs)
		{
			//String cmd = textfieldCMD.getText();
			String sender = textfieldSender.getText();
			//String msg=textfieldMsg.getText();
			try{/*
				JSONObject obj;
	
				obj = new JSONObject();

				obj.put("action", "Customer.makeOrder");
				obj.put("userId", "DJ");
				Dictionary <String, Object> d =  new Hashtable <String,Object>();
				d.put("base", 12);
				d.put("basket", 15);
				obj.put("orderInfo", d);
*/				
				oos.writeObject(sender);
				oos.flush();
			}catch(IOException ioe){
				append("�޼��� ���� ����");
			}
		}
		textfieldMsg.setText("");
	}
	public static void main(String[] args) {
		new Client_TestClient().display();
	}
}
