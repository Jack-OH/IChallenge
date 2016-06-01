package com.lge.simple.jsonclient;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class RobotTestClient implements ActionListener {
	String ip = "127.0.0.1";// "192.168.10.41";
	int port = 9003;

	int seq = 1;

	ObjectInputStream ois;
	ObjectOutputStream oos;
	BufferedWriter out;
	BufferedReader in;
	JFrame f;
	JTextArea ta;
	JTextPane labelMac, labelWMSID, labelRobotID, labelDESC;
	JScrollPane sp;
	JPanel p, bottomPanel;
	JButton bs, be, btnConnect, btnC_READY, btnC_ARRIVED, btnC_COMPELTEJOB,
			btnC_ERROR, btnC_PING;
	JTextField textfieldMAC, textfieldWMSID, textfieldRobotID, textfieldDESC,
			textfieldIP, textfieldPort;
	private Socket s;

	public RobotTestClient() {
		f = new JFrame("Robot Test Client");
		ta = new JTextArea();
		sp = new JScrollPane(ta);
		p = new JPanel();
		bottomPanel = new JPanel();
		bs = new JButton("Send");
		be = new JButton("Exit");
		labelMac = new JTextPane();
		labelMac.setText("MAC");
		labelWMSID = new JTextPane();
		labelWMSID.setText("WMS ID");
		labelRobotID = new JTextPane();
		labelRobotID.setText("Robot ID");
		labelDESC = new JTextPane();
		labelDESC.setText("Description");
		btnC_READY = new JButton("Robot : Ready");
		btnC_ARRIVED = new JButton("Robot : Notify");
		btnC_ERROR = new JButton("Robot : Send Error");
		btnC_PING = new JButton("Robot : Send Ping");

		btnConnect = new JButton("Connect");
		textfieldMAC = new JTextField("3885010EC478");
		textfieldWMSID = new JTextField("1");
		textfieldRobotID = new JTextField("1");
		textfieldDESC = new JTextField();
		textfieldIP = new JTextField(ip.toString());
		textfieldPort = new JTextField(Integer.toString(port));
	}

	public void connect(String ip, int port) {
		try {
			append("Connect to " + ip + ":" + port);
			s = new Socket(ip, port);
			out = new BufferedWriter(
					new OutputStreamWriter(s.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			new ChatClientThread().start();
			append("3.Thread ready");
		} catch (Exception e) {
			append("����IP, PORT�� Ȯ���ϼ���");
		}
	}

	class ChatClientThread extends Thread {
		String msg;

		public void run() {
			try {
				while ((msg = in.readLine()) != null) {
					if (msg.substring(0, 6).equals("000111")) {
						out.write(msg);
						out.newLine();
						out.flush();
					}
					// System.out.println(msg);
					append(msg);
				}
			} catch (Exception e) {
				append("�޼��� �б� ����");
			}
		}
	}

	public void display() {
		bs.addActionListener(this);
		textfieldWMSID.addActionListener(this);
		be.addActionListener(this);
		btnConnect.addActionListener(this);
		btnC_ARRIVED.addActionListener(this);

		btnC_ERROR.addActionListener(this);
		btnC_PING.addActionListener(this);
		btnC_READY.addActionListener(this);

		p.setLayout(new GridLayout(10, 1));
		p.add(textfieldIP);
		p.add(textfieldPort);
		p.add(btnConnect);
		p.add(btnC_READY);
		p.add(btnC_ARRIVED);

		p.add(btnC_ERROR);
		p.add(btnC_PING);
		p.add(bs);
		p.add(be);

		ta.setFocusable(false);

		bottomPanel.setLayout(new GridLayout(4, 2));
		bottomPanel.add(labelMac);
		bottomPanel.add(textfieldMAC);
		bottomPanel.add(labelWMSID);
		bottomPanel.add(textfieldWMSID);
		bottomPanel.add(labelRobotID);
		bottomPanel.add(textfieldRobotID);
		bottomPanel.add(labelDESC);
		bottomPanel.add(textfieldDESC);

		textfieldWMSID.requestFocus();

		f.getContentPane().add(sp, BorderLayout.CENTER);
		f.getContentPane().add(p, BorderLayout.EAST);
		f.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		// f.getContentPane().add(textfieldIP, BorderLayout.SOUTH);
		// f.getContentPane().add(textfieldPort, BorderLayout.SOUTH);

		f.setSize(400, 300);
		f.setVisible(true);
	}

	public void append(String msg) {
		ta.append(msg + "'\n'");
		ta.setCaretPosition(ta.getText().length());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == be)
			System.exit(0);
		else if (e.getSource() == btnConnect) {
			ip = textfieldIP.getText();
			port = Integer.parseInt(textfieldPort.getText());

			connect(ip, port);
		} else if (e.getSource() == bs) {
			String msg = textfieldDESC.getText();
			try {
				out.write(msg, 0, msg.length());
				out.newLine();
				// oos.writeObject(obj.toJSONString());
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (e.getSource() == btnC_READY) {
			String msg = String.format("%02X", seq++);
			msg = msg + "021607" + textfieldMAC.getText() + "00";
			try {
				// System.out.println(msg);
				out.write(msg, 0, msg.length());
				out.newLine();
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (e.getSource() == btnC_ARRIVED) {
			String msg = String.format("%02X", seq++);
			msg = msg
					+ "021204"
					+ String.format("%02X",
							Integer.valueOf(textfieldWMSID.getText()))
					+ String.format("%02X",
							Integer.valueOf(textfieldRobotID.getText()))
					+ String.format("%02X",
							Integer.valueOf(textfieldDESC.getText()))

					+ "00";
			try {
				out.write(msg, 0, msg.length());
				out.newLine();
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (e.getSource() == btnC_ERROR) {
			String msg = String.format("%02X", seq++);
			msg = msg
					+ "021303"
					+ String.format("%02X",
							Integer.valueOf(textfieldWMSID.getText()))

					+ String.format("%02X",
							Integer.valueOf(textfieldRobotID.getText()))
					+ String.format("%02X",
							Integer.valueOf(textfieldDESC.getText())) + "00";
			try {
				out.write(msg, 0, msg.length());
				out.newLine();
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (e.getSource() == btnC_PING) {
			String msg = String.format("%02X", seq++);
			msg = msg
					+ "021803"
					+ String.format("%02X",
							Integer.valueOf(textfieldWMSID.getText()))
					+ String.format("%02X",
							Integer.valueOf(textfieldRobotID.getText()))

					+ "00";
			try {
				out.write(msg, 0, msg.length());
				out.newLine();
				out.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		// textfieldWMSID.setText("");
	}

	public static void main(String[] args) {
		new RobotTestClient().display();
	}
}
